# Chub AI Integration Specifications

## Executive Summary

This document provides detailed integration specifications for connecting the Chub AI mobile application with four complementary systems from the `9cog` and `cogpy` ecosystems. Each integration addresses a specific capability gap and aligns with the broader AGI development platform goals of integrating Plan 9/Inferno distributed systems, OpenCog cognitive architectures, and modern AI frameworks.

---

## 1. LLM-Proxy Integration: Centralized AI Logging

### Overview

The **llm-proxy** is a transparent logging proxy for LLM API traffic. Integrating it with Chub AI's multi-provider AI Model Proxy would provide centralized logging, debugging, and analytics for all AI conversations.

### Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                    Chub AI Mobile App                           │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │              React/Vite Frontend                         │   │
│  └──────────────────────┬──────────────────────────────────┘   │
│                         │                                       │
│  ┌──────────────────────▼──────────────────────────────────┐   │
│  │              AI Model Proxy (proxy.chub.ai)             │   │
│  └──────────────────────┬──────────────────────────────────┘   │
└─────────────────────────┼───────────────────────────────────────┘
                          │
          ┌───────────────▼───────────────┐
          │        LLM-Proxy Layer        │
          │  ┌─────────────────────────┐  │
          │  │ Session Manager         │  │
          │  │ Request/Response Logger │  │
          │  │ Fingerprint Tracker     │  │
          │  └─────────────────────────┘  │
          └───────────────┬───────────────┘
                          │
    ┌─────────────────────┼─────────────────────┐
    │                     │                     │
    ▼                     ▼                     ▼
┌────────┐          ┌──────────┐          ┌──────────┐
│ OpenAI │          │Anthropic │          │OpenRouter│
└────────┘          └──────────┘          └──────────┘
```

### Integration Points

| Component | Chub AI Element | LLM-Proxy Element | Integration Method |
|-----------|-----------------|-------------------|-------------------|
| API Gateway | `proxy.chub.ai/v1` | `ParseProxyURL()` | URL path routing |
| Session Tracking | Chat sessions | `SessionManager` | Conversation fingerprinting |
| Request Logging | API calls | `LogRequest()` | JSONL file output |
| Response Logging | AI responses | `LogResponse()` | Streaming chunk capture |

### Implementation Specification

#### 1. Provider Registration

Extend `urlparse.go` to support Chub AI's custom providers:

```go
var validProviders = map[string]bool{
    "anthropic":    true,
    "openai":       true,
    "chub-mars":    true,  // Mars tier models
    "chub-mercury": true,  // Mercury tier models
    "openrouter":   true,
    "elevenlabs":   true,
}
```

#### 2. Endpoint Mapping

Add Chub AI conversation endpoints to `isConversationEndpoint()`:

```go
func isConversationEndpoint(path string) bool {
    // Existing endpoints...
    
    // Chub AI endpoints
    if strings.HasPrefix(path, "/api/core/chats") {
        return true
    }
    if strings.HasPrefix(path, "/chub/asha/v1") {
        return true
    }
    if strings.HasPrefix(path, "/mixtral/v1") {
        return true
    }
    return false
}
```

#### 3. Configuration

```toml
# llm-proxy config for Chub AI
port = 8080
log_dir = "~/.chub-ai-logs"

