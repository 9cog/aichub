import { useAuth } from "@/_core/hooks/useAuth";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Textarea } from "@/components/ui/textarea";
import { Badge } from "@/components/ui/badge";
import { trpc } from "@/lib/trpc";
import { getLoginUrl } from "@/const";
import { Link, useParams, useLocation } from "wouter";
import { ArrowLeft, Plus, BookOpen, Trash2, Loader2, Save, X } from "lucide-react";
import { useEffect, useState } from "react";
import { toast } from "sonner";
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogFooter, DialogTrigger } from "@/components/ui/dialog";

interface Entry {
  id?: number;
  keyword: string;
  content: string;
  priority: number;
  enabled: boolean;
}

export default function LorebookEdit() {
  const params = useParams<{ id: string }>();
  const lorebookId = parseInt(params.id || '0');
  const { isAuthenticated, loading: authLoading } = useAuth();
  const [, navigate] = useLocation();

  const [name, setName] = useState('');
  const [description, setDescription] = useState('');
  const [entries, setEntries] = useState<Entry[]>([]);
  const [addOpen, setAddOpen] = useState(false);
  const [newKeyword, setNewKeyword] = useState('');
  const [newContent, setNewContent] = useState('');
  const [newPriority, setNewPriority] = useState(100);

  const { data: lorebook, isLoading, refetch: refetchEntries } = trpc.lorebook.get.useQuery(
    { id: lorebookId },
    { enabled: lorebookId > 0 && isAuthenticated }
  );

  // Entries are included in the get query

  useEffect(() => {
    if (lorebook) {
      setName(lorebook.name);
      setDescription(lorebook.description || '');
    }
  }, [lorebook]);

  useEffect(() => {
    if (lorebook && (lorebook as any).entries) {
      setEntries((lorebook as any).entries.map((e: any) => ({
        id: e.id,
        keyword: e.keys?.join(', ') || '',
        content: e.content,
        priority: e.priority,
        enabled: e.enabled,
      })));
    }
  }, [lorebook]);

  const updateMutation = trpc.lorebook.update.useMutation({
    onSuccess: () => toast.success('Lorebook updated'),
  });

  const addEntryMutation = trpc.lorebook.addEntry.useMutation({
    onSuccess: () => {
      toast.success('Entry added');
      setAddOpen(false);
      setNewKeyword('');
      setNewContent('');
      setNewPriority(100);
      refetchEntries();
    },
  });

  const updateEntryMutation = trpc.lorebook.updateEntry.useMutation({
    onSuccess: () => toast.success('Entry updated'),
  });

  const deleteEntryMutation = trpc.lorebook.deleteEntry.useMutation({
    onSuccess: () => {
      toast.success('Entry deleted');
      refetchEntries();
    },
  });

  useEffect(() => {
    if (!authLoading && !isAuthenticated) {
      window.location.href = getLoginUrl();
    }
  }, [authLoading, isAuthenticated]);

  if (authLoading || isLoading) {
    return (
      <div className="min-h-screen bg-background flex items-center justify-center">
        <Loader2 className="h-8 w-8 animate-spin text-primary" />
      </div>
    );
  }

  if (!lorebook) {
    return (
      <div className="min-h-screen bg-background flex items-center justify-center">
        <div className="text-center">
          <h2 className="text-xl font-semibold">Lorebook not found</h2>
          <Button className="mt-4" asChild><Link href="/lorebooks">Back to Lorebooks</Link></Button>
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
              <Link href="/lorebooks"><ArrowLeft className="h-5 w-5" /></Link>
            </Button>
            <div className="flex items-center gap-2">
              <BookOpen className="h-5 w-5 text-primary" />
              <span className="font-semibold">Edit Lorebook</span>
            </div>
          </div>
          <Button onClick={() => updateMutation.mutate({ id: lorebookId, data: { name, description } })} disabled={updateMutation.isPending}>
            {updateMutation.isPending ? <Loader2 className="h-4 w-4 animate-spin mr-2" /> : <Save className="h-4 w-4 mr-2" />}
            Save
          </Button>
        </div>
      </header>

      <main className="container py-8 max-w-4xl">
        <Card className="mb-6">
          <CardHeader><CardTitle>Lorebook Details</CardTitle></CardHeader>
          <CardContent className="space-y-4">
            <div className="space-y-2">
              <Label>Name</Label>
              <Input value={name} onChange={(e) => setName(e.target.value)} />
            </div>
            <div className="space-y-2">
              <Label>Description</Label>
              <Textarea value={description} onChange={(e) => setDescription(e.target.value)} rows={2} />
            </div>
          </CardContent>
        </Card>

        <div className="flex items-center justify-between mb-4">
          <h2 className="text-xl font-semibold">Entries ({entries.length})</h2>
          <Dialog open={addOpen} onOpenChange={setAddOpen}>
            <DialogTrigger asChild>
              <Button><Plus className="h-4 w-4 mr-2" />Add Entry</Button>
            </DialogTrigger>
            <DialogContent>
              <DialogHeader><DialogTitle>Add Entry</DialogTitle></DialogHeader>
              <div className="space-y-4">
                <div className="space-y-2">
                  <Label>Keywords (comma-separated)</Label>
                  <Input value={newKeyword} onChange={(e) => setNewKeyword(e.target.value)} placeholder="keyword1, keyword2" />
                </div>
                <div className="space-y-2">
                  <Label>Content</Label>
                  <Textarea value={newContent} onChange={(e) => setNewContent(e.target.value)} placeholder="Information to inject when keywords are triggered..." rows={4} />
                </div>
                <div className="space-y-2">
                  <Label>Priority (higher = more important)</Label>
                  <Input type="number" value={newPriority} onChange={(e) => setNewPriority(parseInt(e.target.value) || 100)} />
                </div>
              </div>
              <DialogFooter>
                <Button variant="outline" onClick={() => setAddOpen(false)}>Cancel</Button>
                <Button onClick={() => addEntryMutation.mutate({ lorebookId, entry: { keys: newKeyword.split(',').map(k => k.trim()), content: newContent, priority: newPriority } })} disabled={!newKeyword.trim() || !newContent.trim() || addEntryMutation.isPending}>
                  {addEntryMutation.isPending ? <Loader2 className="h-4 w-4 animate-spin mr-2" /> : null}
                  Add
                </Button>
              </DialogFooter>
            </DialogContent>
          </Dialog>
        </div>

        {entries.length > 0 ? (
          <div className="space-y-3">
            {entries.map((entry, idx) => (
              <Card key={entry.id || idx}>
                <CardContent className="p-4">
                  <div className="flex items-start justify-between gap-4">
                    <div className="flex-1 min-w-0">
                      <div className="flex items-center gap-2 flex-wrap">
                        {entry.keyword.split(',').map((kw, i) => (
                          <Badge key={i} variant="secondary">{kw.trim()}</Badge>
                        ))}
                        <Badge variant="outline">Priority: {entry.priority}</Badge>
                      </div>
                      <p className="text-sm text-muted-foreground mt-2 line-clamp-2">{entry.content}</p>
                    </div>
                    <Button variant="ghost" size="icon" onClick={() => {
                      if (entry.id && confirm('Delete this entry?')) deleteEntryMutation.mutate({ id: entry.id });
                    }}>
                      <Trash2 className="h-4 w-4 text-muted-foreground" />
                    </Button>
                  </div>
                </CardContent>
              </Card>
            ))}
          </div>
        ) : (
          <div className="text-center py-12 text-muted-foreground">
            <BookOpen className="h-12 w-12 mx-auto mb-4 opacity-50" />
            <p>No entries yet. Add entries to provide context during conversations.</p>
          </div>
        )}
      </main>
    </div>
  );
}
