import { eq, and, desc, asc, like, or, sql, inArray } from "drizzle-orm";
import { drizzle } from "drizzle-orm/mysql2";
import { 
  InsertUser, users, 
  characters, InsertCharacter, Character,
  lorebooks, InsertLorebook, Lorebook,
  lorebookEntries, InsertLorebookEntry, LorebookEntry,
  chatSessions, InsertChatSession, ChatSession,
  messages, InsertMessage, Message,
  personas, InsertPersona, Persona,
  characterLikes, InsertCharacterLike,
  contentReports, InsertContentReport, ContentReport,
  subscriptionHistory, InsertSubscriptionHistory
} from "../drizzle/schema";
import { ENV } from './_core/env';

let _db: ReturnType<typeof drizzle> | null = null;

export async function getDb() {
  if (!_db && process.env.DATABASE_URL) {
    try {
      _db = drizzle(process.env.DATABASE_URL);
    } catch (error) {
      console.warn("[Database] Failed to connect:", error);
      _db = null;
    }
  }
  return _db;
}

// ============================================================================
// USER OPERATIONS
// ============================================================================

export async function upsertUser(user: InsertUser): Promise<void> {
  if (!user.openId) {
    throw new Error("User openId is required for upsert");
  }

  const db = await getDb();
  if (!db) {
    console.warn("[Database] Cannot upsert user: database not available");
    return;
  }

  try {
    const values: InsertUser = {
      openId: user.openId,
    };
    const updateSet: Record<string, unknown> = {};

    const textFields = ["name", "email", "loginMethod"] as const;
    type TextField = (typeof textFields)[number];

    const assignNullable = (field: TextField) => {
      const value = user[field];
      if (value === undefined) return;
      const normalized = value ?? null;
      values[field] = normalized;
      updateSet[field] = normalized;
    };

    textFields.forEach(assignNullable);

    if (user.lastSignedIn !== undefined) {
      values.lastSignedIn = user.lastSignedIn;
      updateSet.lastSignedIn = user.lastSignedIn;
    }
    if (user.role !== undefined) {
      values.role = user.role;
      updateSet.role = user.role;
    } else if (user.openId === ENV.ownerOpenId) {
      values.role = 'admin';
      updateSet.role = 'admin';
    }

    if (!values.lastSignedIn) {
      values.lastSignedIn = new Date();
    }

    if (Object.keys(updateSet).length === 0) {
      updateSet.lastSignedIn = new Date();
    }

    await db.insert(users).values(values).onDuplicateKeyUpdate({
      set: updateSet,
    });
  } catch (error) {
    console.error("[Database] Failed to upsert user:", error);
    throw error;
  }
}

export async function getUserByOpenId(openId: string) {
  const db = await getDb();
  if (!db) return undefined;
  const result = await db.select().from(users).where(eq(users.openId, openId)).limit(1);
  return result.length > 0 ? result[0] : undefined;
}

export async function getUserById(id: number) {
  const db = await getDb();
  if (!db) return undefined;
  const result = await db.select().from(users).where(eq(users.id, id)).limit(1);
  return result.length > 0 ? result[0] : undefined;
}

export async function updateUserSubscription(userId: number, tier: 'free' | 'mercury' | 'mars', expiresAt?: Date) {
  const db = await getDb();
  if (!db) return;
  await db.update(users).set({ 
    subscriptionTier: tier, 
    subscriptionExpiresAt: expiresAt || null 
  }).where(eq(users.id, userId));
}

export async function getAllUsers(limit = 100, offset = 0) {
  const db = await getDb();
  if (!db) return [];
  return db.select().from(users).limit(limit).offset(offset).orderBy(desc(users.createdAt));
}

// ============================================================================
// CHARACTER OPERATIONS
// ============================================================================

export async function createCharacter(data: InsertCharacter): Promise<number> {
  const db = await getDb();
  if (!db) throw new Error("Database not available");
  const result = await db.insert(characters).values(data);
  return Number(result[0].insertId);
}

export async function getCharacterById(id: number): Promise<Character | undefined> {
  const db = await getDb();
  if (!db) return undefined;
  const result = await db.select().from(characters).where(eq(characters.id, id)).limit(1);
  return result[0];
}