[providers]
chub-mars = "mars.chub.ai"
chub-mercury = "mercury.chub.ai"
chub-proxy = "proxy.chub.ai"
```

### Deployment Options

1. **Sidecar Mode**: Run llm-proxy alongside Chub AI backend
2. **Gateway Mode**: Replace `proxy.chub.ai` with llm-proxy instance
3. **Client-Side Mode**: Configure mobile app to route through local proxy

---

## 2. 9fs9rc Integration: Plan 9 Distributed Access

### Overview

The **9fs9rc** framework provides 9P protocol and rc shell integration for AI chat applications. This integration exposes Chub AI's services as a 9P file system, enabling shell-native interaction with characters, chats, and lorebooks.

### Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                    Plan 9 / Inferno Environment                 │
│                                                                 │
│   rc shell                                                      │
│   ┌──────────────────────────────────────────────────────────┐ │
│   │ % cat /ai/chub/characters/alice/personality              │ │
│   │ % echo "Hello" > /ai/chub/sessions/12345/ctl             │ │
│   │ % cat /ai/chub/sessions/12345/history                    │ │
│   └──────────────────────────────────────────────────────────┘ │
│                              │                                  │
│                    ┌─────────▼─────────┐                       │
│                    │   9P Protocol     │                       │
│                    │   (u9fs server)   │                       │
│                    └─────────┬─────────┘                       │
└──────────────────────────────┼──────────────────────────────────┘
                               │
                    ┌──────────▼──────────┐
                    │  Chub AI 9P Server  │
                    │                     │
                    │  /ai/chub/          │
                    │  ├── characters/    │
                    │  ├── lorebooks/     │
                    │  ├── sessions/      │
                    │  ├── personas/      │
                    │  └── ctl            │
                    └──────────┬──────────┘
                               │
                    ┌──────────▼──────────┐
                    │   Chub AI Backend   │
                    │   (REST API)        │
                    └─────────────────────┘
```

### File System Structure

```
/ai/chub/
├── ctl                         # Control file (commands)
├── config                      # Configuration
├── models                      # Available AI models
│
├── characters/
│   ├── {character_id}/
│   │   ├── meta               # Character metadata (JSON)
│   │   ├── name               # Character name
│   │   ├── description        # Character description
│   │   ├── personality        # Personality definition
│   │   ├── scenario           # Scenario text
│   │   ├── first_mes          # First message
│   │   ├── avatar             # Avatar URL/path
│   │   └── lorebook           # Linked lorebook ID
│   └── ...
│
├── lorebooks/
│   ├── {lorebook_id}/
│   │   ├── meta               # Lorebook metadata
│   │   ├── entries/           # Entry directory
│   │   │   ├── {entry_id}/
│   │   │   │   ├── keys       # Trigger keywords
│   │   │   │   ├── content    # Entry content
│   │   │   │   └── config     # Priority, depth, etc.
│   │   │   └── ...
│   │   └── ctl                # Lorebook control
│   └── ...
│
├── sessions/
│   ├── {session_id}/
│   │   ├── meta               # Session metadata
│   │   ├── character          # Character ID
│   │   ├── history            # Message history (append-only)
│   │   ├── context            # Current context window
│   │   └── ctl                # Session control (write to send)
│   └── ...
│
└── personas/
    ├── {persona_id}/
    │   ├── meta
    │   ├── name
    │   └── description
    └── ...
```

### 9P Operations Mapping

| 9P Operation | Chub AI Action | API Endpoint |
|--------------|----------------|--------------|
| `Twalk` to `/characters/{id}` | Load character | `GET /api/core/characters/{id}` |
| `Tread` from `/sessions/{id}/history` | Get chat history | `GET /api/core/chats/{id}` |
| `Twrite` to `/sessions/{id}/ctl` | Send message | `POST /api/core/chats/{id}/messages` |
| `Tcreate` in `/sessions/` | Start new chat | `POST /api/core/chats` |
| `Tstat` on any file | Get metadata | Various GET endpoints |

### rc Shell Integration

```bash
# Load AI chat functions
. /ai/chub/lib/rc/chub.rc

# List available characters
ls /ai/chub/characters

# Start a chat session
chub-chat alice

# Send a message
echo "Tell me a story" > /ai/chub/sessions/current/ctl

# Read response
cat /ai/chub/sessions/current/history | tail -1

# Search lorebooks
grep -l "dragon" /ai/chub/lorebooks/*/entries/*/keys
```

---

## 3. Cogn8n Integration: Workflow Automation

### Overview

**Cogn8n** is a workflow automation platform with native AI and OpenCog cognitive capabilities. This integration enables automated management of Chub AI content through visual workflows.

### Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                      Cogn8n Workflow Engine                     │
│                                                                 │
│   ┌─────────┐    ┌─────────┐    ┌─────────┐    ┌─────────┐    │
│   │ Trigger │───▶│AtomSpace│───▶│ Chub AI │───▶│ Output  │    │
│   │ (Cron)  │    │ (Query) │    │ (Node)  │    │ (Slack) │    │
│   └─────────┘    └─────────┘    └─────────┘    └─────────┘    │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
                              │
                    ┌─────────▼─────────┐
                    │   Chub AI Node    │
                    │                   │
                    │ • Character CRUD  │
                    │ • Lorebook Sync   │
                    │ • Chat Management │
                    │ • Bulk Operations │
                    └─────────┬─────────┘
                              │
                    ┌─────────▼─────────┐
                    │   Chub AI API     │
                    └───────────────────┘
```

### Custom Node Specification

#### ChubAI Node (`packages/nodes-base/nodes/ChubAI/`)

```typescript
// ChubAI.node.ts
import { INodeType, INodeTypeDescription } from 'n8n-workflow';

export class ChubAI implements INodeType {
    description: INodeTypeDescription = {
        displayName: 'Chub AI',
        name: 'chubAI',
        icon: 'file:chubai.svg',
        group: ['transform'],
        version: 1,
        subtitle: '={{$parameter["operation"]}}',
        description: 'Interact with Chub AI platform',
        defaults: {
            name: 'Chub AI',
        },
        inputs: ['main'],
        outputs: ['main'],
        credentials: [
            {
                name: 'chubAiApi',
                required: true,
            },
        ],
        properties: [
            {
                displayName: 'Resource',
                name: 'resource',
                type: 'options',
                options: [
                    { name: 'Character', value: 'character' },
                    { name: 'Lorebook', value: 'lorebook' },
                    { name: 'Chat', value: 'chat' },
                    { name: 'Persona', value: 'persona' },
                ],
                default: 'character',
            },
            {
                displayName: 'Operation',
                name: 'operation',
                type: 'options',
                displayOptions: {
                    show: { resource: ['character'] },
                },
                options: [
                    { name: 'Create', value: 'create' },
                    { name: 'Get', value: 'get' },
                    { name: 'Update', value: 'update' },
                    { name: 'Delete', value: 'delete' },
                    { name: 'List', value: 'list' },
                    { name: 'Export', value: 'export' },
                    { name: 'Import', value: 'import' },
                ],
                default: 'get',
            },
            // Additional properties...
        ],
    };
}
```

### Workflow Templates

#### 1. Character Backup Workflow

```json
{
  "name": "Chub AI Character Backup",
  "nodes": [
    {
      "name": "Schedule Trigger",
      "type": "n8n-nodes-base.scheduleTrigger",
      "parameters": {
        "rule": { "interval": [{ "field": "days", "daysInterval": 1 }] }
      }
    },
    {
      "name": "List Characters",
      "type": "n8n-nodes-base.chubAI",
      "parameters": {
        "resource": "character",
        "operation": "list",
        "returnAll": true
      }
    },
    {
      "name": "Export Each",
      "type": "n8n-nodes-base.chubAI",
      "parameters": {
        "resource": "character",
        "operation": "export",
        "format": "tavernai"
      }
    },
    {
      "name": "Save to Google Drive",
      "type": "n8n-nodes-base.googleDrive",
      "parameters": {
        "operation": "upload",
        "folderId": "chub-backups"
      }
    }
  ]
}
```

#### 2. Lorebook Synchronization Workflow

```json
{
  "name": "Lorebook Cross-Platform Sync",
  "nodes": [
    {
      "name": "Webhook Trigger",
      "type": "n8n-nodes-base.webhook",
      "parameters": { "path": "lorebook-sync" }
    },
    {
      "name": "Get Lorebook",
      "type": "n8n-nodes-base.chubAI",
      "parameters": {
        "resource": "lorebook",
        "operation": "get",
        "lorebookId": "={{$json.lorebookId}}"
      }
    },
    {
      "name": "Transform to SillyTavern",
      "type": "n8n-nodes-base.code",
      "parameters": {
        "jsCode": "// Transform Chub format to SillyTavern format"
      }
    },
    {
      "name": "Upload to SillyTavern",
      "type": "n8n-nodes-base.httpRequest",
      "parameters": {
        "url": "http://localhost:8000/api/lorebooks"
      }
    }
  ]
}
```

### OpenCog Integration

Leverage cogn8n's OpenCog nodes for cognitive processing:

```
Character Data → AtomSpace (Store) → Pattern Miner (Analyze) → Reasoning Engine → Insights
```

---

## 4. Jancog Integration: Offline AI Capabilities

### Overview

**Jancog** (Jan + OpenCog) is an open-source, offline-first AI application. This integration enables Chub AI to function without internet connectivity by using local LLM inference.

### Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                    Chub AI Mobile App                           │
│                                                                 │
│   ┌─────────────────────────────────────────────────────────┐  │
│   │                   React Frontend                         │  │
│   └──────────────────────┬──────────────────────────────────┘  │
│                          │                                      │
│   ┌──────────────────────▼──────────────────────────────────┐  │
│   │              AI Provider Router                          │  │
│   │  ┌────────────┐  ┌────────────┐  ┌────────────────────┐ │  │
│   │  │  Online    │  │  Offline   │  │  Hybrid            │ │  │
│   │  │  (Cloud)   │  │  (Local)   │  │  (Fallback)        │ │  │
│   │  └─────┬──────┘  └─────┬──────┘  └────────────────────┘ │  │
│   └────────┼───────────────┼────────────────────────────────┘  │
│            │               │                                    │
└────────────┼───────────────┼────────────────────────────────────┘
             │               │
             ▼               ▼
    ┌────────────────┐  ┌────────────────────────────────────┐
    │  Cloud APIs    │  │         Jancog Engine              │
    │  (Anthropic,   │  │  ┌──────────────────────────────┐  │
    │   OpenAI)      │  │  │       llama.cpp Backend      │  │
    │                │  │  │  ┌────────────────────────┐  │  │
    └────────────────┘  │  │  │  Local LLM Models      │  │  │
                        │  │  │  (Llama, Mistral, etc) │  │  │
                        │  │  └────────────────────────┘  │  │
                        │  └──────────────────────────────┘  │
                        │  ┌──────────────────────────────┐  │
                        │  │    OpenCog Orchestration     │  │
                        │  │    (Goal Decomposition)      │  │
                        │  └──────────────────────────────┘  │
                        └────────────────────────────────────┘
```

