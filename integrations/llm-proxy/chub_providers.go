// chub_providers.go
// LLM-Proxy extension for Chub AI provider support
// This file extends llm-proxy to handle Chub AI's multi-provider architecture

package main

import (
	"net/url"
	"strings"
)

// ChubProvider represents a Chub AI backend provider
type ChubProvider struct {
	Name     string
	BaseURL  string
	Tier     string // "mars", "mercury", or "custom"
	APIPath  string
}

// ChubProviders maps provider names to their configurations
var ChubProviders = map[string]ChubProvider{
	"chub-mars-asha": {
		Name:    "Mars Asha",
		BaseURL: "https://mars.chub.ai",
		Tier:    "mars",
		APIPath: "/chub/asha/v1",
	},
	"chub-mars-mixtral": {
		Name:    "Mars Mixtral",
		BaseURL: "https://mars.chub.ai",
		Tier:    "mars",
		APIPath: "/mixtral/v1",
	},
	"chub-mercury-mistral": {
		Name:    "Mercury Mistral",
		BaseURL: "https://mercury.chub.ai",
		Tier:    "mercury",
		APIPath: "/mistral/v1",
	},
	"chub-mercury-mythomax": {
		Name:    "Mercury MythoMax",
		BaseURL: "https://mercury.chub.ai",
		Tier:    "mercury",
		APIPath: "/mythomax/v1",
	},
	"chub-proxy": {
		Name:    "Chub Proxy",
		BaseURL: "https://proxy.chub.ai",
		Tier:    "proxy",
		APIPath: "/v1",
	},
}

// ParseChubURL parses a Chub AI proxy URL and returns the provider and path
func ParseChubURL(rawURL string) (*ChubProvider, string, error) {
	parsed, err := url.Parse(rawURL)
	if err != nil {
		return nil, "", err
	}

	// Check if this is a Chub AI URL
	host := strings.ToLower(parsed.Host)
	if !strings.Contains(host, "chub.ai") {
		return nil, "", nil // Not a Chub URL
	}

	// Determine provider based on host and path
	for name, provider := range ChubProviders {
		if strings.Contains(host, strings.Split(provider.BaseURL, "://")[1]) {
			if strings.HasPrefix(parsed.Path, provider.APIPath) {
				remainingPath := strings.TrimPrefix(parsed.Path, provider.APIPath)
				return &provider, remainingPath, nil
			}
		}
	}

	// Default to proxy provider
	proxyProvider := ChubProviders["chub-proxy"]
	return &proxyProvider, parsed.Path, nil
}

// IsChubConversationEndpoint checks if the path is a Chub AI conversation endpoint
func IsChubConversationEndpoint(path string) bool {
	conversationPaths := []string{
		"/chat/completions",
		"/completions",
		"/messages",
		"/api/core/chats",
		"/chub/asha/v1/chat",
		"/mixtral/v1/chat",
		"/mistral/v1/chat",
	}

	for _, convPath := range conversationPaths {
		if strings.Contains(path, convPath) {
			return true
		}
	}
	return false
}

// ChubSessionFingerprint generates a session fingerprint for Chub AI requests
type ChubSessionFingerprint struct {
	CharacterID string `json:"character_id,omitempty"`
	ChatID      string `json:"chat_id,omitempty"`
	PersonaID   string `json:"persona_id,omitempty"`
	UserID      string `json:"user_id,omitempty"`
	Tier        string `json:"tier"`
}

// ExtractChubFingerprint extracts session fingerprint from Chub AI request
func ExtractChubFingerprint(headers map[string]string, body map[string]interface{}) *ChubSessionFingerprint {
	fp := &ChubSessionFingerprint{}

	// Extract from headers
	if auth, ok := headers["Authorization"]; ok {
		// Parse JWT or API key to get user info
		fp.UserID = extractUserFromAuth(auth)
	}

	// Extract from body
	if charID, ok := body["character_id"].(string); ok {
		fp.CharacterID = charID
	}
	if chatID, ok := body["chat_id"].(string); ok {
		fp.ChatID = chatID
	}
	if personaID, ok := body["persona_id"].(string); ok {
		fp.PersonaID = personaID
	}

	// Determine tier from model name
	if model, ok := body["model"].(string); ok {
		fp.Tier = inferTierFromModel(model)
	}

	return fp
}

func extractUserFromAuth(auth string) string {
	// Simplified - in production, decode JWT
	if strings.HasPrefix(auth, "Bearer ") {
		token := strings.TrimPrefix(auth, "Bearer ")
		if len(token) > 8 {
			return token[:8] + "..."
		}
	}
	return "anonymous"
}

func inferTierFromModel(model string) string {
	model = strings.ToLower(model)
	if strings.Contains(model, "asha") || strings.Contains(model, "70b") {
		return "mars"
	}
	if strings.Contains(model, "mixtral") {
		return "mars"
	}
	if strings.Contains(model, "mistral") || strings.Contains(model, "7b") {
		return "mercury"
	}
	if strings.Contains(model, "mythomax") {
		return "mercury"
	}
	return "unknown"
}

// ChubLogEntry extends the base log entry with Chub-specific fields
type ChubLogEntry struct {
	Timestamp   string                  `json:"timestamp"`
	Provider    string                  `json:"provider"`
	Tier        string                  `json:"tier"`
	Fingerprint *ChubSessionFingerprint `json:"fingerprint"`
	Request     interface{}             `json:"request"`
	Response    interface{}             `json:"response,omitempty"`
	Latency     int64                   `json:"latency_ms"`
	TokensIn    int                     `json:"tokens_in,omitempty"`
	TokensOut   int                     `json:"tokens_out,omitempty"`
	Error       string                  `json:"error,omitempty"`
}
