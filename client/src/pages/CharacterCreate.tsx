import { useAuth } from "@/_core/hooks/useAuth";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Textarea } from "@/components/ui/textarea";
import { Switch } from "@/components/ui/switch";
import { Badge } from "@/components/ui/badge";
import { trpc } from "@/lib/trpc";
import { getLoginUrl } from "@/const";
import { Link, useLocation } from "wouter";
import { ArrowLeft, Sparkles, Upload, X, Wand2, Loader2 } from "lucide-react";
import { useState, useEffect } from "react";
import { toast } from "sonner";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import {
  Tabs,
  TabsContent,
  TabsList,
  TabsTrigger,
} from "@/components/ui/tabs";

export default function CharacterCreate() {
  const { user, isAuthenticated, loading } = useAuth();
  const [, navigate] = useLocation();

  // Form state
  const [name, setName] = useState('');
  const [description, setDescription] = useState('');
  const [personality, setPersonality] = useState('');
  const [scenario, setScenario] = useState('');
  const [firstMessage, setFirstMessage] = useState('');
  const [exampleMessages, setExampleMessages] = useState('');
  const [systemPrompt, setSystemPrompt] = useState('');
  const [creatorNotes, setCreatorNotes] = useState('');
  const [avatarUrl, setAvatarUrl] = useState('');
  const [tags, setTags] = useState<string[]>([]);
  const [tagInput, setTagInput] = useState('');
  const [contentRating, setContentRating] = useState<'sfw' | 'nsfw'>('sfw');
  const [isPublic, setIsPublic] = useState(false);
  const [lorebookId, setLorebookId] = useState<number | undefined>();

  // Import state
  const [importData, setImportData] = useState('');

  const { data: lorebooks } = trpc.lorebook.myLorebooks.useQuery(undefined, {
    enabled: isAuthenticated,
  });

  const createMutation = trpc.character.create.useMutation({
    onSuccess: (data) => {
      toast.success('Character created successfully!');
      navigate(`/character/${data.id}`);
    },
    onError: (error) => {
      toast.error(error.message || 'Failed to create character');
    },
  });

  const generateAvatarMutation = trpc.character.generateAvatar.useMutation({
    onSuccess: (data) => {
      setAvatarUrl(data.avatarUrl || '');
      toast.success('Avatar generated!');
    },
    onError: () => {
      toast.error('Failed to generate avatar');
    },
  });

  const importMutation = trpc.character.import.useMutation({
    onSuccess: (data) => {
      toast.success('Character imported! Redirecting...');
      navigate(`/character/${data.id}/edit`);
    },
    onError: (error) => {
      toast.error(error.message || 'Failed to import character');
    },
  });

  useEffect(() => {
    if (!loading && !isAuthenticated) {
      window.location.href = getLoginUrl();
    }
  }, [loading, isAuthenticated]);

  const handleAddTag = () => {
    if (tagInput.trim() && !tags.includes(tagInput.trim())) {
      setTags([...tags, tagInput.trim()]);
      setTagInput('');
    }
  };

  const handleRemoveTag = (tag: string) => {
    setTags(tags.filter(t => t !== tag));
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!name.trim()) {
      toast.error('Please enter a character name');
      return;
    }
    createMutation.mutate({
      name,
      description,
      personality,
      scenario,
      firstMessage,
      exampleMessages,
      systemPrompt,
      creatorNotes,
      avatarUrl,
      tags,
      contentRating,
      isPublic,
      lorebookId,
    });
  };

  const handleImport = () => {
    if (!importData.trim()) {
      toast.error('Please paste character data');
      return;
    }
    try {
      JSON.parse(importData);
      importMutation.mutate({ format: 'sillytavern', data: importData });
    } catch {
      toast.error('Invalid JSON format');
    }
  };

  const handleGenerateAvatar = () => {
    const desc = description || personality || name;
    if (!desc) {
      toast.error('Please add a description or personality first');
      return;
    }
    generateAvatarMutation.mutate({ description: desc });
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-background flex items-center justify-center">
        <Loader2 className="h-8 w-8 animate-spin text-primary" />
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-background">
      {/* Header */}
      <header className="sticky top-0 z-50 border-b border-border/50 bg-background/80 backdrop-blur-lg">
        <div className="container flex h-16 items-center justify-between">
          <div className="flex items-center gap-4">
            <Button variant="ghost" size="icon" asChild>
              <Link href="/">
                <ArrowLeft className="h-5 w-5" />
              </Link>
            </Button>
            <span className="font-semibold">Create Character</span>
          </div>
        </div>
      </header>

      <main className="container py-8 max-w-4xl">
        <Tabs defaultValue="create">
          <TabsList className="mb-6">
            <TabsTrigger value="create">Create New</TabsTrigger>
            <TabsTrigger value="import">Import</TabsTrigger>
          </TabsList>

          <TabsContent value="create">
            <form onSubmit={handleSubmit} className="space-y-6">
              {/* Basic Info */}
              <Card>
                <CardHeader>
                  <CardTitle>Basic Information</CardTitle>
                  <CardDescription>The essential details about your character</CardDescription>
                </CardHeader>
                <CardContent className="space-y-4">
                  <div className="grid md:grid-cols-2 gap-4">
                    <div className="space-y-2">
                      <Label htmlFor="name">Name *</Label>
                      <Input
                        id="name"
                        placeholder="Character name"
                        value={name}
                        onChange={(e) => setName(e.target.value)}
                        required
                      />
                    </div>
                    <div className="space-y-2">
                      <Label htmlFor="contentRating">Content Rating</Label>
                      <Select value={contentRating} onValueChange={(v) => setContentRating(v as any)}>
                        <SelectTrigger>
                          <SelectValue />
                        </SelectTrigger>
                        <SelectContent>
                          <SelectItem value="sfw">SFW (Safe for Work)</SelectItem>
                          <SelectItem value="nsfw">NSFW (Adult Content)</SelectItem>
                        </SelectContent>
                      </Select>
                    </div>
                  </div>

                  <div className="space-y-2">
                    <Label htmlFor="description">Description</Label>
                    <Textarea
                      id="description"
                      placeholder="A brief description of your character..."
                      value={description}
                      onChange={(e) => setDescription(e.target.value)}
                      rows={3}
                    />
                  </div>

                  <div className="space-y-2">
                    <Label>Tags</Label>
                    <div className="flex gap-2">
                      <Input
                        placeholder="Add a tag..."
                        value={tagInput}
                        onChange={(e) => setTagInput(e.target.value)}
                        onKeyDown={(e) => {
                          if (e.key === 'Enter') {
                            e.preventDefault();
                            handleAddTag();
                          }
                        }}
                      />
                      <Button type="button" variant="outline" onClick={handleAddTag}>
                        Add
                      </Button>
                    </div>
                    {tags.length > 0 && (
                      <div className="flex flex-wrap gap-2 mt-2">
                        {tags.map((tag) => (
                          <Badge key={tag} variant="secondary" className="gap-1">
                            {tag}
                            <button
                              type="button"
                              onClick={() => handleRemoveTag(tag)}
                              className="ml-1 hover:text-destructive"
                            >
                              <X className="h-3 w-3" />
                            </button>
                          </Badge>
                        ))}
                      </div>
                    )}
                  </div>
                </CardContent>
              </Card>

              {/* Avatar */}
              <Card>
                <CardHeader>
                  <CardTitle>Avatar</CardTitle>
                  <CardDescription>Give your character a face</CardDescription>
                </CardHeader>
                <CardContent className="space-y-4">
                  <div className="flex items-start gap-4">
                    <div className="w-32 h-32 rounded-lg bg-muted flex items-center justify-center overflow-hidden">
                      {avatarUrl ? (
                        <img src={avatarUrl} alt="Avatar" className="w-full h-full object-cover" />
                      ) : (
                        <Sparkles className="h-8 w-8 text-muted-foreground/50" />
                      )}
                    </div>
                    <div className="flex-1 space-y-2">
                      <Label htmlFor="avatarUrl">Avatar URL</Label>
                      <Input
                        id="avatarUrl"
                        placeholder="https://..."
                        value={avatarUrl}
                        onChange={(e) => setAvatarUrl(e.target.value)}
                      />
                      <Button
                        type="button"
                        variant="outline"
                        onClick={handleGenerateAvatar}
                        disabled={generateAvatarMutation.isPending}
                      >
                        {generateAvatarMutation.isPending ? (
                          <Loader2 className="h-4 w-4 mr-2 animate-spin" />
                        ) : (
                          <Wand2 className="h-4 w-4 mr-2" />
                        )}
                        Generate with AI
                      </Button>
                    </div>
                  </div>
                </CardContent>
              </Card>

              {/* Personality */}
              <Card>
                <CardHeader>
                  <CardTitle>Personality & Behavior</CardTitle>
                  <CardDescription>Define how your character acts and speaks</CardDescription>
                </CardHeader>
                <CardContent className="space-y-4">
                  <div className="space-y-2">
                    <Label htmlFor="personality">Personality</Label>
                    <Textarea
                      id="personality"
                      placeholder="Describe your character's personality traits, quirks, and mannerisms..."
                      value={personality}
                      onChange={(e) => setPersonality(e.target.value)}
                      rows={4}
                    />
                  </div>

                  <div className="space-y-2">
                    <Label htmlFor="scenario">Scenario</Label>
                    <Textarea
                      id="scenario"
                      placeholder="The setting or situation where conversations take place..."
                      value={scenario}
                      onChange={(e) => setScenario(e.target.value)}
                      rows={3}
                    />
                  </div>

                  <div className="space-y-2">
                    <Label htmlFor="systemPrompt">System Prompt (Advanced)</Label>
                    <Textarea
                      id="systemPrompt"
                      placeholder="Custom system prompt for the AI..."
                      value={systemPrompt}
                      onChange={(e) => setSystemPrompt(e.target.value)}
                      rows={3}
                    />
                  </div>
                </CardContent>
              </Card>

              {/* Messages */}
              <Card>
                <CardHeader>
                  <CardTitle>Messages</CardTitle>
                  <CardDescription>Set up the conversation flow</CardDescription>
                </CardHeader>
                <CardContent className="space-y-4">
                  <div className="space-y-2">
                    <Label htmlFor="firstMessage">First Message</Label>
                    <Textarea
                      id="firstMessage"
                      placeholder="The first message your character sends when starting a chat..."
                      value={firstMessage}
                      onChange={(e) => setFirstMessage(e.target.value)}
                      rows={4}
                    />
                  </div>

                  <div className="space-y-2">
                    <Label htmlFor="exampleMessages">Example Messages</Label>
                    <Textarea
                      id="exampleMessages"
                      placeholder="Example dialogue to help the AI understand your character's voice..."
                      value={exampleMessages}
                      onChange={(e) => setExampleMessages(e.target.value)}
                      rows={4}
                    />
                  </div>
                </CardContent>
              </Card>

              {/* Advanced */}
              <Card>
                <CardHeader>
                  <CardTitle>Advanced Settings</CardTitle>
                </CardHeader>
                <CardContent className="space-y-4">
                  <div className="space-y-2">
                    <Label htmlFor="lorebook">Lorebook</Label>
                    <Select 
                      value={lorebookId?.toString() || 'none'} 
                      onValueChange={(v) => setLorebookId(v === 'none' ? undefined : parseInt(v))}
                    >
                      <SelectTrigger>
                        <SelectValue placeholder="Select a lorebook (optional)" />
                      </SelectTrigger>
                      <SelectContent>
                        <SelectItem value="none">No lorebook</SelectItem>
                        {lorebooks?.map((lb) => (
                          <SelectItem key={lb.id} value={lb.id.toString()}>
                            {lb.name}
                          </SelectItem>
                        ))}
                      </SelectContent>
                    </Select>
                  </div>

                  <div className="space-y-2">
                    <Label htmlFor="creatorNotes">Creator Notes</Label>
                    <Textarea
                      id="creatorNotes"
                      placeholder="Private notes about this character (not shown to users)..."
                      value={creatorNotes}
                      onChange={(e) => setCreatorNotes(e.target.value)}
                      rows={2}
                    />
                  </div>

                  <div className="flex items-center justify-between">
                    <div>
                      <Label htmlFor="isPublic">Make Public</Label>
                      <p className="text-sm text-muted-foreground">
                        Allow others to discover and chat with this character
                      </p>
                    </div>
                    <Switch
                      id="isPublic"
                      checked={isPublic}
                      onCheckedChange={setIsPublic}
                    />
                  </div>
                </CardContent>
              </Card>

              {/* Submit */}
              <div className="flex justify-end gap-4">
                <Button type="button" variant="outline" asChild>
                  <Link href="/">Cancel</Link>
                </Button>
                <Button type="submit" disabled={createMutation.isPending}>
                  {createMutation.isPending ? (
                    <Loader2 className="h-4 w-4 mr-2 animate-spin" />
                  ) : (
                    <Sparkles className="h-4 w-4 mr-2" />
                  )}
                  Create Character
                </Button>
              </div>
            </form>
          </TabsContent>

          <TabsContent value="import">
            <Card>
              <CardHeader>
                <CardTitle>Import Character</CardTitle>
                <CardDescription>
                  Import a character from TavernAI or SillyTavern format
                </CardDescription>
              </CardHeader>
              <CardContent className="space-y-4">
                <div className="space-y-2">
                  <Label htmlFor="importData">Character JSON</Label>
                  <Textarea
                    id="importData"
                    placeholder="Paste your character JSON here..."
                    value={importData}
                    onChange={(e) => setImportData(e.target.value)}
                    rows={12}
                    className="font-mono text-sm"
                  />
                </div>
                <Button 
                  onClick={handleImport}
                  disabled={importMutation.isPending}
                >
                  {importMutation.isPending ? (
                    <Loader2 className="h-4 w-4 mr-2 animate-spin" />
                  ) : (
                    <Upload className="h-4 w-4 mr-2" />
                  )}
                  Import Character
                </Button>
              </CardContent>
            </Card>
          </TabsContent>
        </Tabs>
      </main>
    </div>
  );
}
