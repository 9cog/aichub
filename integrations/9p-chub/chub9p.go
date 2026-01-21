// chub9p.go
// 9P File Server for Chub AI
// Exposes Chub AI resources as a Plan 9 file system

package main

import (
	"encoding/json"
	"fmt"
	"io"
	"log"
	"net"
	"net/http"
	"strings"
	"sync"
	"time"

	"github.com/lionkov/go9p/p"
	"github.com/lionkov/go9p/p/srv"
)

const (
	CHUB_API_BASE = "https://api.chub.ai"
)

// ChubFS implements the 9P file server for Chub AI
type ChubFS struct {
	srv.Fsrv
	root      *ChubDir
	apiKey    string
	client    *http.Client
	cache     *ChubCache
	cacheLock sync.RWMutex
}

// ChubCache stores cached API responses
type ChubCache struct {
	Characters map[string]*Character
	Lorebooks  map[string]*Lorebook
	Sessions   map[string]*Session
	TTL        time.Duration
}

// Character represents a Chub AI character
type Character struct {
	ID          string `json:"id"`
	Name        string `json:"name"`
	Description string `json:"description"`
	Personality string `json:"personality"`
	Scenario    string `json:"scenario"`
	FirstMes    string `json:"first_mes"`
	AvatarURL   string `json:"avatar_url"`
	LorebookID  string `json:"lorebook_id,omitempty"`
	UpdatedAt   time.Time
}

// Lorebook represents a Chub AI lorebook
type Lorebook struct {
	ID          string          `json:"id"`
	Name        string          `json:"name"`
	Description string          `json:"description"`
	Entries     []LorebookEntry `json:"entries"`
	UpdatedAt   time.Time
}

// LorebookEntry represents a lorebook entry
type LorebookEntry struct {
	ID       string   `json:"id"`
	Keys     []string `json:"keys"`
	Content  string   `json:"content"`
	Priority int      `json:"priority"`
	Depth    int      `json:"depth"`
	Enabled  bool     `json:"enabled"`
}

// Session represents a chat session
type Session struct {
	ID          string    `json:"id"`
	CharacterID string    `json:"character_id"`
	Messages    []Message `json:"messages"`
	CreatedAt   time.Time
	UpdatedAt   time.Time
}

// Message represents a chat message
type Message struct {
	Role    string `json:"role"`
	Content string `json:"content"`
	Time    time.Time
}

// ChubDir represents a directory in the Chub 9P filesystem
type ChubDir struct {
	srv.File
	fs       *ChubFS
	path     string
	children map[string]*ChubFile
}

// ChubFile represents a file in the Chub 9P filesystem
type ChubFile struct {
	srv.File
	fs      *ChubFS
	path    string
	content func() ([]byte, error)
	write   func([]byte) error
}

// NewChubFS creates a new Chub AI 9P file server
func NewChubFS(apiKey string) *ChubFS {
	fs := &ChubFS{
		apiKey: apiKey,
		client: &http.Client{Timeout: 30 * time.Second},
		cache: &ChubCache{
			Characters: make(map[string]*Character),
			Lorebooks:  make(map[string]*Lorebook),
			Sessions:   make(map[string]*Session),
			TTL:        5 * time.Minute,
		},
	}

	// Create root directory
	fs.root = &ChubDir{
		fs:       fs,
		path:     "/",
		children: make(map[string]*ChubFile),
	}

	return fs
}

// Attach handles the 9P attach request
func (fs *ChubFS) Attach(req *srv.Req) {
	if req.Afid != nil {
		req.RespondError(srv.Enoauth)
		return
	}

	// Create root Qid
	qid := p.Qid{
		Type:    p.QTDIR,
		Version: 0,
		Path:    0,
	}

	req.Fid.Aux = fs.root
	req.RespondRattach(&qid)
}

// Walk handles the 9P walk request
func (fs *ChubFS) Walk(req *srv.Req) {
	fid := req.Fid.Aux.(*ChubDir)
	tc := req.Tc

	if len(tc.Wname) == 0 {
		req.Fid.Aux = fid
		req.RespondRwalk(nil)
		return
	}

	qids := make([]p.Qid, 0, len(tc.Wname))
	current := fid

	for _, name := range tc.Wname {
		qid, next, err := fs.walkOne(current, name)
		if err != nil {
			if len(qids) == 0 {
				req.RespondError(err)
				return
			}
			break
		}
		qids = append(qids, *qid)
		current = next
	}

	req.Newfid.Aux = current
	req.RespondRwalk(qids)
}

