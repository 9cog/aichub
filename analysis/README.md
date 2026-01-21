# Chub AI Application Analysis

This directory contains the extracted and analyzed contents of the Chub AI mobile application (APK).

## Directory Structure

```
analysis/
├── original/               # Original APK and ZIP files
│   ├── ai.chub.apk        # Android application package
│   └── ai.chub.zip        # Extracted APK contents
│
├── apk_contents/          # Key APK metadata and configuration
│   ├── AndroidManifest.xml    # Android manifest
│   ├── capacitor.config.json  # Capacitor configuration
│   ├── capacitor.plugins.json # Capacitor plugins
│   └── *.properties           # Play Services dependencies
│
├── decompiled/            # Decompiled Java source code
│   └── ai/chub/           # Chub AI package
│       ├── MainActivity.java
│       ├── R.java
│       └── plugins/updater/   # Self-updater plugin
│
└── web_assets/            # Bundled web application
    ├── index.html         # Entry point
    ├── manifest.json      # PWA manifest
    └── assets/            # Bundled JS/CSS
        ├── index-*.js     # Main application bundle (~14MB)
        ├── index-*.css    # Styles (~190KB)
        └── worker-*.js    # Web workers
```

## Key Findings

### Application Architecture

The Chub AI application is a **hybrid mobile app** built with:
- **Capacitor** - Native runtime for web apps
- **React** - Frontend framework (Vite bundled)
- **TypeScript** - Primary language

### Native Components

| Component | Purpose |
|-----------|---------|
| `MainActivity.java` | Android entry point |
| `SelfUpdaterPlugin.java` | OTA update mechanism |
| Play Services | Authentication, billing |

### Web Application

The bundled web application contains:
- Single-page React application
- LiveKit integration for real-time communication
- PWA manifest for installability
- Service worker for offline support

### API Endpoints

Discovered API endpoints from the bundled JavaScript:
- `https://api.chub.ai` - Main API
- `https://mars.chub.ai` - Mars tier AI models
- `https://mercury.chub.ai` - Mercury tier AI models
- `https://proxy.chub.ai` - Proxy for external providers

## Usage

These files are provided for analysis and integration development purposes. See the [integration specifications](../docs/chub_ai_integration_specifications.md) for details on how to integrate with Chub AI.

## Legal Notice

This analysis is for educational and interoperability purposes. All trademarks and copyrights belong to their respective owners.
