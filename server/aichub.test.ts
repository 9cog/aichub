import { describe, expect, it, vi, beforeEach } from "vitest";
import { appRouter } from "./routers";
import type { TrpcContext } from "./_core/context";

// Mock the database functions
vi.mock("./db", () => ({
  getDb: vi.fn().mockResolvedValue({}),
  getCharacterById: vi.fn(),
  getCharactersByCreator: vi.fn(),
  searchCharacters: vi.fn(),
  createCharacter: vi.fn(),
  updateCharacter: vi.fn(),
  deleteCharacter: vi.fn(),
  hasUserLikedCharacter: vi.fn(),
  likeCharacter: vi.fn(),
  unlikeCharacter: vi.fn(),
  getLorebookById: vi.fn(),
  getLorebooksByCreator: vi.fn(),
  createLorebook: vi.fn(),
  updateLorebook: vi.fn(),
  deleteLorebook: vi.fn(),
  getLorebookEntries: vi.fn(),
  createLorebookEntry: vi.fn(),
  updateLorebookEntry: vi.fn(),
  deleteLorebookEntry: vi.fn(),
  getChatSessionById: vi.fn(),
  getChatSessionsByUser: vi.fn(),
  createChatSession: vi.fn(),
  deleteChatSession: vi.fn(),
  getMessagesByChatSession: vi.fn(),
  createMessage: vi.fn(),
  getLastMessageTurn: vi.fn(),
  incrementCharacterChatCount: vi.fn(),
  getPersonasByUser: vi.fn(),
  createPersona: vi.fn(),
  updatePersona: vi.fn(),
  deletePersona: vi.fn(),
  setDefaultPersona: vi.fn(),
  getContentReports: vi.fn(),
  createContentReport: vi.fn(),
  updateContentReport: vi.fn(),
  getSystemStats: vi.fn(),
  getAllUsers: vi.fn(),
  getUserById: vi.fn(),
}));

// Mock LLM
vi.mock("./_core/llm", () => ({
  invokeLLM: vi.fn().mockResolvedValue({
    choices: [{ message: { content: "Hello! I am a test response." } }],
  }),
}));

// Mock image generation
vi.mock("./_core/imageGeneration", () => ({
  generateImage: vi.fn().mockResolvedValue({ url: "https://example.com/avatar.png" }),
}));

// Mock storage
vi.mock("./storage", () => ({
  storagePut: vi.fn().mockResolvedValue({ url: "https://example.com/file.png" }),
}));

// Mock voice transcription
vi.mock("./_core/voiceTranscription", () => ({
  transcribeAudio: vi.fn().mockResolvedValue({ text: "Transcribed text" }),
}));

type AuthenticatedUser = NonNullable<TrpcContext["user"]>;

function createAuthContext(role: "user" | "admin" = "user"): TrpcContext {
  const user: AuthenticatedUser = {
    id: 1,
    openId: "test-user",
    email: "test@example.com",
    name: "Test User",
    loginMethod: "manus",
    role,
    createdAt: new Date(),
    updatedAt: new Date(),
    lastSignedIn: new Date(),
  };

  return {
    user,
    req: { protocol: "https", headers: {} } as TrpcContext["req"],
    res: { clearCookie: vi.fn() } as unknown as TrpcContext["res"],
  };
}

function createPublicContext(): TrpcContext {
  return {
    user: null,
    req: { protocol: "https", headers: {} } as TrpcContext["req"],
    res: { clearCookie: vi.fn() } as unknown as TrpcContext["res"],
  };
}