func (fs *ChubFS) walkOne(dir *ChubDir, name string) (*p.Qid, *ChubDir, error) {
	path := dir.path + "/" + name
	if dir.path == "/" {
		path = "/" + name
	}

	// Handle virtual directories
	switch {
	case path == "/characters":
		return fs.walkCharacters()
	case path == "/lorebooks":
		return fs.walkLorebooks()
	case path == "/sessions":
		return fs.walkSessions()
	case path == "/ctl":
		return fs.walkCtl()
	case strings.HasPrefix(path, "/characters/"):
		return fs.walkCharacter(path)
	case strings.HasPrefix(path, "/lorebooks/"):
		return fs.walkLorebook(path)
	case strings.HasPrefix(path, "/sessions/"):
		return fs.walkSession(path)
	}

	return nil, nil, srv.Enoent
}

func (fs *ChubFS) walkCharacters() (*p.Qid, *ChubDir, error) {
	qid := &p.Qid{Type: p.QTDIR, Path: 1}
	dir := &ChubDir{fs: fs, path: "/characters", children: make(map[string]*ChubFile)}
	return qid, dir, nil
}

func (fs *ChubFS) walkLorebooks() (*p.Qid, *ChubDir, error) {
	qid := &p.Qid{Type: p.QTDIR, Path: 2}
	dir := &ChubDir{fs: fs, path: "/lorebooks", children: make(map[string]*ChubFile)}
	return qid, dir, nil
}

func (fs *ChubFS) walkSessions() (*p.Qid, *ChubDir, error) {
	qid := &p.Qid{Type: p.QTDIR, Path: 3}
	dir := &ChubDir{fs: fs, path: "/sessions", children: make(map[string]*ChubFile)}
	return qid, dir, nil
}

func (fs *ChubFS) walkCtl() (*p.Qid, *ChubDir, error) {
	qid := &p.Qid{Type: p.QTFILE, Path: 4}
	dir := &ChubDir{fs: fs, path: "/ctl", children: make(map[string]*ChubFile)}
	return qid, dir, nil
}

func (fs *ChubFS) walkCharacter(path string) (*p.Qid, *ChubDir, error) {
	parts := strings.Split(path, "/")
	if len(parts) < 3 {
		return nil, nil, srv.Enoent
	}

	charID := parts[2]
	
	// Fetch character from cache or API
	char, err := fs.getCharacter(charID)
	if err != nil {
		return nil, nil, srv.Enoent
	}

	if len(parts) == 3 {
		// Character directory
		qid := &p.Qid{Type: p.QTDIR, Path: hashPath(path)}
		dir := &ChubDir{fs: fs, path: path, children: make(map[string]*ChubFile)}
		return qid, dir, nil
	}

	// Character file
	fileName := parts[3]
	content, err := fs.getCharacterFile(char, fileName)
	if err != nil {
		return nil, nil, srv.Enoent
	}

	qid := &p.Qid{Type: p.QTFILE, Path: hashPath(path)}
	dir := &ChubDir{fs: fs, path: path, children: map[string]*ChubFile{
		fileName: {
			fs:   fs,
			path: path,
			content: func() ([]byte, error) {
				return content, nil
			},
		},
	}}
	return qid, dir, nil
}

func (fs *ChubFS) walkLorebook(path string) (*p.Qid, *ChubDir, error) {
	// Similar implementation for lorebooks
	qid := &p.Qid{Type: p.QTDIR, Path: hashPath(path)}
	dir := &ChubDir{fs: fs, path: path, children: make(map[string]*ChubFile)}
	return qid, dir, nil
}

func (fs *ChubFS) walkSession(path string) (*p.Qid, *ChubDir, error) {
	// Similar implementation for sessions
	qid := &p.Qid{Type: p.QTDIR, Path: hashPath(path)}
	dir := &ChubDir{fs: fs, path: path, children: make(map[string]*ChubFile)}
	return qid, dir, nil
}

// Read handles the 9P read request
func (fs *ChubFS) Read(req *srv.Req) {
	fid := req.Fid.Aux
	tc := req.Tc

	switch f := fid.(type) {
	case *ChubDir:
		if f.path == "/" {
			// Root directory listing
			data := fs.readRootDir()
			req.RespondRread(data)
			return
		}
		// Handle directory reads
		data, err := fs.readDir(f.path)
		if err != nil {
			req.RespondError(err)
			return
		}
		req.RespondRread(data[tc.Offset:])

	case *ChubFile:
		data, err := f.content()
		if err != nil {
			req.RespondError(srv.Eio)
			return
		}
		if tc.Offset >= uint64(len(data)) {
			req.RespondRread(nil)
			return
		}
		req.RespondRread(data[tc.Offset:])
	}
}

// Write handles the 9P write request
func (fs *ChubFS) Write(req *srv.Req) {
	fid := req.Fid.Aux
	tc := req.Tc

	switch f := fid.(type) {
	case *ChubFile:
		if f.write != nil {
			err := f.write(tc.Data)
			if err != nil {
				req.RespondError(srv.Eio)
				return
			}
			req.RespondRwrite(uint32(len(tc.Data)))
			return
		}
		req.RespondError(srv.Eperm)

	default:
		req.RespondError(srv.Eperm)
	}
}