export async function getCharactersByCreator(creatorId: number, limit = 50, offset = 0) {
  const db = await getDb();
  if (!db) return [];
  return db.select().from(characters)
    .where(eq(characters.creatorId, creatorId))
    .limit(limit).offset(offset)
    .orderBy(desc(characters.updatedAt));
}

export async function updateCharacter(id: number, data: Partial<InsertCharacter>) {
  const db = await getDb();
  if (!db) return;
  await db.update(characters).set(data).where(eq(characters.id, id));
}

export async function deleteCharacter(id: number) {
  const db = await getDb();
  if (!db) return;
  await db.delete(characters).where(eq(characters.id, id));
}

export async function searchCharacters(options: {
  query?: string;
  tags?: string[];
  contentRating?: 'sfw' | 'nsfw';
  isPublic?: boolean;
  limit?: number;
  offset?: number;
  sortBy?: 'recent' | 'popular' | 'likes';
}) {
  const db = await getDb();
  if (!db) return [];
  
  const { query, tags, contentRating, isPublic = true, limit = 20, offset = 0, sortBy = 'recent' } = options;
  
  const conditions = [eq(characters.isPublic, isPublic)];
  
  if (contentRating) {
    conditions.push(eq(characters.contentRating, contentRating));
  }
  
  if (query) {
    conditions.push(
      or(
        like(characters.name, `%${query}%`),
        like(characters.description, `%${query}%`)
      )!
    );
  }
  
  let orderBy;
  switch (sortBy) {
    case 'popular':
      orderBy = desc(characters.chatCount);
      break;
    case 'likes':
      orderBy = desc(characters.likeCount);
      break;
    default:
      orderBy = desc(characters.createdAt);
  }
  
  return db.select().from(characters)
    .where(and(...conditions))
    .limit(limit).offset(offset)
    .orderBy(orderBy);
}

export async function incrementCharacterChatCount(id: number) {
  const db = await getDb();
  if (!db) return;
  await db.update(characters)
    .set({ chatCount: sql`${characters.chatCount} + 1` })
    .where(eq(characters.id, id));
}

// ============================================================================
// LOREBOOK OPERATIONS
// ============================================================================

export async function createLorebook(data: InsertLorebook): Promise<number> {
  const db = await getDb();
  if (!db) throw new Error("Database not available");
  const result = await db.insert(lorebooks).values(data);
  return Number(result[0].insertId);
}

export async function getLorebookById(id: number): Promise<Lorebook | undefined> {
  const db = await getDb();
  if (!db) return undefined;
  const result = await db.select().from(lorebooks).where(eq(lorebooks.id, id)).limit(1);
  return result[0];
}

export async function getLorebooksByCreator(creatorId: number) {
  const db = await getDb();
  if (!db) return [];
  return db.select().from(lorebooks)
    .where(eq(lorebooks.creatorId, creatorId))
    .orderBy(desc(lorebooks.updatedAt));
}

export async function updateLorebook(id: number, data: Partial<InsertLorebook>) {
  const db = await getDb();
  if (!db) return;
  await db.update(lorebooks).set(data).where(eq(lorebooks.id, id));
}

export async function deleteLorebook(id: number) {
  const db = await getDb();
  if (!db) return;
  await db.delete(lorebookEntries).where(eq(lorebookEntries.lorebookId, id));
  await db.delete(lorebooks).where(eq(lorebooks.id, id));
}

// ============================================================================
// LOREBOOK ENTRY OPERATIONS
// ============================================================================

export async function createLorebookEntry(data: InsertLorebookEntry): Promise<number> {
  const db = await getDb();
  if (!db) throw new Error("Database not available");
  const result = await db.insert(lorebookEntries).values(data);
  return Number(result[0].insertId);
}

export async function getLorebookEntries(lorebookId: number): Promise<LorebookEntry[]> {
  const db = await getDb();
  if (!db) return [];
  return db.select().from(lorebookEntries)
    .where(eq(lorebookEntries.lorebookId, lorebookId))
    .orderBy(asc(lorebookEntries.insertionOrder));
}

export async function updateLorebookEntry(id: number, data: Partial<InsertLorebookEntry>) {
  const db = await getDb();
  if (!db) return;
  await db.update(lorebookEntries).set(data).where(eq(lorebookEntries.id, id));
}

