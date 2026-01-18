# Chub AI Application Analysis Summary

## Application Overview

**App ID:** ai.chub  
**App Name:** Chub  
**Platform:** Capacitor-based hybrid mobile application (Android)  
**Web Framework:** React (Vite bundled)  
**Build System:** Android Gradle Plugin 8.0.0  

## Architecture

### Core Technology Stack
- **Frontend:** React + TypeScript + Vite
- **Mobile Framework:** Capacitor 
- **Native Bridge:** Apache Cordova compatibility layer
- **HTTP Client:** OkHttp3
- **Language Runtime:** Kotlin coroutines

### Key Capacitor Plugins
1. **@chub-ai/capacitor-self-updater** - Custom OTA update mechanism
2. **@byteowls/capacitor-oauth2** - OAuth2 authentication
3. **@codetrix-studio/capacitor-google-auth** - Google Sign-In
4. **@capacitor-community/apple-sign-in** - Apple Sign-In
5. **@capacitor-community/media** - Media handling
6. **@capacitor/filesystem** - File system access
7. **@capacitor/haptics** - Haptic feedback
8. **capacitor-subscriptions** - In-app purchases/subscriptions
9. **capacitor-voice-recorder** - Voice recording capability

## Domain Model

### Primary Entities
1. **Character** - AI character definitions with personality, scenario, greetings
2. **Lorebook** - World-building knowledge bases with entries and triggers
3. **Persona** - User-defined personas for roleplay
4. **Chat** - Conversation sessions with characters
5. **Message** - Individual chat messages with swipe/branch support
6. **Extension** - Modular functionality extensions
7. **Stage** - Interactive scenario/game stages
8. **Preset** - Model configuration presets
9. **Project** - User content projects

### Character Card Format (TavernAI/SillyTavern Compatible)
- `name`, `description`, `personality`, `scenario`
- `first_mes`, `alternate_greetings`, `mes_example`
- `system_prompt`, `creator_notes`, `character_book`
- `tags`, `extensions`, `avatar`

### Lorebook Entry Structure
- `keys` - Trigger keywords
- `content` - Entry content
- `enabled`, `case_sensitive`
- `insertion_order`, `priority`, `position`, `depth`

## API Architecture

### Backend Services
- **Main API:** `https://chub.ai/api/*`
- **Gateway:** `https://gateway.chub.ai`
- **Proxy:** `https://proxy.chub.ai/v1`
- **Avatar CDN:** `https://avatars.charhub.io`

### AI Model Endpoints (Subscription Tiers)
- **Mars:** `https://mars.chub.ai/chub/asha/v1`, `https://mars.chub.ai/mixtral/v1`
- **Mercury:** `https://mercury.chub.ai/mistral/v1`, `https://mercury.chub.ai/mythomax/v1`

### Third-Party Integrations
- OpenAI API
- Anthropic Claude API
- OpenRouter
- Google Gemini/PaLM2
- ElevenLabs (TTS)
- NovelAI
- KoboldAI
- Oobabooga Text Generation WebUI
- LiveKit (real-time communication)

## API Endpoints (Partial)

### Account Management
- `GET /api/account` - User account info
- `POST /api/account/token/` - Token management
- `POST /api/account/block/user/` - User blocking
- `POST /api/account/block/tag/` - Tag blocking

### Content Management
- `GET/POST /api/core/characters` - Character CRUD
- `GET/POST /api/core/lorebooks` - Lorebook CRUD
- `GET/POST /api/core/chats` - Chat sessions
- `GET/POST /api/personas` - User personas
- `GET/POST /api/gallery/` - Gallery management

### Social Features
- `GET/POST /api/follow/` - Follow users
- `GET /api/notifications` - Notifications
- `GET /api/leaderboard/v1` - Leaderboards
- `POST /api/events/nominate` - Event nominations

### Subscription
- `POST /api/subscribe/` - Subscription management
- `POST /api/now/subscribe/mars` - Mars tier
- `POST /api/now/subscribe/mercury` - Mercury tier

## Subscription System

### Tiers (Inferred)
1. **Free** - Basic access
2. **Mercury** - Mistral/MythoMax models
3. **Mars** - Premium models (Asha, Mixtral)

### Billing Integration
- Google Play Billing Client
- PayPal integration
- Apple In-App Purchases (iOS)

## Security Features

### Authentication
- Google OAuth2
- Apple Sign-In
- Email/Password (implied)

### Content Moderation
- NSFW content filtering
- Tag-based content classification
- User blocking system
- Content reporting system

## Real-Time Features

### LiveKit Integration
- End-to-end encryption worker
- Voice/video communication
- Screen sharing capability

### Voice Features
- Voice recording (capacitor-voice-recorder)
- Text-to-speech (ElevenLabs integration)

## Self-Update Mechanism

The app includes a custom self-updater plugin (`@chub-ai/capacitor-self-updater`) that:
1. Downloads APK updates
2. Triggers Android package installer
3. Supports both legacy and modern (FileProvider) installation methods

## File Structure

```
ai.chub/
├── assets/
│   ├── capacitor.config.json
│   ├── capacitor.plugins.json
│   ├── native-bridge.js
│   ├── dexopt/
│   │   ├── baseline.prof
│   │   └── baseline.profm
│   └── public/
│       ├── index.html
│       ├── cordova.js
│       ├── cordova_plugins.js
│       ├── assets/
│       │   ├── index-B0lmOk0B.js (14MB bundled app)
│       │   ├── index-Cz38bezi.css
│       │   ├── livekit-client.e2ee.worker-CJyXLvs_.js
│       │   └── worker-BAOIWoxA.js
│       ├── favicon/
│       └── sitemaps/
├── kotlin/
├── okhttp3/
├── org/apache/cordova/
├── res/
├── classes.dex
└── AndroidManifest.xml
```

## Key Observations

1. **Hybrid Architecture:** The app is a web application wrapped in a native Android shell using Capacitor
2. **Multi-Provider AI:** Supports numerous AI providers with unified interface
3. **Rich Content System:** Sophisticated character/lorebook system compatible with community standards
4. **Monetization:** Tiered subscription model with multiple payment providers
5. **Real-Time Capable:** LiveKit integration suggests voice/video chat features
6. **Self-Updating:** Custom OTA update mechanism bypasses app store updates
7. **NSFW Content:** Explicit content support with filtering/classification system