// API interaction methods

func (fs *ChubFS) getCharacter(id string) (*Character, error) {
	fs.cacheLock.RLock()
	if char, ok := fs.cache.Characters[id]; ok {
		if time.Since(char.UpdatedAt) < fs.cache.TTL {
			fs.cacheLock.RUnlock()
			return char, nil
		}
	}
	fs.cacheLock.RUnlock()

	// Fetch from API
	url := fmt.Sprintf("%s/api/core/characters/%s", CHUB_API_BASE, id)
	req, _ := http.NewRequest("GET", url, nil)
	req.Header.Set("Authorization", "Bearer "+fs.apiKey)

	resp, err := fs.client.Do(req)
	if err != nil {
		return nil, err
	}
	defer resp.Body.Close()

	if resp.StatusCode != 200 {
		return nil, fmt.Errorf("API error: %d", resp.StatusCode)
	}

	var char Character
	if err := json.NewDecoder(resp.Body).Decode(&char); err != nil {
		return nil, err
	}

	char.UpdatedAt = time.Now()

	fs.cacheLock.Lock()
	fs.cache.Characters[id] = &char
	fs.cacheLock.Unlock()

	return &char, nil
}

func (fs *ChubFS) getCharacterFile(char *Character, fileName string) ([]byte, error) {
	switch fileName {
	case "meta":
		return json.MarshalIndent(char, "", "  ")
	case "name":
		return []byte(char.Name + "\n"), nil
	case "description":
		return []byte(char.Description + "\n"), nil
	case "personality":
		return []byte(char.Personality + "\n"), nil
	case "scenario":
		return []byte(char.Scenario + "\n"), nil
	case "first_mes":
		return []byte(char.FirstMes + "\n"), nil
	case "avatar":
		return []byte(char.AvatarURL + "\n"), nil
	case "lorebook":
		return []byte(char.LorebookID + "\n"), nil
	default:
		return nil, fmt.Errorf("unknown file: %s", fileName)
	}
}

func (fs *ChubFS) readRootDir() []byte {
	entries := []string{"characters", "lorebooks", "sessions", "ctl", "config", "models"}
	return []byte(strings.Join(entries, "\n") + "\n")
}

func (fs *ChubFS) readDir(path string) ([]byte, error) {
	switch {
	case path == "/characters":
		return fs.listCharacters()
	case path == "/lorebooks":
		return fs.listLorebooks()
	case path == "/sessions":
		return fs.listSessions()
	case strings.HasPrefix(path, "/characters/"):
		return []byte("meta\nname\ndescription\npersonality\nscenario\nfirst_mes\navatar\nlorebook\n"), nil
	}
	return nil, srv.Enoent
}

func (fs *ChubFS) listCharacters() ([]byte, error) {
	// In production, this would call the API
	// For now, return cached character IDs
	fs.cacheLock.RLock()
	defer fs.cacheLock.RUnlock()

	var ids []string
	for id := range fs.cache.Characters {
		ids = append(ids, id)
	}
	return []byte(strings.Join(ids, "\n") + "\n"), nil
}

func (fs *ChubFS) listLorebooks() ([]byte, error) {
	fs.cacheLock.RLock()
	defer fs.cacheLock.RUnlock()

	var ids []string
	for id := range fs.cache.Lorebooks {
		ids = append(ids, id)
	}
	return []byte(strings.Join(ids, "\n") + "\n"), nil
}

func (fs *ChubFS) listSessions() ([]byte, error) {
	fs.cacheLock.RLock()
	defer fs.cacheLock.RUnlock()

	var ids []string
	for id := range fs.cache.Sessions {
		ids = append(ids, id)
	}
	return []byte(strings.Join(ids, "\n") + "\n"), nil
}

func hashPath(path string) uint64 {
	var h uint64 = 5381
	for _, c := range path {
		h = ((h << 5) + h) + uint64(c)
	}
	return h
}

func main() {
	apiKey := "YOUR_CHUB_API_KEY" // In production, read from env or config

	fs := NewChubFS(apiKey)

	// Listen on TCP port 564 (standard 9P port)
	listener, err := net.Listen("tcp", ":564")
	if err != nil {
		log.Fatal(err)
	}

	log.Println("Chub AI 9P server listening on :564")

	for {
		conn, err := listener.Accept()
		if err != nil {
			log.Println("Accept error:", err)
			continue
		}

		go fs.handleConnection(conn)
	}
}

func (fs *ChubFS) handleConnection(conn net.Conn) {
	defer conn.Close()

	// Create new 9P connection handler
	s := srv.NewConn(conn)
	s.Debuglevel = 0
	s.Start(fs)
}
