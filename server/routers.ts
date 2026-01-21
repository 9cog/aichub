import { COOKIE_NAME } from "@shared/const";
import { getSessionCookieOptions } from "./_core/cookies";
import { systemRouter } from "./_core/systemRouter";
import { publicProcedure, protectedProcedure, router } from "./_core/trpc";
import { z } from "zod";
import { TRPCError } from "@trpc/server";
import * as db from "./db";
import { invokeLLM } from "./_core/llm";
import { generateImage } from "./_core/imageGeneration";
import { transcribeAudio } from "./_core/voiceTranscription";
import { notifyOwner } from "./_core/notification";
import { storagePut } from "./storage";
import { nanoid } from "nanoid";

// Admin procedure - requires admin role
const adminProcedure = protectedProcedure.use(({ ctx, next }) => {
  if (ctx.user.role !== 'admin') {
    throw new TRPCError({ code: 'FORBIDDEN', message: 'Admin access required' });
  }
  return next({ ctx });
});

// Subscription tier check helper
function checkSubscriptionTier(userTier: string, requiredTier: 'mercury' | 'mars'): boolean {
  const tiers = ['free', 'mercury', 'mars'];
  const userLevel = tiers.indexOf(userTier);
  const requiredLevel = tiers.indexOf(requiredTier);
  return userLevel >= requiredLevel;
}

// Character input schema
const characterSchema = z.object({
  name: z.string().min(1).max(255),
  description: z.string().optional(),
  personality: z.string().optional(),
  scenario: z.string().optional(),
  firstMessage: z.string().optional(),
  exampleMessages: z.string().optional(),
  systemPrompt: z.string().optional(),
  creatorNotes: z.string().optional(),
  avatarUrl: z.string().optional(),
  tags: z.array(z.string()).optional(),
  contentRating: z.enum(['sfw', 'nsfw']).default('sfw'),
  isPublic: z.boolean().default(false),
  lorebookId: z.number().optional(),
});

// Lorebook entry schema
const lorebookEntrySchema = z.object({
  keys: z.array(z.string()),
  content: z.string(),
  enabled: z.boolean().default(true),
  caseSensitive: z.boolean().default(false),
  priority: z.number().default(10),
  insertionOrder: z.number().default(0),
  depth: z.number().default(4),
});

