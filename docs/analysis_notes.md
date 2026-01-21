# AIChub Analysis - Key Findings

## Repository Overview
- **Repository**: 9cog/aichub
- **Purpose**: Reverse-engineering analysis of Chub AI mobile application (ai.chub)
- **Content**: Formal Z++ specifications, technical documentation, architectural diagrams, decompiled source

## Application Architecture

### Technology Stack
| Layer | Technology |
|-------|------------|
| Frontend | React + TypeScript + Vite |
| Mobile Framework | Capacitor (hybrid app) |
| Native Bridge | Apache Cordova compatibility |
| HTTP Client | OkHttp3 |
| Runtime | Kotlin coroutines |

### Key Capacitor Plugins
1. `@chub-ai/capacitor-self-updater` - Custom OTA update mechanism
2. `@byteowls/capacitor-oauth2` - OAuth2 authentication
3. `@codetrix-studio/capacitor-google-auth` - Google Sign-In
4. `@capacitor-community/apple-sign-in` - Apple Sign-In
5. `capacitor-subscriptions` - In-app purchases
6. `capacitor-voice-recorder` - Voice recording
7. `@capacitor/filesystem` - File system access

### Backend Services
- **Main API**: https://chub.ai/api/*
- **Gateway**: https://gateway.chub.ai
- **Proxy**: https://proxy.chub.ai/v1
- **Avatar CDN**: https://avatars.charhub.io

### AI Model Endpoints (Tiered)
- **Mars Tier**: https://mars.chub.ai/chub/asha/v1, https://mars.chub.ai/mixtral/v1
- **Mercury Tier**: https://mercury.chub.ai/mistral/v1, https://mercury.chub.ai/mythomax/v1

### Third-Party Integrations
- OpenAI API
- Anthropic Claude API
- OpenRouter
- Google Gemini/PaLM2
- ElevenLabs (TTS)
- NovelAI
- KoboldAI
- LiveKit (real-time communication)

## Domain Model (Z++ Formal Spec)

### Core Entities
1. **USER** - id, username, email, role, subscription_tier
2. **CHARACTER** - name, description, personality, scenario, first_mes
3. **LOREBOOK** - name, description, entries (with trigger keys)
4. **CHAT** - character_id, user_id, messages
5. **MESSAGE** - role, content, timestamp, swipe support

### Subscription Tiers
- Free - Basic access
- Mercury - Mistral/MythoMax models
- Mars - Premium models (Asha, Mixtral)

## Related Repositories in User's Orgs

### 9cog Organization
- **llm-proxy**: Transparent logging proxy for AI API conversations
- **9fs9rc**: 9P Protocol, rc Shell, and AI Chat Integration
- **tocom**: Similar APK analysis methodology

### cogpy Organization
- **jancog**: Open source ChatGPT alternative (offline)
- **cogn8n**: Workflow automation with AI capabilities
- **cogprime**: (potential cognitive architecture)
- **atomspace-explorer**: OpenCog AtomSpace visualization

## Integration Opportunities

1. **LLM Proxy Integration**: The llm-proxy could intercept/log Chub AI's API calls
2. **9P Protocol Bridge**: 9fs9rc patterns could enable Plan 9 distributed access to chat services
3. **Workflow Automation**: cogn8n could orchestrate character/lorebook management
4. **Offline AI**: jancog patterns for local model inference

## Key Observations

1. **Hybrid Architecture**: Web SPA wrapped in native Android shell via Capacitor
2. **Multi-Provider AI**: Model-agnostic proxy layer for various AI providers
3. **Rich Content System**: TavernAI/SillyTavern compatible character cards
4. **Tiered Monetization**: Subscription model with multiple payment providers
5. **Real-Time Capable**: LiveKit integration for voice/video
6. **Self-Updating**: Custom OTA bypasses app store updates
7. **NSFW Support**: Content filtering/classification system
