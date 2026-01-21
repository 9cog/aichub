import { int, mysqlEnum, mysqlTable, text, timestamp, varchar, boolean, json } from "drizzle-orm/mysql-core";

// ============================================================================
// USER MANAGEMENT
// ============================================================================

export const users = mysqlTable("users", {
  id: int("id").autoincrement().primaryKey(),
  openId: varchar("openId", { length: 64 }).notNull().unique(),
  name: text("name"),
  email: varchar("email", { length: 320 }),
  loginMethod: varchar("loginMethod", { length: 64 }),
  role: mysqlEnum("role", ["user", "admin"]).default("user").notNull(),
  subscriptionTier: mysqlEnum("subscriptionTier", ["free", "mercury", "mars"]).default("free").notNull(),
  subscriptionExpiresAt: timestamp("subscriptionExpiresAt"),
  blockedUsers: json("blockedUsers").$type<string[]>(),
  blockedTags: json("blockedTags").$type<string[]>(),
  createdAt: timestamp("createdAt").defaultNow().notNull(),
  updatedAt: timestamp("updatedAt").defaultNow().onUpdateNow().notNull(),
  lastSignedIn: timestamp("lastSignedIn").defaultNow().notNull(),
});

export type User = typeof users.$inferSelect;
export type InsertUser = typeof users.$inferInsert;

// ============================================================================
// USER PERSONAS
// ============================================================================

export const personas = mysqlTable("personas", {
  id: int("id").autoincrement().primaryKey(),
  userId: int("userId").notNull(),
  name: varchar("name", { length: 255 }).notNull(),
  description: text("description"),
  avatarUrl: text("avatarUrl"),
  isDefault: boolean("isDefault").default(false).notNull(),
  createdAt: timestamp("createdAt").defaultNow().notNull(),
  updatedAt: timestamp("updatedAt").defaultNow().onUpdateNow().notNull(),
});

export type Persona = typeof personas.$inferSelect;
export type InsertPersona = typeof personas.$inferInsert;

// ============================================================================
// CHARACTERS
// ============================================================================

export const characters = mysqlTable("characters", {
  id: int("id").autoincrement().primaryKey(),
  creatorId: int("creatorId").notNull(),
  name: varchar("name", { length: 255 }).notNull(),
  description: text("description"),
  personality: text("personality"),
  scenario: text("scenario"),
  firstMessage: text("firstMessage"),
  exampleMessages: text("exampleMessages"),
  systemPrompt: text("systemPrompt"),
  creatorNotes: text("creatorNotes"),
  avatarUrl: text("avatarUrl"),
  tags: json("tags").$type<string[]>(),
  contentRating: mysqlEnum("contentRating", ["sfw", "nsfw"]).default("sfw").notNull(),
  isPublic: boolean("isPublic").default(false).notNull(),
  lorebookId: int("lorebookId"),
  chatCount: int("chatCount").default(0).notNull(),
  likeCount: int("likeCount").default(0).notNull(),
  createdAt: timestamp("createdAt").defaultNow().notNull(),
  updatedAt: timestamp("updatedAt").defaultNow().onUpdateNow().notNull(),
});

export type Character = typeof characters.$inferSelect;
export type InsertCharacter = typeof characters.$inferInsert;

// ============================================================================
// LOREBOOKS
// ============================================================================

export const lorebooks = mysqlTable("lorebooks", {
  id: int("id").autoincrement().primaryKey(),
  creatorId: int("creatorId").notNull(),
  name: varchar("name", { length: 255 }).notNull(),
  description: text("description"),
  isPublic: boolean("isPublic").default(false).notNull(),
  createdAt: timestamp("createdAt").defaultNow().notNull(),
  updatedAt: timestamp("updatedAt").defaultNow().onUpdateNow().notNull(),
});

export type Lorebook = typeof lorebooks.$inferSelect;
export type InsertLorebook = typeof lorebooks.$inferInsert;

export const lorebookEntries = mysqlTable("lorebookEntries", {
  id: int("id").autoincrement().primaryKey(),
  lorebookId: int("lorebookId").notNull(),
  keys: json("keys").$type<string[]>(),
  content: text("content"),
  enabled: boolean("enabled").default(true).notNull(),
  caseSensitive: boolean("caseSensitive").default(false).notNull(),
  priority: int("priority").default(10).notNull(),
  insertionOrder: int("insertionOrder").default(0).notNull(),
  depth: int("depth").default(4).notNull(),
  createdAt: timestamp("createdAt").defaultNow().notNull(),
  updatedAt: timestamp("updatedAt").defaultNow().onUpdateNow().notNull(),
});

