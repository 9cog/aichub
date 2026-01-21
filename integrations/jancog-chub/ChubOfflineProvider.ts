/**
 * ChubOfflineProvider.ts
 * Jancog integration for Chub AI offline capabilities
 * Enables local LLM inference when cloud providers are unavailable
 */

import { EventEmitter } from 'events';

// Types
interface ChubCharacter {
  id: string;
  name: string;
  description: string;
  personality: string;
  scenario: string;
  first_mes: string;
  system_prompt?: string;
  avatar_url?: string;
  lorebook_id?: string;
}

interface JancogAssistant {
  id: string;
  name: string;
  instructions: string;
  model: string;
  metadata: Record<string, any>;
}

interface ChatMessage {
  role: 'user' | 'assistant' | 'system';
  content: string;
  timestamp?: number;
}

interface ProviderConfig {
  online: {
    mars: string;
    mercury: string;
    openai: string;
    anthropic: string;
  };
  offline: {
    local: string;
  };
}

interface ModelMapping {
  chubModel: string;
  localModel: string;
  tier: 'mars' | 'mercury';
  minRam: number; // GB
}

// Constants
const PROVIDER_CONFIG: ProviderConfig = {
  online: {
    mars: 'https://mars.chub.ai/chub/asha/v1',
    mercury: 'https://mercury.chub.ai/mistral/v1',
    openai: 'https://api.openai.com/v1',
    anthropic: 'https://api.anthropic.com/v1',
  },
  offline: {
    local: 'http://localhost:1337/v1',
  },
};

const MODEL_MAPPINGS: ModelMapping[] = [
  {
    chubModel: 'mars-asha',
    localModel: 'llama-3.1-70b-instruct',
    tier: 'mars',
    minRam: 48,
  },
  {
    chubModel: 'mars-mixtral',
    localModel: 'mixtral-8x7b-instruct',
    tier: 'mars',
    minRam: 32,
  },
  {
    chubModel: 'mercury-mistral',
    localModel: 'mistral-7b-instruct',
    tier: 'mercury',
    minRam: 8,
  },
  {
    chubModel: 'mercury-mythomax',
    localModel: 'mythomax-13b',
    tier: 'mercury',
    minRam: 16,
  },
  {
    chubModel: 'mercury-llama-8b',
    localModel: 'llama-3.1-8b-instruct',
    tier: 'mercury',
    minRam: 8,
  },
];

/**
 * ChubOfflineProvider manages the connection between Chub AI and Jancog
 * for offline AI inference capabilities
 */
export class ChubOfflineProvider extends EventEmitter {
  private jancogUrl: string;
  private isOnline: boolean = true;
  private syncedCharacters: Map<string, JancogAssistant> = new Map();
  private availableModels: string[] = [];
  private systemRam: number = 16; // Default assumption

  constructor(jancogUrl: string = 'http://localhost:1337') {
    super();
    this.jancogUrl = jancogUrl;
    this.initialize();
  }

  /**
   * Initialize the offline provider
   */
  private async initialize(): Promise<void> {
    // Check Jancog availability
    await this.checkJancogStatus();
    
    // Get available models
    await this.fetchAvailableModels();
    
    // Start connectivity monitoring
    this.startConnectivityMonitor();
  }

  /**
   * Check if Jancog is running and accessible
   */
  async checkJancogStatus(): Promise<boolean> {
    try {
      const response = await fetch(`${this.jancogUrl}/v1/models`);
      if (response.ok) {
        this.emit('jancog-connected');
        return true;
      }
    } catch (error) {
      this.emit('jancog-disconnected', error);
    }
    return false;
  }

  /**
   * Fetch available models from Jancog
   */
  async fetchAvailableModels(): Promise<string[]> {
    try {
      const response = await fetch(`${this.jancogUrl}/v1/models`);
      if (response.ok) {
        const data = await response.json();
        this.availableModels = data.data.map((m: any) => m.id);
        return this.availableModels;
      }
    } catch (error) {
      console.error('Failed to fetch Jancog models:', error);
    }
    return [];
  }