export async function deleteLorebookEntry(id: number) {
  const db = await getDb();
  if (!db) return;
  await db.delete(lorebookEntries).where(eq(lorebookEntries.id, id));
}

// ============================================================================
// CHAT SESSION OPERATIONS
// ============================================================================

export async function createChatSession(data: InsertChatSession): Promise<number> {
  const db = await getDb();
  if (!db) throw new Error("Database not available");
  const result = await db.insert(chatSessions).values(data);
  return Number(result[0].insertId);
}

export async function getChatSessionById(id: number): Promise<ChatSession | undefined> {
  const db = await getDb();
  if (!db) return undefined;
  const result = await db.select().from(chatSessions).where(eq(chatSessions.id, id)).limit(1);
  return result[0];
}

export async function getChatSessionsByUser(userId: number, limit = 50, offset = 0) {
  const db = await getDb();
  if (!db) return [];
  return db.select().from(chatSessions)
    .where(eq(chatSessions.userId, userId))
    .limit(limit).offset(offset)
    .orderBy(desc(chatSessions.updatedAt));
}

export async function getChatSessionsByCharacter(userId: number, characterId: number) {
  const db = await getDb();
  if (!db) return [];
  return db.select().from(chatSessions)
    .where(and(
      eq(chatSessions.userId, userId),
      eq(chatSessions.characterId, characterId)
    ))
    .orderBy(desc(chatSessions.updatedAt));
}

export async function updateChatSession(id: number, data: Partial<InsertChatSession>) {
  const db = await getDb();
  if (!db) return;
  await db.update(chatSessions).set(data).where(eq(chatSessions.id, id));
}

export async function deleteChatSession(id: number) {
  const db = await getDb();
  if (!db) return;
  await db.delete(messages).where(eq(messages.chatSessionId, id));
  await db.delete(chatSessions).where(eq(chatSessions.id, id));
}

// ============================================================================
// MESSAGE OPERATIONS
// ============================================================================

export async function createMessage(data: InsertMessage): Promise<number> {
  const db = await getDb();
  if (!db) throw new Error("Database not available");
  const result = await db.insert(messages).values(data);
  // Update chat session timestamp
  await db.update(chatSessions)
    .set({ updatedAt: new Date() })
    .where(eq(chatSessions.id, data.chatSessionId));
  return Number(result[0].insertId);
}

export async function getMessagesByChatSession(chatSessionId: number): Promise<Message[]> {
  const db = await getDb();
  if (!db) return [];
  return db.select().from(messages)
    .where(and(
      eq(messages.chatSessionId, chatSessionId),
      eq(messages.isActive, true)
    ))
    .orderBy(asc(messages.turn), asc(messages.createdAt));
}

export async function getLastMessageTurn(chatSessionId: number): Promise<number> {
  const db = await getDb();
  if (!db) return 0;
  const result = await db.select({ maxTurn: sql<number>`MAX(${messages.turn})` })
    .from(messages)
    .where(eq(messages.chatSessionId, chatSessionId));
  return result[0]?.maxTurn || 0;
}

export async function deactivateMessage(id: number) {
  const db = await getDb();
  if (!db) return;
  await db.update(messages).set({ isActive: false }).where(eq(messages.id, id));
}

// ============================================================================
// PERSONA OPERATIONS
// ============================================================================

export async function createPersona(data: InsertPersona): Promise<number> {
  const db = await getDb();
  if (!db) throw new Error("Database not available");
  const result = await db.insert(personas).values(data);
  return Number(result[0].insertId);
}

export async function getPersonasByUser(userId: number): Promise<Persona[]> {
  const db = await getDb();
  if (!db) return [];
  return db.select().from(personas)
    .where(eq(personas.userId, userId))
    .orderBy(desc(personas.isDefault), desc(personas.updatedAt));
}

export async function getDefaultPersona(userId: number): Promise<Persona | undefined> {
  const db = await getDb();
  if (!db) return undefined;
  const result = await db.select().from(personas)
    .where(and(eq(personas.userId, userId), eq(personas.isDefault, true)))
    .limit(1);
  return result[0];
}