export const appRouter = router({
  system: systemRouter,
  
  auth: router({
    me: publicProcedure.query(opts => opts.ctx.user),
    logout: publicProcedure.mutation(({ ctx }) => {
      const cookieOptions = getSessionCookieOptions(ctx.req);
      ctx.res.clearCookie(COOKIE_NAME, { ...cookieOptions, maxAge: -1 });
      return { success: true } as const;
    }),
  }),

  // ============================================================================
  // CHARACTER ROUTER
  // ============================================================================
  character: router({
    create: protectedProcedure
      .input(characterSchema)
      .mutation(async ({ ctx, input }) => {
        const id = await db.createCharacter({
          ...input,
          creatorId: ctx.user.id,
          tags: input.tags || [],
        });
        return { id };
      }),

    get: publicProcedure
      .input(z.object({ id: z.number() }))
      .query(async ({ input }) => {
        const character = await db.getCharacterById(input.id);
        if (!character) {
          throw new TRPCError({ code: 'NOT_FOUND', message: 'Character not found' });
        }
        return character;
      }),

    update: protectedProcedure
      .input(z.object({ id: z.number(), data: characterSchema.partial() }))
      .mutation(async ({ ctx, input }) => {
        const character = await db.getCharacterById(input.id);
        if (!character) {
          throw new TRPCError({ code: 'NOT_FOUND', message: 'Character not found' });
        }
        if (character.creatorId !== ctx.user.id && ctx.user.role !== 'admin') {
          throw new TRPCError({ code: 'FORBIDDEN', message: 'Not authorized' });
        }
        await db.updateCharacter(input.id, input.data);
        return { success: true };
      }),

    delete: protectedProcedure
      .input(z.object({ id: z.number() }))
      .mutation(async ({ ctx, input }) => {
        const character = await db.getCharacterById(input.id);
        if (!character) {
          throw new TRPCError({ code: 'NOT_FOUND', message: 'Character not found' });
        }
        if (character.creatorId !== ctx.user.id && ctx.user.role !== 'admin') {
          throw new TRPCError({ code: 'FORBIDDEN', message: 'Not authorized' });
        }
        await db.deleteCharacter(input.id);
        return { success: true };
      }),

    myCharacters: protectedProcedure
      .input(z.object({ limit: z.number().default(50), offset: z.number().default(0) }))
      .query(async ({ ctx, input }) => {
        return db.getCharactersByCreator(ctx.user.id, input.limit, input.offset);
      }),

    search: publicProcedure
      .input(z.object({
        query: z.string().optional(),
        tags: z.array(z.string()).optional(),
        contentRating: z.enum(['sfw', 'nsfw']).optional(),
        sortBy: z.enum(['recent', 'popular', 'likes']).default('recent'),
        limit: z.number().default(20),
        offset: z.number().default(0),
      }))
      .query(async ({ input }) => {
        return db.searchCharacters(input);
      }),

    like: protectedProcedure
      .input(z.object({ characterId: z.number() }))
      .mutation(async ({ ctx, input }) => {
        const hasLiked = await db.hasUserLikedCharacter(ctx.user.id, input.characterId);
        if (hasLiked) {
          await db.unlikeCharacter(ctx.user.id, input.characterId);
          return { liked: false };
        } else {
          await db.likeCharacter(ctx.user.id, input.characterId);
          return { liked: true };
        }
      }),

    hasLiked: protectedProcedure
      .input(z.object({ characterId: z.number() }))
      .query(async ({ ctx, input }) => {
        return db.hasUserLikedCharacter(ctx.user.id, input.characterId);
      }),

    generateAvatar: protectedProcedure
      .input(z.object({ description: z.string() }))
      .mutation(async ({ input }) => {
        const prompt = `Character portrait, high quality digital art, detailed face: ${input.description}`;
        const { url } = await generateImage({ prompt });
        return { avatarUrl: url };
      }),

    import: protectedProcedure
      .input(z.object({ 
        format: z.enum(['tavernai', 'sillytavern', 'chubai']),
        data: z.string() 
      }))
      .mutation(async ({ ctx, input }) => {
        const parsed = JSON.parse(input.data);
        
        // Map from different formats to our schema
        const characterData = {
          name: parsed.name || parsed.char_name || 'Unnamed',
          description: parsed.description || parsed.char_persona || '',
          personality: parsed.personality || '',
          scenario: parsed.scenario || parsed.world_scenario || '',
          firstMessage: parsed.first_mes || parsed.char_greeting || '',
          exampleMessages: parsed.mes_example || parsed.example_dialogue || '',
          systemPrompt: parsed.system_prompt || '',
          creatorNotes: parsed.creator_notes || '',
          tags: parsed.tags || [],
          contentRating: 'sfw' as const,
          isPublic: false,
          creatorId: ctx.user.id,
        };
        
        const id = await db.createCharacter(characterData);
        return { id };
      }),

    export: protectedProcedure
      .input(z.object({ 
        id: z.number(),
        format: z.enum(['tavernai', 'sillytavern', 'chubai'])
      }))
      .query(async ({ ctx, input }) => {
        const character = await db.getCharacterById(input.id);
        if (!character) {
          throw new TRPCError({ code: 'NOT_FOUND', message: 'Character not found' });
        }
        
        // Export in requested format
        if (input.format === 'sillytavern' || input.format === 'tavernai') {
          return {
            name: character.name,
            description: character.description,
            personality: character.personality,
            scenario: character.scenario,
            first_mes: character.firstMessage,
            mes_example: character.exampleMessages,
            system_prompt: character.systemPrompt,
            creator_notes: character.creatorNotes,
            tags: character.tags,
          };
        }
        
        return character;
      }),
  }),

  // ============================================================================
  // LOREBOOK ROUTER
  // ============================================================================
  lorebook: router({
    create: protectedProcedure
      .input(z.object({
        name: z.string().min(1).max(255),
        description: z.string().optional(),
        isPublic: z.boolean().default(false),
      }))
      .mutation(async ({ ctx, input }) => {
        const id = await db.createLorebook({
          ...input,
          creatorId: ctx.user.id,
        });
        return { id };
      }),

    get: publicProcedure
      .input(z.object({ id: z.number() }))
      .query(async ({ input }) => {
        const lorebook = await db.getLorebookById(input.id);
        if (!lorebook) {
          throw new TRPCError({ code: 'NOT_FOUND', message: 'Lorebook not found' });
        }
        const entries = await db.getLorebookEntries(input.id);
        return { ...lorebook, entries };
      }),

    update: protectedProcedure
      .input(z.object({
        id: z.number(),
        data: z.object({
          name: z.string().optional(),
          description: z.string().optional(),
          isPublic: z.boolean().optional(),
        }),
      }))
      .mutation(async ({ ctx, input }) => {
        const lorebook = await db.getLorebookById(input.id);
        if (!lorebook) {
          throw new TRPCError({ code: 'NOT_FOUND', message: 'Lorebook not found' });
        }
        if (lorebook.creatorId !== ctx.user.id && ctx.user.role !== 'admin') {
          throw new TRPCError({ code: 'FORBIDDEN', message: 'Not authorized' });
        }
        await db.updateLorebook(input.id, input.data);
        return { success: true };
      }),

    delete: protectedProcedure
      .input(z.object({ id: z.number() }))
      .mutation(async ({ ctx, input }) => {
        const lorebook = await db.getLorebookById(input.id);
        if (!lorebook) {
          throw new TRPCError({ code: 'NOT_FOUND', message: 'Lorebook not found' });
        }
        if (lorebook.creatorId !== ctx.user.id && ctx.user.role !== 'admin') {
          throw new TRPCError({ code: 'FORBIDDEN', message: 'Not authorized' });
        }
        await db.deleteLorebook(input.id);
        return { success: true };
      }),

    myLorebooks: protectedProcedure.query(async ({ ctx }) => {
      return db.getLorebooksByCreator(ctx.user.id);
    }),

    addEntry: protectedProcedure
      .input(z.object({ lorebookId: z.number(), entry: lorebookEntrySchema }))
      .mutation(async ({ ctx, input }) => {
        const lorebook = await db.getLorebookById(input.lorebookId);
        if (!lorebook) {
          throw new TRPCError({ code: 'NOT_FOUND', message: 'Lorebook not found' });
        }
        if (lorebook.creatorId !== ctx.user.id && ctx.user.role !== 'admin') {
          throw new TRPCError({ code: 'FORBIDDEN', message: 'Not authorized' });
        }
        const id = await db.createLorebookEntry({
          ...input.entry,
          lorebookId: input.lorebookId,
        });
        return { id };
      }),

    updateEntry: protectedProcedure
      .input(z.object({ id: z.number(), data: lorebookEntrySchema.partial() }))
      .mutation(async ({ input }) => {
        await db.updateLorebookEntry(input.id, input.data);
        return { success: true };
      }),

    deleteEntry: protectedProcedure
      .input(z.object({ id: z.number() }))
      .mutation(async ({ input }) => {
        await db.deleteLorebookEntry(input.id);
        return { success: true };
      }),
  }),

  // ============================================================================
  // CHAT ROUTER
  // ============================================================================
  chat: router({
    create: protectedProcedure
      .input(z.object({
        characterId: z.number(),
        personaId: z.number().optional(),
        title: z.string().optional(),
      }))
      .mutation(async ({ ctx, input }) => {
        const character = await db.getCharacterById(input.characterId);
        if (!character) {
          throw new TRPCError({ code: 'NOT_FOUND', message: 'Character not found' });
        }
        
        const id = await db.createChatSession({
          userId: ctx.user.id,
          characterId: input.characterId,
          personaId: input.personaId,
          title: input.title || `Chat with ${character.name}`,
        });
        
        // Increment chat count
        await db.incrementCharacterChatCount(input.characterId);
        
        // Add first message if character has one
        if (character.firstMessage) {
          await db.createMessage({
            chatSessionId: id,
            turn: 1,
            role: 'assistant',
            content: character.firstMessage,
          });
        }
        
        return { id };
      }),

    get: protectedProcedure
      .input(z.object({ id: z.number() }))
      .query(async ({ ctx, input }) => {
        const session = await db.getChatSessionById(input.id);
        if (!session) {
          throw new TRPCError({ code: 'NOT_FOUND', message: 'Chat session not found' });
        }
        if (session.userId !== ctx.user.id && ctx.user.role !== 'admin') {
          throw new TRPCError({ code: 'FORBIDDEN', message: 'Not authorized' });
        }
        const messages = await db.getMessagesByChatSession(input.id);
        const character = await db.getCharacterById(session.characterId);
        return { ...session, messages, character };
      }),

    list: protectedProcedure
      .input(z.object({ limit: z.number().default(50), offset: z.number().default(0) }))
      .query(async ({ ctx, input }) => {
        return db.getChatSessionsByUser(ctx.user.id, input.limit, input.offset);
      }),

    listByCharacter: protectedProcedure
      .input(z.object({ characterId: z.number() }))
      .query(async ({ ctx, input }) => {
        return db.getChatSessionsByCharacter(ctx.user.id, input.characterId);
      }),

    delete: protectedProcedure
      .input(z.object({ id: z.number() }))
      .mutation(async ({ ctx, input }) => {
        const session = await db.getChatSessionById(input.id);
        if (!session) {
          throw new TRPCError({ code: 'NOT_FOUND', message: 'Chat session not found' });
        }
        if (session.userId !== ctx.user.id && ctx.user.role !== 'admin') {
          throw new TRPCError({ code: 'FORBIDDEN', message: 'Not authorized' });
        }
        await db.deleteChatSession(input.id);
        return { success: true };
      }),

    sendMessage: protectedProcedure
      .input(z.object({
        chatSessionId: z.number(),
        content: z.string().min(1),
      }))
      .mutation(async ({ ctx, input }) => {
        const session = await db.getChatSessionById(input.chatSessionId);
        if (!session) {
          throw new TRPCError({ code: 'NOT_FOUND', message: 'Chat session not found' });
        }
        if (session.userId !== ctx.user.id) {
          throw new TRPCError({ code: 'FORBIDDEN', message: 'Not authorized' });
        }
        
        const character = await db.getCharacterById(session.characterId);
        if (!character) {
          throw new TRPCError({ code: 'NOT_FOUND', message: 'Character not found' });
        }
        
        // Get current turn
        const lastTurn = await db.getLastMessageTurn(input.chatSessionId);
        const userTurn = lastTurn + 1;
        
        // Save user message
        await db.createMessage({
          chatSessionId: input.chatSessionId,
          turn: userTurn,
          role: 'user',
          content: input.content,
        });
        
        // Get message history for context
        const history = await db.getMessagesByChatSession(input.chatSessionId);
        
        // Build system prompt
        let systemPrompt = character.systemPrompt || `You are ${character.name}.`;
        if (character.description) systemPrompt += `\n\nDescription: ${character.description}`;
        if (character.personality) systemPrompt += `\n\nPersonality: ${character.personality}`;
        if (character.scenario) systemPrompt += `\n\nScenario: ${character.scenario}`;
        
        // Get lorebook entries if available
        if (character.lorebookId) {
          const entries = await db.getLorebookEntries(character.lorebookId);
          const activeEntries = entries.filter(e => e.enabled);
          
          // Check for keyword matches in recent messages
          const recentText = history.slice(-4).map(m => m.content).join(' ') + ' ' + input.content;
          const matchedEntries = activeEntries.filter(entry => {
            const keys = entry.keys || [];
            return keys.some(key => {
              const regex = entry.caseSensitive 
                ? new RegExp(key) 
                : new RegExp(key, 'i');
              return regex.test(recentText);
            });
          });
          
          if (matchedEntries.length > 0) {
            const lorebookContext = matchedEntries
              .sort((a, b) => b.priority - a.priority)
              .map(e => e.content)
              .join('\n\n');
            systemPrompt += `\n\n[World Info]\n${lorebookContext}`;
          }
        }
        
        // Build messages for LLM
        const llmMessages: Array<{ role: 'system' | 'user' | 'assistant'; content: string }> = [
          { role: 'system', content: systemPrompt },
        ];
        
        // Add history (limit to last 20 messages for context)
        const contextMessages = history.slice(-20);
        for (const msg of contextMessages) {
          llmMessages.push({
            role: msg.role === 'assistant' ? 'assistant' : 'user',
            content: msg.content,
          });
        }
        
        // Add current user message
        llmMessages.push({ role: 'user', content: input.content });
        
        // Call LLM
        const response = await invokeLLM({ messages: llmMessages });
        const messageContent = response.choices[0]?.message?.content;
        const assistantContent = typeof messageContent === 'string' ? messageContent : 'I apologize, but I was unable to generate a response.';
        
        // Save assistant message
        const assistantMsgId = await db.createMessage({
          chatSessionId: input.chatSessionId,
          turn: userTurn + 1,
          role: 'assistant',
          content: assistantContent,
        });
        
        return {
          userMessageId: userTurn,
          assistantMessage: {
            id: assistantMsgId,
            turn: userTurn + 1,
            role: 'assistant' as const,
            content: assistantContent,
          },
        };
      }),

    regenerate: protectedProcedure
      .input(z.object({ chatSessionId: z.number(), messageId: z.number() }))
      .mutation(async ({ ctx, input }) => {
        const session = await db.getChatSessionById(input.chatSessionId);
        if (!session || session.userId !== ctx.user.id) {
          throw new TRPCError({ code: 'FORBIDDEN', message: 'Not authorized' });
        }
        
        // Deactivate the old message
        await db.deactivateMessage(input.messageId);
        
        // Get the last user message to regenerate from
        const messages = await db.getMessagesByChatSession(input.chatSessionId);
        const lastUserMsg = [...messages].reverse().find(m => m.role === 'user');
        
        if (!lastUserMsg) {
          throw new TRPCError({ code: 'BAD_REQUEST', message: 'No user message to regenerate from' });
        }
        
        // This will trigger a new AI response
        // For now, return success - the client should call sendMessage again
        return { success: true, lastUserMessage: lastUserMsg.content };
      }),

    transcribeVoice: protectedProcedure
      .input(z.object({ audioUrl: z.string() }))
      .mutation(async ({ input }) => {
        const result = await transcribeAudio({ audioUrl: input.audioUrl });
        if ('error' in result) {
          throw new TRPCError({ code: 'INTERNAL_SERVER_ERROR', message: result.error });
        }
        return { text: result.text };
      }),
  }),

  // ============================================================================
  // PERSONA ROUTER
  // ============================================================================
  persona: router({
    create: protectedProcedure
      .input(z.object({
        name: z.string().min(1).max(255),
        description: z.string().optional(),
        avatarUrl: z.string().optional(),
        isDefault: z.boolean().default(false),
      }))
      .mutation(async ({ ctx, input }) => {
        if (input.isDefault) {
          // Clear existing defaults
          const personas = await db.getPersonasByUser(ctx.user.id);
          for (const p of personas) {
            if (p.isDefault) {
              await db.updatePersona(p.id, { isDefault: false });
            }
          }
        }
        const id = await db.createPersona({
          ...input,
          userId: ctx.user.id,
        });
        return { id };
      }),

    list: protectedProcedure.query(async ({ ctx }) => {
      return db.getPersonasByUser(ctx.user.id);
    }),

    update: protectedProcedure
      .input(z.object({
        id: z.number(),
        data: z.object({
          name: z.string().optional(),
          description: z.string().optional(),
          avatarUrl: z.string().optional(),
        }),
      }))
      .mutation(async ({ ctx, input }) => {
        await db.updatePersona(input.id, input.data);
        return { success: true };
      }),

    delete: protectedProcedure
      .input(z.object({ id: z.number() }))
      .mutation(async ({ input }) => {
        await db.deletePersona(input.id);
        return { success: true };
      }),

    setDefault: protectedProcedure
      .input(z.object({ id: z.number() }))
      .mutation(async ({ ctx, input }) => {
        await db.setDefaultPersona(ctx.user.id, input.id);
        return { success: true };
      }),
  }),

  // ============================================================================
  // SUBSCRIPTION ROUTER
  // ============================================================================
  subscription: router({
    current: protectedProcedure.query(async ({ ctx }) => {
      return {
        tier: ctx.user.subscriptionTier,
        expiresAt: ctx.user.subscriptionExpiresAt,
      };
    }),

    history: protectedProcedure.query(async ({ ctx }) => {
      return db.getSubscriptionHistoryByUser(ctx.user.id);
    }),

    // Note: Actual payment processing would be handled by Stripe integration
    // This is a placeholder for the subscription upgrade flow
    upgrade: protectedProcedure
      .input(z.object({ tier: z.enum(['mercury', 'mars']) }))
      .mutation(async ({ ctx, input }) => {
        // In production, this would redirect to Stripe checkout
        // For now, just return info about the tier
        const tierInfo = {
          mercury: {
            name: 'Mercury',
            price: 9.99,
            features: ['Mistral 7B', 'MythoMax 13B', 'Unlimited chats'],
          },
          mars: {
            name: 'Mars',
            price: 19.99,
            features: ['All Mercury features', 'Llama 70B', 'Mixtral 8x7B', 'Priority support'],
          },
        };
        return tierInfo[input.tier];
      }),
  }),

  // ============================================================================
  // REPORT ROUTER
  // ============================================================================
  report: router({
    create: protectedProcedure
      .input(z.object({
        characterId: z.number().optional(),
        chatSessionId: z.number().optional(),
        messageId: z.number().optional(),
        reason: z.string().min(10),
      }))
      .mutation(async ({ ctx, input }) => {
        const id = await db.createContentReport({
          ...input,
          reporterId: ctx.user.id,
        });
        
        // Notify owner about new report
        await notifyOwner({
          title: 'New Content Report',
          content: `A user has reported content. Reason: ${input.reason.substring(0, 100)}...`,
        });
        
        return { id };
      }),
  }),

  // ============================================================================
  // ADMIN ROUTER
  // ============================================================================
  admin: router({
    stats: adminProcedure.query(async () => {
      return db.getSystemStats();
    }),

    users: adminProcedure
      .input(z.object({ limit: z.number().default(100), offset: z.number().default(0) }))
      .query(async ({ input }) => {
        return db.getAllUsers(input.limit, input.offset);
      }),

    updateUserRole: adminProcedure
      .input(z.object({ userId: z.number(), role: z.enum(['user', 'admin']) }))
      .mutation(async ({ input }) => {
        const user = await db.getUserById(input.userId);
        if (!user) {
          throw new TRPCError({ code: 'NOT_FOUND', message: 'User not found' });
        }
        // Update role via raw query since we don't have a dedicated function
        const dbInstance = await db.getDb();
        if (dbInstance) {
          const { users } = await import('../drizzle/schema');
          const { eq } = await import('drizzle-orm');
          await dbInstance.update(users).set({ role: input.role }).where(eq(users.id, input.userId));
        }
        return { success: true };
      }),

    reports: adminProcedure
      .input(z.object({
        status: z.enum(['pending', 'reviewed', 'resolved', 'dismissed']).optional(),
        limit: z.number().default(50),
        offset: z.number().default(0),
      }))
      .query(async ({ input }) => {
        return db.getContentReports(input.status, input.limit, input.offset);
      }),

    updateReport: adminProcedure
      .input(z.object({
        id: z.number(),
        status: z.enum(['pending', 'reviewed', 'resolved', 'dismissed']),
        reviewNotes: z.string().optional(),
      }))
      .mutation(async ({ ctx, input }) => {
        await db.updateContentReport(input.id, {
          status: input.status,
          reviewedBy: ctx.user.id,
          reviewNotes: input.reviewNotes,
        });
        return { success: true };
      }),

    deleteCharacter: adminProcedure
      .input(z.object({ id: z.number() }))
      .mutation(async ({ input }) => {
        await db.deleteCharacter(input.id);
        return { success: true };
      }),
  }),

  // ============================================================================
  // UPLOAD ROUTER
  // ============================================================================
  upload: router({
    getPresignedUrl: protectedProcedure
      .input(z.object({
        filename: z.string(),
        contentType: z.string(),
      }))
      .mutation(async ({ ctx, input }) => {
        const key = `uploads/${ctx.user.id}/${nanoid()}-${input.filename}`;
        // Return the key for client to use with direct upload
        return { key, uploadUrl: `/api/upload/${key}` };
      }),

    avatar: protectedProcedure
      .input(z.object({
        base64: z.string(),
        filename: z.string(),
      }))
      .mutation(async ({ ctx, input }) => {
        const buffer = Buffer.from(input.base64, 'base64');
        const key = `avatars/${ctx.user.id}/${nanoid()}-${input.filename}`;
        const { url } = await storagePut(key, buffer, 'image/png');
        return { url };
      }),
  }),
});

export type AppRouter = typeof appRouter;