  /**
   * Start monitoring network connectivity
   */
  private startConnectivityMonitor(): void {
    setInterval(async () => {
      const wasOnline = this.isOnline;
      
      try {
        // Try to reach Chub AI
        const response = await fetch('https://api.chub.ai/health', {
          method: 'HEAD',
          signal: AbortSignal.timeout(5000),
        });
        this.isOnline = response.ok;
      } catch {
        this.isOnline = false;
      }

      if (wasOnline !== this.isOnline) {
        this.emit('connectivity-changed', this.isOnline);
        if (!this.isOnline) {
          this.emit('offline-mode-activated');
        } else {
          this.emit('online-mode-restored');
        }
      }
    }, 30000); // Check every 30 seconds
  }

  /**
   * Get the appropriate provider URL based on connectivity
   */
  getProviderUrl(tier: 'mars' | 'mercury' | 'openai' | 'anthropic'): string {
    if (this.isOnline) {
      return PROVIDER_CONFIG.online[tier];
    }
    return PROVIDER_CONFIG.offline.local;
  }

  /**
   * Map a Chub AI model to a local equivalent
   */
  mapModelToLocal(chubModel: string): string {
    const mapping = MODEL_MAPPINGS.find(
      (m) => m.chubModel === chubModel || chubModel.includes(m.chubModel)
    );

    if (mapping) {
      // Check if we have enough RAM for this model
      if (mapping.minRam <= this.systemRam) {
        // Check if model is available in Jancog
        if (this.availableModels.includes(mapping.localModel)) {
          return mapping.localModel;
        }
      }
    }

    // Fallback to smallest available model
    const fallbacks = ['mistral-7b-instruct', 'llama-3.1-8b-instruct', 'phi-3-mini'];
    for (const fallback of fallbacks) {
      if (this.availableModels.includes(fallback)) {
        return fallback;
      }
    }

    // Return first available model
    return this.availableModels[0] || 'mistral-7b-instruct';
  }

  /**
   * Sync a Chub AI character to Jancog for offline use
   */
  async syncCharacter(character: ChubCharacter): Promise<JancogAssistant> {
    // Check if already synced
    if (this.syncedCharacters.has(character.id)) {
      return this.syncedCharacters.get(character.id)!;
    }

    // Build system prompt from character data
    const systemPrompt = this.buildSystemPrompt(character);

    // Create Jancog assistant
    const assistant: JancogAssistant = {
      id: `chub-${character.id}`,
      name: character.name,
      instructions: systemPrompt,
      model: this.mapModelToLocal('mercury-mistral'), // Default to smaller model
      metadata: {
        chub_id: character.id,
        personality: character.personality,
        scenario: character.scenario,
        synced_at: Date.now(),
      },
    };

    // Save to Jancog
    try {
      const response = await fetch(`${this.jancogUrl}/v1/assistants`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(assistant),
      });

      if (response.ok) {
        const created = await response.json();
        this.syncedCharacters.set(character.id, created);
        this.emit('character-synced', character.id);
        return created;
      }
    } catch (error) {
      console.error('Failed to sync character to Jancog:', error);
      throw error;
    }

