# AIChub - AI Character Chat Platform TODO

## Core Infrastructure
- [x] Database schema design (users, characters, lorebooks, chats, messages, personas, subscriptions)
- [x] tRPC routers setup for all features
- [x] Authentication with role-based access control (admin/user)

## Character Management
- [x] Character CRUD operations
- [x] Character customization (personality, scenario, first message, system prompt, avatar, tags)
- [x] Character discovery with search and filtering
- [x] Tag-based filtering system
- [x] Content rating filters (SFW/NSFW)
- [x] Character card import (TavernAI/SillyTavern formats)
- [x] Character card export (TavernAI/SillyTavern formats)

## Lorebook System
- [x] Lorebook CRUD operations
- [x] Lorebook entries with keywords, priority, depth settings
- [x] Link lorebooks to characters

## AI Chat System
- [x] Real-time chat interface
- [x] Streaming AI responses (using built-in LLM)
- [x] Multi-provider support (via built-in Manus LLM API)
- [x] Chat session management (create, save, load, delete)
- [x] Message history persistence

## Subscription System
- [x] Three-tier subscription model (Free, Mercury, Mars)
- [x] Model access control based on subscription tier
- [x] Subscription management UI

## User Features
- [x] User persona management
- [x] Voice input using Whisper transcription
- [x] AI-powered avatar generation for characters

## Admin Dashboard
- [x] Content moderation tools
- [x] User management capabilities
- [x] System monitoring
- [x] Report handling system
- [x] Owner notifications for reports

## UI/UX
- [x] Clean, functional chat platform design
- [x] Dark theme optimized for chat
- [x] Responsive layout
- [x] Dashboard layout for authenticated users
- [x] Landing page for visitors


## Bugs
- [ ] Fix API returning HTML instead of JSON (tRPC routing issue)

- [x] Sync project to GitHub repository

- [x] Add sample characters (various types)
- [x] Add sample lorebooks with entries
- [x] Add sample personas
