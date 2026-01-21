# Reference Implementations

This directory contains key interface files extracted from the integration repositories. These serve as reference implementations for developing Chub AI integrations.

## Directory Structure

```
references/
├── llm-proxy/              # LLM request/response logging proxy
│   ├── README.md           # Project overview
│   ├── proxy.go            # Core proxy implementation
│   ├── urlparse.go         # URL parsing for provider routing
│   ├── logger.go           # JSONL logging implementation
│   ├── session.go          # Session management
│   └── fingerprint.go      # Request fingerprinting
│
├── 9fs9rc/                 # Plan 9 file system integration
│   ├── README.md           # Project overview
│   ├── 9p.z                # Formal 9P protocol specification (Z notation)
│   ├── 9p/                 # 9P protocol headers
│   │   ├── fcall.h         # File call structures
│   │   ├── plan9.h         # Plan 9 compatibility layer
│   │   └── u9fs.h          # User-space 9P server
│   └── docs/
│       └── plan9-aichat.md # AI chat integration design
│
├── cogn8n/                 # Workflow automation (n8n fork)
│   └── OpenCog/            # OpenCog integration nodes
│       ├── README.md       # OpenCog nodes overview
│       ├── OpenCogClient.ts    # API client implementation
│       ├── AtomSpace.node.ts   # AtomSpace operations node
│       ├── CognitiveAgent.node.ts  # Cognitive agent node
│       └── ReasoningEngine.node.ts # PLN reasoning node
│
└── jancog/                 # Local AI inference (Jan fork)
    └── opencog-extension/  # OpenCog extension for Jan
        ├── README.md       # Extension overview
        ├── index.ts        # Extension entry point
        ├── atomspace.ts    # AtomSpace implementation
        ├── cognitive-reasoning.ts  # Reasoning engine
        ├── pln.ts          # Probabilistic Logic Networks
        └── tools.ts        # Tool integrations
```

## Source Repositories

| Reference | Source Repository | Description |
|-----------|-------------------|-------------|
| llm-proxy | [9cog/llm-proxy](https://github.com/9cog/llm-proxy) | Transparent LLM API logging proxy |
| 9fs9rc | [9cog/9fs9rc](https://github.com/9cog/9fs9rc) | Plan 9 file system for AI resources |
| cogn8n | [cogpy/cogn8n](https://github.com/cogpy/cogn8n) | OpenCog-enhanced n8n workflow automation |
| jancog | [cogpy/jancog](https://github.com/cogpy/jancog) | OpenCog-enhanced Jan local AI |

## Usage

These reference files demonstrate the interfaces and patterns used in each integration project. Use them as a guide when implementing Chub AI integrations:

1. **llm-proxy**: Study `proxy.go` and `fingerprint.go` to understand request interception and session tracking
2. **9fs9rc**: Review `9p.z` and `plan9-aichat.md` for the 9P protocol and AI chat file system design
3. **cogn8n**: Examine the node implementations to understand n8n custom node patterns
4. **jancog**: Study the OpenCog extension for local AI inference patterns

## Integration with Chub AI

See the [integration specifications](../docs/chub_ai_integration_specifications.md) for detailed guidance on how these references map to Chub AI integration points.