### Integration Approach

#### 1. OpenAI-Compatible Local API

Jancog exposes an OpenAI-compatible API at `localhost:1337`. Chub AI can route requests to this endpoint when offline:

```typescript
// Chub AI Provider Configuration
const providers = {
  online: {
    mars: 'https://mars.chub.ai/chub/asha/v1',
    mercury: 'https://mercury.chub.ai/mistral/v1',
    openai: 'https://api.openai.com/v1',
  },
  offline: {
    local: 'http://localhost:1337/v1',
  }
};

function getProvider(isOnline: boolean, tier: string) {
  if (isOnline) {
    return providers.online[tier];
  }
  return providers.offline.local;
}
```

#### 2. Model Mapping

Map Chub AI's tier models to local equivalents:

| Chub AI Model | Local Equivalent | Jancog Model ID |
|---------------|------------------|-----------------|
| Mars Asha | Llama 3.1 70B | `llama-3.1-70b-instruct` |
| Mars Mixtral | Mixtral 8x7B | `mixtral-8x7b-instruct` |
| Mercury Mistral | Mistral 7B | `mistral-7b-instruct` |
| Mercury MythoMax | MythoMax 13B | `mythomax-13b` |

#### 3. Character Card Compatibility

Both Chub AI and Jancog support TavernAI/SillyTavern character card format:

```json
{
  "name": "Alice",
  "description": "A curious adventurer...",
  "personality": "Curious, brave, kind",
  "scenario": "In a magical forest...",
  "first_mes": "Hello! I'm Alice.",
  "mes_example": "<START>{{user}}: Hi!\n{{char}}: Hello there!",
  "system_prompt": "You are Alice, a curious adventurer."
}
```

#### 4. Offline Sync Strategy

```typescript
// Sync characters for offline use
async function syncForOffline(characterIds: string[]) {
  const characters = await Promise.all(
    characterIds.map(id => chubApi.getCharacter(id))
  );
  
  // Store in Jancog's local database
  for (const char of characters) {
    await jancog.assistants.create({
      name: char.name,
      instructions: char.system_prompt,
      model: 'mistral-7b-instruct',
      metadata: {
        chub_id: char.id,
        personality: char.personality,
        scenario: char.scenario,
      }
    });
  }
}
```