export type LorebookEntry = typeof lorebookEntries.$inferSelect;
export type InsertLorebookEntry = typeof lorebookEntries.$inferInsert;

// ============================================================================
// CHAT SESSIONS
// ============================================================================

export const chatSessions = mysqlTable("chatSessions", {
  id: int("id").autoincrement().primaryKey(),
  userId: int("userId").notNull(),
  characterId: int("characterId").notNull(),
  personaId: int("personaId"),
  title: varchar("title", { length: 255 }),
  aiProvider: mysqlEnum("aiProvider", ["openai", "anthropic", "openrouter", "builtin"]).default("builtin").notNull(),
  aiModel: varchar("aiModel", { length: 128 }),
  isClosed: boolean("isClosed").default(false).notNull(),
  createdAt: timestamp("createdAt").defaultNow().notNull(),
  updatedAt: timestamp("updatedAt").defaultNow().onUpdateNow().notNull(),
});

export type ChatSession = typeof chatSessions.$inferSelect;
export type InsertChatSession = typeof chatSessions.$inferInsert;

// ============================================================================
// MESSAGES
// ============================================================================

export const messages = mysqlTable("messages", {
  id: int("id").autoincrement().primaryKey(),
  chatSessionId: int("chatSessionId").notNull(),
  turn: int("turn").notNull(),
  role: mysqlEnum("role", ["user", "assistant", "system"]).notNull(),
  content: text("content").notNull(),
  swipedFromId: int("swipedFromId"),
  isActive: boolean("isActive").default(true).notNull(),
  createdAt: timestamp("createdAt").defaultNow().notNull(),
});

export type Message = typeof messages.$inferSelect;
export type InsertMessage = typeof messages.$inferInsert;

// ============================================================================
// CHARACTER LIKES
// ============================================================================

export const characterLikes = mysqlTable("characterLikes", {
  id: int("id").autoincrement().primaryKey(),
  userId: int("userId").notNull(),
  characterId: int("characterId").notNull(),
  createdAt: timestamp("createdAt").defaultNow().notNull(),
});

export type CharacterLike = typeof characterLikes.$inferSelect;
export type InsertCharacterLike = typeof characterLikes.$inferInsert;

// ============================================================================
// CONTENT REPORTS
// ============================================================================

export const contentReports = mysqlTable("contentReports", {
  id: int("id").autoincrement().primaryKey(),
  reporterId: int("reporterId").notNull(),
  characterId: int("characterId"),
  chatSessionId: int("chatSessionId"),
  messageId: int("messageId"),
  reason: text("reason").notNull(),
  status: mysqlEnum("status", ["pending", "reviewed", "resolved", "dismissed"]).default("pending").notNull(),
  reviewedBy: int("reviewedBy"),
  reviewNotes: text("reviewNotes"),
  createdAt: timestamp("createdAt").defaultNow().notNull(),
  updatedAt: timestamp("updatedAt").defaultNow().onUpdateNow().notNull(),
});

export type ContentReport = typeof contentReports.$inferSelect;
export type InsertContentReport = typeof contentReports.$inferInsert;

// ============================================================================
// SUBSCRIPTION HISTORY
// ============================================================================

export const subscriptionHistory = mysqlTable("subscriptionHistory", {
  id: int("id").autoincrement().primaryKey(),
  userId: int("userId").notNull(),
  tier: mysqlEnum("tier", ["free", "mercury", "mars"]).notNull(),
  startedAt: timestamp("startedAt").defaultNow().notNull(),
  expiresAt: timestamp("expiresAt"),
  paymentMethod: varchar("paymentMethod", { length: 64 }),
  paymentId: varchar("paymentId", { length: 255 }),
  createdAt: timestamp("createdAt").defaultNow().notNull(),
});

export type SubscriptionHistory = typeof subscriptionHistory.$inferSelect;
export type InsertSubscriptionHistory = typeof subscriptionHistory.$inferInsert;