    return assistant;
  }

  /**
   * Build a system prompt from character data
   */
  private buildSystemPrompt(character: ChubCharacter): string {
    const parts: string[] = [];

    if (character.system_prompt) {
      parts.push(character.system_prompt);
    } else {
      parts.push(`You are ${character.name}.`);
    }

    if (character.description) {
      parts.push(`\n\nDescription: ${character.description}`);
    }

    if (character.personality) {
      parts.push(`\n\nPersonality: ${character.personality}`);
    }

    if (character.scenario) {
      parts.push(`\n\nScenario: ${character.scenario}`);
    }

    return parts.join('');
  }

  /**
   * Send a chat completion request, routing to online or offline provider
   */
  async chatCompletion(
    messages: ChatMessage[],
    options: {
      model?: string;
      characterId?: string;
      temperature?: number;
      maxTokens?: number;
    } = {}
  ): Promise<ChatMessage> {
    const { model, characterId, temperature = 0.7, maxTokens = 2048 } = options;

    // Determine which model to use
    let targetModel = model || 'mercury-mistral';
    let targetUrl = this.getProviderUrl('mercury');

    if (!this.isOnline) {
      // Use local model
      targetModel = this.mapModelToLocal(targetModel);
      targetUrl = PROVIDER_CONFIG.offline.local;

      // If character is synced, use the assistant
      if (characterId && this.syncedCharacters.has(characterId)) {
        const assistant = this.syncedCharacters.get(characterId)!;
        targetModel = assistant.model;
      }
    }

    // Build request
    const requestBody = {
      model: targetModel,
      messages: messages.map((m) => ({
        role: m.role,
        content: m.content,
      })),
      temperature,
      max_tokens: maxTokens,
      stream: false,
    };

    try {
      const response = await fetch(`${targetUrl}/chat/completions`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          // Add auth header if online
          ...(this.isOnline && { Authorization: `Bearer ${process.env.CHUB_API_KEY}` }),
        },
        body: JSON.stringify(requestBody),
      });

      if (!response.ok) {
        throw new Error(`API error: ${response.status}`);
      }

      const data = await response.json();
      const assistantMessage = data.choices[0].message;

      return {
        role: 'assistant',
        content: assistantMessage.content,
        timestamp: Date.now(),
      };
    } catch (error) {
      // If online request fails, try offline
      if (this.isOnline) {
        console.warn('Online request failed, falling back to offline');
        this.isOnline = false;
        return this.chatCompletion(messages, options);
      }
      throw error;
    }
  }

  /**
   * Batch sync multiple characters for offline use
   */
  async batchSyncCharacters(characters: ChubCharacter[]): Promise<void> {
    const results = await Promise.allSettled(
      characters.map((char) => this.syncCharacter(char))
    );

    const succeeded = results.filter((r) => r.status === 'fulfilled').length;
    const failed = results.filter((r) => r.status === 'rejected').length;

    this.emit('batch-sync-complete', { succeeded, failed, total: characters.length });
  }

  /**
   * Get list of synced characters
   */
  getSyncedCharacters(): JancogAssistant[] {
    return Array.from(this.syncedCharacters.values());
  }

  /**
   * Check if a character is synced for offline use
   */
  isCharacterSynced(characterId: string): boolean {
    return this.syncedCharacters.has(characterId);
  }

  /**
   * Get current connectivity status
   */
  getConnectivityStatus(): { isOnline: boolean; jancogAvailable: boolean } {
    return {
      isOnline: this.isOnline,
      jancogAvailable: this.availableModels.length > 0,
    };
  }

  /**
   * Set system RAM for model selection
   */
  setSystemRam(ramGb: number): void {
    this.systemRam = ramGb;
  }
}

/**
 * React hook for using the offline provider in Chub AI
 */
export function useChubOffline() {
  // This would be implemented as a React hook in the actual Chub AI app
  const provider = new ChubOfflineProvider();

  return {
    isOnline: () => provider.getConnectivityStatus().isOnline,
    syncCharacter: (char: ChubCharacter) => provider.syncCharacter(char),
    chat: (messages: ChatMessage[], options?: any) =>
      provider.chatCompletion(messages, options),
    getSyncedCharacters: () => provider.getSyncedCharacters(),
  };
}

/**
 * Capacitor plugin interface for mobile integration
 */
export interface ChubOfflinePlugin {
  checkConnectivity(): Promise<{ isOnline: boolean }>;
  syncCharacterForOffline(options: { characterId: string }): Promise<void>;
  getOfflineModels(): Promise<{ models: string[] }>;
  setPreferredModel(options: { model: string }): Promise<void>;
}

// Export default instance
export default new ChubOfflineProvider();