describe("AIChub Platform Tests", () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  describe("Character Router", () => {
    it("should search characters publicly", async () => {
      const { searchCharacters } = await import("./db");
      (searchCharacters as any).mockResolvedValue([
        { id: 1, name: "Test Character", description: "A test character" },
      ]);

      const ctx = createPublicContext();
      const caller = appRouter.createCaller(ctx);

      const result = await caller.character.search({
        query: "test",
        limit: 10,
        sortBy: "recent",
      });

      expect(result).toHaveLength(1);
      expect(result[0].name).toBe("Test Character");
    });

    it("should get character by id", async () => {
      const { getCharacterById } = await import("./db");
      (getCharacterById as any).mockResolvedValue({
        id: 1,
        name: "Test Character",
        description: "A test character",
        creatorId: 1,
      });

      const ctx = createPublicContext();
      const caller = appRouter.createCaller(ctx);

      const result = await caller.character.get({ id: 1 });

      expect(result.name).toBe("Test Character");
    });

    it("should create character when authenticated", async () => {
      const { createCharacter } = await import("./db");
      (createCharacter as any).mockResolvedValue(1);

      const ctx = createAuthContext();
      const caller = appRouter.createCaller(ctx);

      const result = await caller.character.create({
        name: "New Character",
        description: "A new character",
        personality: "Friendly",
      });

      expect(result.id).toBe(1);
      expect(createCharacter).toHaveBeenCalled();
    });

    it("should get user's characters", async () => {
      const { getCharactersByCreator } = await import("./db");
      (getCharactersByCreator as any).mockResolvedValue([
        { id: 1, name: "My Character", creatorId: 1 },
      ]);

      const ctx = createAuthContext();
      const caller = appRouter.createCaller(ctx);

      const result = await caller.character.myCharacters({});

      expect(result).toHaveLength(1);
      expect(result[0].name).toBe("My Character");
    });

    it("should import character from JSON", async () => {
      const { createCharacter } = await import("./db");
      (createCharacter as any).mockResolvedValue(2);

      const ctx = createAuthContext();
      const caller = appRouter.createCaller(ctx);

      const characterJson = JSON.stringify({
        name: "Imported Character",
        description: "An imported character",
        personality: "Mysterious",
        first_mes: "Hello, traveler.",
      });

      const result = await caller.character.import({
        format: "sillytavern",
        data: characterJson,
      });

      expect(result.id).toBe(2);
    });

    it("should export character in SillyTavern format", async () => {
      const { getCharacterById } = await import("./db");
      (getCharacterById as any).mockResolvedValue({
        id: 1,
        name: "Export Character",
        description: "A character to export",
        personality: "Brave",
        scenario: "Fantasy world",
        firstMessage: "Greetings!",
        exampleMessages: "Example dialogue",
        systemPrompt: "You are a hero",
        creatorNotes: "Created for testing",
        tags: ["fantasy", "hero"],
        creatorId: 1,
      });

      const ctx = createAuthContext();
      const caller = appRouter.createCaller(ctx);

      const result = await caller.character.export({ id: 1, format: "sillytavern" });

      expect(result.name).toBe("Export Character");
      expect(result.first_mes).toBe("Greetings!");
    });
  });

  describe("Lorebook Router", () => {
    it("should create lorebook", async () => {
      const { createLorebook } = await import("./db");
      (createLorebook as any).mockResolvedValue(1);

      const ctx = createAuthContext();
      const caller = appRouter.createCaller(ctx);

      const result = await caller.lorebook.create({
        name: "Test Lorebook",
        description: "A test lorebook",
      });

      expect(result.id).toBe(1);
    });

    it("should get lorebook with entries", async () => {
      const { getLorebookById, getLorebookEntries } = await import("./db");
      (getLorebookById as any).mockResolvedValue({
        id: 1,
        name: "Test Lorebook",
        creatorId: 1,
      });
      (getLorebookEntries as any).mockResolvedValue([
        { id: 1, keys: ["dragon"], content: "A mythical creature", priority: 100 },
      ]);

      const ctx = createPublicContext();
      const caller = appRouter.createCaller(ctx);

      const result = await caller.lorebook.get({ id: 1 });

      expect(result.name).toBe("Test Lorebook");
      expect(result.entries).toHaveLength(1);
    });

    it("should add entry to lorebook", async () => {
      const { getLorebookById, createLorebookEntry } = await import("./db");
      (getLorebookById as any).mockResolvedValue({
        id: 1,
        name: "Test Lorebook",
        creatorId: 1,
      });
      (createLorebookEntry as any).mockResolvedValue(1);

      const ctx = createAuthContext();
      const caller = appRouter.createCaller(ctx);

      const result = await caller.lorebook.addEntry({
        lorebookId: 1,
        entry: {
          keys: ["magic", "spell"],
          content: "Magic is a powerful force",
          priority: 100,
        },
      });

      expect(result.id).toBe(1);
    });
  });

  describe("Chat Router", () => {
    it("should create chat session", async () => {
      const { getCharacterById, createChatSession, createMessage, incrementCharacterChatCount } = await import("./db");
      (getCharacterById as any).mockResolvedValue({
        id: 1,
        name: "Chat Character",
        firstMessage: "Hello!",
      });
      (createChatSession as any).mockResolvedValue(1);
      (createMessage as any).mockResolvedValue(1);
      (incrementCharacterChatCount as any).mockResolvedValue(undefined);

      const ctx = createAuthContext();
      const caller = appRouter.createCaller(ctx);

      const result = await caller.chat.create({ characterId: 1 });

      expect(result.id).toBe(1);
      expect(createMessage).toHaveBeenCalled(); // First message should be added
    });

    it("should get chat session with messages", async () => {
      const { getChatSessionById, getMessagesByChatSession, getCharacterById } = await import("./db");
      (getChatSessionById as any).mockResolvedValue({
        id: 1,
        userId: 1,
        characterId: 1,
        title: "Test Chat",
      });
      (getMessagesByChatSession as any).mockResolvedValue([
        { id: 1, role: "assistant", content: "Hello!", turn: 1 },
      ]);
      (getCharacterById as any).mockResolvedValue({
        id: 1,
        name: "Chat Character",
      });

      const ctx = createAuthContext();
      const caller = appRouter.createCaller(ctx);

      const result = await caller.chat.get({ id: 1 });

      expect(result.title).toBe("Test Chat");
      expect(result.messages).toHaveLength(1);
    });

    it("should list user's chat sessions", async () => {
      const { getChatSessionsByUser } = await import("./db");
      (getChatSessionsByUser as any).mockResolvedValue([
        { id: 1, title: "Chat 1", characterId: 1 },
        { id: 2, title: "Chat 2", characterId: 2 },
      ]);

      const ctx = createAuthContext();
      const caller = appRouter.createCaller(ctx);

      const result = await caller.chat.list({ limit: 10 });

      expect(result).toHaveLength(2);
    });
  });

  describe("Persona Router", () => {
    it("should create persona", async () => {
      const { createPersona, getPersonasByUser } = await import("./db");
      (createPersona as any).mockResolvedValue(1);
      (getPersonasByUser as any).mockResolvedValue([]);

      const ctx = createAuthContext();
      const caller = appRouter.createCaller(ctx);

      const result = await caller.persona.create({
        name: "My Persona",
        description: "A test persona",
        isDefault: false,
      });

      expect(result.id).toBe(1);
    });

    it("should list user's personas", async () => {
      const { getPersonasByUser } = await import("./db");
      (getPersonasByUser as any).mockResolvedValue([
        { id: 1, name: "Persona 1", isDefault: true },
        { id: 2, name: "Persona 2", isDefault: false },
      ]);

      const ctx = createAuthContext();
      const caller = appRouter.createCaller(ctx);

      const result = await caller.persona.list();

      expect(result).toHaveLength(2);
    });
  });

  describe("Admin Router", () => {
    it("should get system stats for admin", async () => {
      const { getSystemStats } = await import("./db");
      (getSystemStats as any).mockResolvedValue({
        users: 100,
        characters: 500,
        chats: 1000,
        messages: 50000,
      });

      const ctx = createAuthContext("admin");
      const caller = appRouter.createCaller(ctx);

      const result = await caller.admin.stats();

      expect(result.users).toBe(100);
      expect(result.characters).toBe(500);
    });

    it("should get pending reports for admin", async () => {
      const { getContentReports } = await import("./db");
      (getContentReports as any).mockResolvedValue([
        { id: 1, characterId: 1, reason: "Inappropriate content", status: "pending" },
      ]);

      const ctx = createAuthContext("admin");
      const caller = appRouter.createCaller(ctx);

      const result = await caller.admin.reports({ status: "pending" });

      expect(result).toHaveLength(1);
      expect(result[0].status).toBe("pending");
    });

    it("should reject non-admin access to admin routes", async () => {
      const ctx = createAuthContext("user");
      const caller = appRouter.createCaller(ctx);

      await expect(caller.admin.stats()).rejects.toThrow();
    });
  });

  describe("Report Router", () => {
    it("should create content report", async () => {
      const { getCharacterById, createContentReport } = await import("./db");
      (getCharacterById as any).mockResolvedValue({ id: 1, name: "Test Character" });
      (createContentReport as any).mockResolvedValue(1);

      const ctx = createAuthContext();
      const caller = appRouter.createCaller(ctx);

      const result = await caller.report.create({
        characterId: 1,
        reason: "Inappropriate content",
      });

      expect(result.id).toBe(1);
    });
  });

  describe("Auth Router", () => {
    it("should return current user", async () => {
      const ctx = createAuthContext();
      const caller = appRouter.createCaller(ctx);

      const result = await caller.auth.me();

      expect(result?.name).toBe("Test User");
      expect(result?.email).toBe("test@example.com");
    });

    it("should return null for unauthenticated user", async () => {
      const ctx = createPublicContext();
      const caller = appRouter.createCaller(ctx);

      const result = await caller.auth.me();

      expect(result).toBeNull();
    });

    it("should logout user", async () => {
      const ctx = createAuthContext();
      const caller = appRouter.createCaller(ctx);

      const result = await caller.auth.logout();

      expect(result.success).toBe(true);
      expect(ctx.res.clearCookie).toHaveBeenCalled();
    });
  });
});