export async function updatePersona(id: number, data: Partial<InsertPersona>) {
  const db = await getDb();
  if (!db) return;
  await db.update(personas).set(data).where(eq(personas.id, id));
}

export async function deletePersona(id: number) {
  const db = await getDb();
  if (!db) return;
  await db.delete(personas).where(eq(personas.id, id));
}

export async function setDefaultPersona(userId: number, personaId: number) {
  const db = await getDb();
  if (!db) return;
  // Clear all defaults for user
  await db.update(personas).set({ isDefault: false }).where(eq(personas.userId, userId));
  // Set new default
  await db.update(personas).set({ isDefault: true }).where(eq(personas.id, personaId));
}

// ============================================================================
// CHARACTER LIKE OPERATIONS
// ============================================================================

export async function likeCharacter(userId: number, characterId: number) {
  const db = await getDb();
  if (!db) return;
  await db.insert(characterLikes).values({ userId, characterId });
  await db.update(characters)
    .set({ likeCount: sql`${characters.likeCount} + 1` })
    .where(eq(characters.id, characterId));
}

export async function unlikeCharacter(userId: number, characterId: number) {
  const db = await getDb();
  if (!db) return;
  await db.delete(characterLikes)
    .where(and(eq(characterLikes.userId, userId), eq(characterLikes.characterId, characterId)));
  await db.update(characters)
    .set({ likeCount: sql`${characters.likeCount} - 1` })
    .where(eq(characters.id, characterId));
}

export async function hasUserLikedCharacter(userId: number, characterId: number): Promise<boolean> {
  const db = await getDb();
  if (!db) return false;
  const result = await db.select().from(characterLikes)
    .where(and(eq(characterLikes.userId, userId), eq(characterLikes.characterId, characterId)))
    .limit(1);
  return result.length > 0;
}

// ============================================================================
// CONTENT REPORT OPERATIONS
// ============================================================================

export async function createContentReport(data: InsertContentReport): Promise<number> {
  const db = await getDb();
  if (!db) throw new Error("Database not available");
  const result = await db.insert(contentReports).values(data);
  return Number(result[0].insertId);
}

export async function getContentReports(status?: 'pending' | 'reviewed' | 'resolved' | 'dismissed', limit = 50, offset = 0) {
  const db = await getDb();
  if (!db) return [];
  const conditions = status ? [eq(contentReports.status, status)] : [];
  return db.select().from(contentReports)
    .where(conditions.length > 0 ? and(...conditions) : undefined)
    .limit(limit).offset(offset)
    .orderBy(desc(contentReports.createdAt));
}

export async function updateContentReport(id: number, data: Partial<InsertContentReport>) {
  const db = await getDb();
  if (!db) return;
  await db.update(contentReports).set(data).where(eq(contentReports.id, id));
}

// ============================================================================
// SUBSCRIPTION HISTORY OPERATIONS
// ============================================================================

export async function createSubscriptionHistory(data: InsertSubscriptionHistory): Promise<number> {
  const db = await getDb();
  if (!db) throw new Error("Database not available");
  const result = await db.insert(subscriptionHistory).values(data);
  return Number(result[0].insertId);
}

export async function getSubscriptionHistoryByUser(userId: number) {
  const db = await getDb();
  if (!db) return [];
  return db.select().from(subscriptionHistory)
    .where(eq(subscriptionHistory.userId, userId))
    .orderBy(desc(subscriptionHistory.createdAt));
}

// ============================================================================
// STATS OPERATIONS
// ============================================================================

export async function getSystemStats() {
  const db = await getDb();
  if (!db) return { users: 0, characters: 0, chats: 0, messages: 0 };
  
  const [userCount] = await db.select({ count: sql<number>`COUNT(*)` }).from(users);
  const [charCount] = await db.select({ count: sql<number>`COUNT(*)` }).from(characters);
  const [chatCount] = await db.select({ count: sql<number>`COUNT(*)` }).from(chatSessions);
  const [msgCount] = await db.select({ count: sql<number>`COUNT(*)` }).from(messages);
  
  return {
    users: userCount?.count || 0,
    characters: charCount?.count || 0,
    chats: chatCount?.count || 0,
    messages: msgCount?.count || 0,
  };
}
