import { useAuth } from "@/_core/hooks/useAuth";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Textarea } from "@/components/ui/textarea";
import { trpc } from "@/lib/trpc";
import { getLoginUrl } from "@/const";
import { Link, useLocation } from "wouter";
import { ArrowLeft, Plus, BookOpen, Edit, Trash2, Loader2 } from "lucide-react";
import { useEffect, useState } from "react";
import { toast } from "sonner";
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogFooter, DialogTrigger } from "@/components/ui/dialog";

export default function Lorebooks() {
  const { isAuthenticated, loading } = useAuth();
  const [, navigate] = useLocation();
  const [createOpen, setCreateOpen] = useState(false);
  const [name, setName] = useState('');
  const [description, setDescription] = useState('');

  const { data: lorebooks, isLoading, refetch } = trpc.lorebook.myLorebooks.useQuery(
    undefined,
    { enabled: isAuthenticated }
  );

  const createMutation = trpc.lorebook.create.useMutation({
    onSuccess: (data) => {
      toast.success('Lorebook created');
      setCreateOpen(false);
      setName('');
      setDescription('');
      navigate(`/lorebook/${data.id}`);
    },
  });

  const deleteMutation = trpc.lorebook.delete.useMutation({
    onSuccess: () => {
      toast.success('Lorebook deleted');
      refetch();
    },
  });

  useEffect(() => {
    if (!loading && !isAuthenticated) {
      window.location.href = getLoginUrl();
    }
  }, [loading, isAuthenticated]);

  if (loading || isLoading) {
    return (
      <div className="min-h-screen bg-background flex items-center justify-center">
        <Loader2 className="h-8 w-8 animate-spin text-primary" />
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-background">
      <header className="sticky top-0 z-50 border-b border-border/50 bg-background/80 backdrop-blur-lg">
        <div className="container flex h-16 items-center justify-between">
          <div className="flex items-center gap-4">
            <Button variant="ghost" size="icon" asChild>
              <Link href="/"><ArrowLeft className="h-5 w-5" /></Link>
            </Button>
            <div className="flex items-center gap-2">
              <BookOpen className="h-5 w-5 text-primary" />
              <span className="font-semibold">Lorebooks</span>
            </div>
          </div>
          <Dialog open={createOpen} onOpenChange={setCreateOpen}>
            <DialogTrigger asChild>
              <Button><Plus className="h-4 w-4 mr-2" />New Lorebook</Button>
            </DialogTrigger>
            <DialogContent>
              <DialogHeader>
                <DialogTitle>Create Lorebook</DialogTitle>
              </DialogHeader>
              <div className="space-y-4">
                <div className="space-y-2">
                  <Label>Name</Label>
                  <Input value={name} onChange={(e) => setName(e.target.value)} placeholder="My Lorebook" />
                </div>
                <div className="space-y-2">
                  <Label>Description</Label>
                  <Textarea value={description} onChange={(e) => setDescription(e.target.value)} placeholder="What is this lorebook about?" rows={3} />
                </div>
              </div>
              <DialogFooter>
                <Button variant="outline" onClick={() => setCreateOpen(false)}>Cancel</Button>
                <Button onClick={() => createMutation.mutate({ name, description })} disabled={!name.trim() || createMutation.isPending}>
                  {createMutation.isPending ? <Loader2 className="h-4 w-4 animate-spin mr-2" /> : null}
                  Create
                </Button>
              </DialogFooter>
            </DialogContent>
          </Dialog>
        </div>
      </header>

      <main className="container py-8 max-w-4xl">
        {lorebooks && lorebooks.length > 0 ? (
          <div className="grid gap-4">
            {lorebooks.map((lb) => (
              <Card key={lb.id} className="card-hover">
                <CardContent className="p-6">
                  <div className="flex items-start justify-between">
                    <div className="flex-1">
                      <h3 className="font-semibold text-lg">{lb.name}</h3>
                      {lb.description && <p className="text-muted-foreground mt-1">{lb.description}</p>}
                      <p className="text-sm text-muted-foreground mt-2">{(lb as any).entryCount || 0} entries</p>
                    </div>
                    <div className="flex gap-2">
                      <Button variant="outline" size="sm" asChild>
                        <Link href={`/lorebook/${lb.id}`}><Edit className="h-4 w-4 mr-1" />Edit</Link>
                      </Button>
                      <Button variant="outline" size="sm" onClick={() => {
                        if (confirm('Delete this lorebook?')) deleteMutation.mutate({ id: lb.id });
                      }}>
                        <Trash2 className="h-4 w-4" />
                      </Button>
                    </div>
                  </div>
                </CardContent>
              </Card>
            ))}
          </div>
        ) : (
          <div className="text-center py-16">
            <BookOpen className="h-12 w-12 text-muted-foreground/50 mx-auto mb-4" />
            <h3 className="text-lg font-semibold">No lorebooks yet</h3>
            <p className="text-muted-foreground mt-1">Create a lorebook to add world-building context to your characters.</p>
            <Button className="mt-4" onClick={() => setCreateOpen(true)}><Plus className="h-4 w-4 mr-2" />Create Lorebook</Button>
          </div>
        )}
      </main>
    </div>
  );
}
