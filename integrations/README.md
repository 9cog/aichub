# Chub AI Integration Modules

This directory contains proof-of-concept implementations for integrating Chub AI with complementary systems from the `9cog` and `cogpy` ecosystems.

## Overview

| Integration | Purpose | Status |
|-------------|---------|--------|
| **llm-proxy** | Centralized AI request/response logging | POC |
| **9p-chub** | Plan 9 file system access to Chub AI resources | POC |
| **cogn8n-chub** | Workflow automation for content management | POC |
| **jancog-chub** | Offline AI inference capabilities | POC |

## Directory Structure

```
integrations/
├── llm-proxy/           # LLM-Proxy Chub AI provider extension
│   └── chub_providers.go
├── 9p-chub/             # 9P file server for Chub AI
│   └── chub9p.go
├── cogn8n-chub/         # Cogn8n custom node for Chub AI
│   └── ChubAI.node.ts
└── jancog-chub/         # Jancog offline provider
    └── ChubOfflineProvider.ts
```

## Integration Details

### 1. LLM-Proxy Integration

Extends the `llm-proxy` transparent logging proxy to support Chub AI's multi-provider architecture (Mars, Mercury, OpenRouter). Provides session fingerprinting and conversation tracking.

**Key Features:**
- Provider URL parsing for Chub AI endpoints
- Session fingerprinting with character/chat context
- JSONL logging with tier-specific metadata

### 2. 9P-Chub Integration

Exposes Chub AI resources as a 9P file system, enabling shell-native interaction with characters, lorebooks, and chat sessions from Plan 9 or Linux systems.

**Key Features:**
- Virtual file system for characters, lorebooks, sessions
- Read/write operations mapped to REST API calls
- Caching layer for performance optimization

### 3. Cogn8n-Chub Integration

Custom n8n node for workflow automation of Chub AI content management tasks.

**Key Features:**
- Character CRUD operations
- Lorebook management and synchronization
- Chat session automation
- Export/import in multiple formats (TavernAI, SillyTavern)

### 4. Jancog-Chub Integration

Enables offline AI inference by routing requests to local LLM models via Jancog when cloud providers are unavailable.

**Key Features:**
- Automatic connectivity detection and failover
- Model mapping from Chub AI tiers to local equivalents
- Character sync for offline use
- OpenAI-compatible API routing

## Documentation

For detailed integration specifications, architecture diagrams, and implementation guidance, see:

- [Integration Specifications](../docs/chub_ai_integration_specifications.md)

## Usage

Each integration module is designed as a standalone component that can be developed and deployed independently. Refer to the individual source files for implementation details and API documentation.

## License

These integrations are provided as proof-of-concept implementations. See the main repository license for terms of use.