### OpenCog Orchestration

Leverage Jancog's OpenCog extension for autonomous task execution:

```typescript
// Use OpenCog for complex character interactions
const openCog = window.core.extensionManager.get(ExtensionTypeEnum.OpenCog);

// Create a plan for character development
const plan = await openCog.createPlan(
  "Develop character backstory and generate sample dialogues",
  { threadId: characterId }
);

// Execute autonomously
await openCog.executePlan(plan.id);
```

---

## 5. Unified Integration Architecture

### Combined System Diagram

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           Unified Chub AI Ecosystem                         │
│                                                                             │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                        Chub AI Mobile App                            │   │
│  │  ┌─────────────────────────────────────────────────────────────┐    │   │
│  │  │                    React/Vite Frontend                       │    │   │
│  │  └──────────────────────────┬──────────────────────────────────┘    │   │
│  └─────────────────────────────┼────────────────────────────────────────┘   │
│                                │                                            │
│  ┌─────────────────────────────▼────────────────────────────────────────┐   │
│  │                      Integration Layer                                │   │
│  │                                                                       │   │
│  │  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  ┌──────────┐ │   │
│  │  │  LLM-Proxy   │  │   9fs9rc     │  │   Cogn8n     │  │  Jancog  │ │   │
│  │  │  (Logging)   │  │   (9P FS)    │  │  (Workflow)  │  │ (Offline)│ │   │
│  │  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘  └────┬─────┘ │   │
│  └─────────┼─────────────────┼─────────────────┼───────────────┼────────┘   │
│            │                 │                 │               │            │
│            ▼                 ▼                 ▼               ▼            │
│  ┌──────────────────────────────────────────────────────────────────────┐   │
│  │                        OpenCog AtomSpace                              │   │
│  │  ┌────────────────────────────────────────────────────────────────┐  │   │
│  │  │  Knowledge Graph: Characters, Lorebooks, Sessions, Patterns    │  │   │
│  │  └────────────────────────────────────────────────────────────────┘  │   │
│  └──────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### Data Flow

1. **User Request** → Chub AI Frontend
2. **Logging** → LLM-Proxy captures request/response
3. **Routing** → Online (Cloud) or Offline (Jancog)
4. **Automation** → Cogn8n workflows for batch operations
5. **Access** → 9P file system for shell-based interaction
6. **Knowledge** → OpenCog AtomSpace for cognitive processing

---

## 6. Implementation Roadmap

### Phase 1: LLM-Proxy Integration (2 weeks)

1. Fork llm-proxy and add Chub AI provider support
2. Configure session tracking for Chub AI chat format
3. Deploy as sidecar to Chub AI backend
4. Implement log explorer customizations

### Phase 2: 9fs9rc Integration (4 weeks)

1. Implement Chub AI 9P server in Go
2. Map REST API to 9P file operations
3. Create rc shell helper functions
4. Test with Plan 9 and Linux 9P clients

### Phase 3: Cogn8n Integration (3 weeks)

1. Develop ChubAI custom node for cogn8n
2. Create workflow templates for common tasks
3. Integrate with OpenCog nodes for cognitive processing
4. Document and publish to cogn8n community

### Phase 4: Jancog Integration (3 weeks)

1. Implement offline provider routing in Chub AI
2. Create character sync mechanism
3. Map Chub AI models to local equivalents
4. Test offline mode with various model sizes

### Phase 5: Unified Testing & Documentation (2 weeks)

1. End-to-end integration testing
2. Performance benchmarking
3. Documentation and user guides
4. Community release

---

## 7. Conclusion

These four integrations transform Chub AI from a standalone mobile application into a node within a larger cognitive computing ecosystem. The combination of centralized logging (llm-proxy), distributed file system access (9fs9rc), workflow automation (cogn8n), and offline capabilities (jancog) creates a robust, flexible, and privacy-respecting AI chat platform that aligns with the broader AGI development goals of integrating Plan 9/Inferno, OpenCog, and modern AI frameworks.

