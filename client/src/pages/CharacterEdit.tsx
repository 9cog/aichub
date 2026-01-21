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
import { Link, useLocation, useParams } from "wouter";
import { ArrowLeft, Sparkles, X, Wand2, Loader2, Save, Download } from "lucide-react";
import { useState, useEffect } from "react";
import { toast } from "sonner";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";

export default function CharacterEdit() {
  const params = useParams<{ id: string }>();
  const characterId = parseInt(params.id || '0');
  const { user, isAuthenticated, loading: authLoading } = useAuth();
  const [, navigate] = useLocation();

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

  const { data: character, isLoading } = trpc.character.get.useQuery(
    { id: characterId },
    { enabled: characterId > 0 }
  );

  const { data: lorebooks } = trpc.lorebook.myLorebooks.useQuery(undefined, {
    enabled: isAuthenticated,
  });

  const { data: exportData } = trpc.character.export.useQuery(
    { id: characterId, format: 'sillytavern' },
    { enabled: characterId > 0 }
  );

  useEffect(() => {
    if (character) {
      setName(character.name);
      setDescription(character.description || '');
      setPersonality(character.personality || '');
      setScenario(character.scenario || '');
      setFirstMessage(character.firstMessage || '');
      setExampleMessages(character.exampleMessages || '');
      setSystemPrompt(character.systemPrompt || '');
      setCreatorNotes(character.creatorNotes || '');
      setAvatarUrl(character.avatarUrl || '');
      setTags((character.tags as string[]) || []);
      setContentRating(character.contentRating);
      setIsPublic(character.isPublic);
      setLorebookId(character.lorebookId || undefined);
    }
  }, [character]);

  const updateMutation = trpc.character.update.useMutation({
    onSuccess: () => {
      toast.success('Character updated!');
    },
    onError: (error) => {
      toast.error(error.message || 'Failed to update character');
    },
  });

  const generateAvatarMutation = trpc.character.generateAvatar.useMutation({
    onSuccess: (data) => {
      setAvatarUrl(data.avatarUrl || '');
      toast.success('Avatar generated!');
    },
  });

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
    updateMutation.mutate({
      id: characterId,
      data: {
        name, description, personality, scenario, firstMessage,
        exampleMessages, systemPrompt, creatorNotes, avatarUrl,
        tags, contentRating, isPublic, lorebookId,
      },
    });
  };

  const handleExport = () => {
    if (exportData) {
      const blob = new Blob([JSON.stringify(exportData, null, 2)], { type: 'application/json' });
      const url = URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `${name || 'character'}.json`;
      a.click();
      URL.revokeObjectURL(url);
      toast.success('Character exported!');
    }
  };

  if (isLoading || authLoading) {
    return (
      <div className="min-h-screen bg-background flex items-center justify-center">
        <Loader2 className="h-8 w-8 animate-spin text-primary" />
      </div>
    );
  }

  if (!character || (character.creatorId !== user?.id && user?.role !== 'admin')) {
    return (
      <div className="min-h-screen bg-background flex items-center justify-center">
        <div className="text-center">
          <h2 className="text-xl font-semibold">Not authorized</h2>
          <Button className="mt-4" asChild>
            <Link href="/">Go Home</Link>
          </Button>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-background">
      <header className="sticky top-0 z-50 border-b border-border/50 bg-background/80 backdrop-blur-lg">
        <div className="container flex h-16 items-center justify-between">
          <div className="flex items-center gap-4">
            <Button variant="ghost" size="icon" asChild>
              <Link href={`/character/${characterId}`}>
                <ArrowLeft className="h-5 w-5" />
              </Link>
            </Button>
            <span className="font-semibold">Edit Character</span>
          </div>
          <div className="flex items-center gap-2">
            <Button variant="outline" size="sm" onClick={handleExport}>
              <Download className="h-4 w-4 mr-2" />
              Export
            </Button>
          </div>
        </div>
      </header>

      <main className="container py-8 max-w-4xl">
        <form onSubmit={handleSubmit} className="space-y-6">
          <Card>
            <CardHeader>
              <CardTitle>Basic Information</CardTitle>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="grid md:grid-cols-2 gap-4">
                <div className="space-y-2">
                  <Label htmlFor="name">Name</Label>
                  <Input id="name" value={name} onChange={(e) => setName(e.target.value)} required />
                </div>
                <div className="space-y-2">
                  <Label>Content Rating</Label>
                  <Select value={contentRating} onValueChange={(v) => setContentRating(v as any)}>
                    <SelectTrigger><SelectValue /></SelectTrigger>
                    <SelectContent>
                      <SelectItem value="sfw">SFW</SelectItem>
                      <SelectItem value="nsfw">NSFW</SelectItem>
                    </SelectContent>
                  </Select>
                </div>
              </div>
              <div className="space-y-2">
                <Label>Description</Label>
                <Textarea value={description} onChange={(e) => setDescription(e.target.value)} rows={3} />
              </div>
              <div className="space-y-2">
                <Label>Tags</Label>
                <div className="flex gap-2">
                  <Input value={tagInput} onChange={(e) => setTagInput(e.target.value)}
                    onKeyDown={(e) => { if (e.key === 'Enter') { e.preventDefault(); handleAddTag(); }}} />
                  <Button type="button" variant="outline" onClick={handleAddTag}>Add</Button>
                </div>
                {tags.length > 0 && (
                  <div className="flex flex-wrap gap-2 mt-2">
                    {tags.map((tag) => (
                      <Badge key={tag} variant="secondary" className="gap-1">
                        {tag}
                        <button type="button" onClick={() => handleRemoveTag(tag)}><X className="h-3 w-3" /></button>
                      </Badge>
                    ))}
                  </div>
                )}
              </div>
            </CardContent>
          </Card>

          <Card>
            <CardHeader><CardTitle>Avatar</CardTitle></CardHeader>
            <CardContent className="space-y-4">
              <div className="flex items-start gap-4">
                <div className="w-32 h-32 rounded-lg bg-muted flex items-center justify-center overflow-hidden">
                  {avatarUrl ? <img src={avatarUrl} alt="Avatar" className="w-full h-full object-cover" />
                    : <Sparkles className="h-8 w-8 text-muted-foreground/50" />}
                </div>
                <div className="flex-1 space-y-2">
                  <Label>Avatar URL</Label>
                  <Input value={avatarUrl} onChange={(e) => setAvatarUrl(e.target.value)} />
                  <Button type="button" variant="outline" onClick={() => generateAvatarMutation.mutate({ description: description || personality || name })}
                    disabled={generateAvatarMutation.isPending}>
                    {generateAvatarMutation.isPending ? <Loader2 className="h-4 w-4 mr-2 animate-spin" /> : <Wand2 className="h-4 w-4 mr-2" />}
                    Generate with AI
                  </Button>
                </div>
              </div>
            </CardContent>
          </Card>

          <Card>
            <CardHeader><CardTitle>Personality & Behavior</CardTitle></CardHeader>
            <CardContent className="space-y-4">
              <div className="space-y-2">
                <Label>Personality</Label>
                <Textarea value={personality} onChange={(e) => setPersonality(e.target.value)} rows={4} />
              </div>
              <div className="space-y-2">
                <Label>Scenario</Label>
                <Textarea value={scenario} onChange={(e) => setScenario(e.target.value)} rows={3} />
              </div>
              <div className="space-y-2">
                <Label>System Prompt</Label>
                <Textarea value={systemPrompt} onChange={(e) => setSystemPrompt(e.target.value)} rows={3} />
              </div>
            </CardContent>
          </Card>

          <Card>
            <CardHeader><CardTitle>Messages</CardTitle></CardHeader>
            <CardContent className="space-y-4">
              <div className="space-y-2">
                <Label>First Message</Label>
                <Textarea value={firstMessage} onChange={(e) => setFirstMessage(e.target.value)} rows={4} />
              </div>
              <div className="space-y-2">
                <Label>Example Messages</Label>
                <Textarea value={exampleMessages} onChange={(e) => setExampleMessages(e.target.value)} rows={4} />
              </div>
            </CardContent>
          </Card>

          <Card>
            <CardHeader><CardTitle>Advanced</CardTitle></CardHeader>
            <CardContent className="space-y-4">
              <div className="space-y-2">
                <Label>Lorebook</Label>
                <Select value={lorebookId?.toString() || 'none'} onValueChange={(v) => setLorebookId(v === 'none' ? undefined : parseInt(v))}>
                  <SelectTrigger><SelectValue placeholder="Select lorebook" /></SelectTrigger>
                  <SelectContent>
                    <SelectItem value="none">No lorebook</SelectItem>
                    {lorebooks?.map((lb) => <SelectItem key={lb.id} value={lb.id.toString()}>{lb.name}</SelectItem>)}
                  </SelectContent>
                </Select>
              </div>
              <div className="space-y-2">
                <Label>Creator Notes</Label>
                <Textarea value={creatorNotes} onChange={(e) => setCreatorNotes(e.target.value)} rows={2} />
              </div>
              <div className="flex items-center justify-between">
                <div>
                  <Label>Make Public</Label>
                  <p className="text-sm text-muted-foreground">Allow others to discover this character</p>
                </div>
                <Switch checked={isPublic} onCheckedChange={setIsPublic} />
              </div>
            </CardContent>
          </Card>

          <div className="flex justify-end gap-4">
            <Button type="button" variant="outline" asChild>
              <Link href={`/character/${characterId}`}>Cancel</Link>
            </Button>
            <Button type="submit" disabled={updateMutation.isPending}>
              {updateMutation.isPending ? <Loader2 className="h-4 w-4 mr-2 animate-spin" /> : <Save className="h-4 w-4 mr-2" />}
              Save Changes
            </Button>
          </div>
        </form>
      </main>
    </div>
  );
}
