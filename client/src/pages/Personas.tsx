import { useAuth } from "@/_core/hooks/useAuth";
import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Textarea } from "@/components/ui/textarea";
import { Switch } from "@/components/ui/switch";
import { trpc } from "@/lib/trpc";
import { getLoginUrl } from "@/const";
import { Link, useLocation } from "wouter";
import { ArrowLeft, Plus, Users, Edit, Trash2, Loader2, Star } from "lucide-react";
import { useEffect, useState } from "react";
import { toast } from "sonner";
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogFooter, DialogTrigger } from "@/components/ui/dialog";

export default function Personas() {
  const { isAuthenticated, loading } = useAuth();
  const [, navigate] = useLocation();
  const [createOpen, setCreateOpen] = useState(false);
  const [editOpen, setEditOpen] = useState(false);
  const [editId, setEditId] = useState<number | null>(null);
  const [name, setName] = useState('');
  const [description, setDescription] = useState('');
  const [isDefault, setIsDefault] = useState(false);

  const { data: personas, isLoading, refetch } = trpc.persona.list.useQuery(
    undefined,
    { enabled: isAuthenticated }
  );

  const createMutation = trpc.persona.create.useMutation({
    onSuccess: () => {
      toast.success('Persona created');
      setCreateOpen(false);
      resetForm();
      refetch();
    },
  });

  const updateMutation = trpc.persona.update.useMutation({
    onSuccess: () => {
      toast.success('Persona updated');
      setEditOpen(false);
      resetForm();
      refetch();
    },
  });

  const deleteMutation = trpc.persona.delete.useMutation({
    onSuccess: () => {
      toast.success('Persona deleted');
      refetch();
    },
  });

  const setDefaultMutation = trpc.persona.setDefault.useMutation({
    onSuccess: () => {
      toast.success('Default persona updated');
      refetch();
    },
  });

  const resetForm = () => {
    setName('');
    setDescription('');
    setIsDefault(false);
    setEditId(null);
  };

  const openEdit = (persona: any) => {
    setEditId(persona.id);
    setName(persona.name);
    setDescription(persona.description || '');
    setIsDefault(persona.isDefault);
    setEditOpen(true);
  };

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
              <Users className="h-5 w-5 text-primary" />
              <span className="font-semibold">Personas</span>
            </div>
          </div>
          <Dialog open={createOpen} onOpenChange={setCreateOpen}>
            <DialogTrigger asChild>
              <Button><Plus className="h-4 w-4 mr-2" />New Persona</Button>
            </DialogTrigger>
            <DialogContent>
              <DialogHeader><DialogTitle>Create Persona</DialogTitle></DialogHeader>
              <div className="space-y-4">
                <div className="space-y-2">
                  <Label>Name</Label>
                  <Input value={name} onChange={(e) => setName(e.target.value)} placeholder="Your persona name" />
                </div>
                <div className="space-y-2">
                  <Label>Description</Label>
                  <Textarea value={description} onChange={(e) => setDescription(e.target.value)} placeholder="Describe this persona..." rows={3} />
                </div>
                <div className="flex items-center justify-between">
                  <Label>Set as default</Label>
                  <Switch checked={isDefault} onCheckedChange={setIsDefault} />
                </div>
              </div>
              <DialogFooter>
                <Button variant="outline" onClick={() => { setCreateOpen(false); resetForm(); }}>Cancel</Button>
                <Button onClick={() => createMutation.mutate({ name, description, isDefault })} disabled={!name.trim() || createMutation.isPending}>
                  {createMutation.isPending ? <Loader2 className="h-4 w-4 animate-spin mr-2" /> : null}
                  Create
                </Button>
              </DialogFooter>
            </DialogContent>
          </Dialog>
        </div>
      </header>

      <main className="container py-8 max-w-2xl">
        <p className="text-muted-foreground mb-6">
          Personas let you define different identities to use when chatting with characters.
        </p>

        {personas && personas.length > 0 ? (
          <div className="space-y-3">
            {personas.map((persona) => (
              <Card key={persona.id} className="card-hover">
                <CardContent className="p-4">
                  <div className="flex items-start justify-between">
                    <div className="flex-1">
                      <div className="flex items-center gap-2">
                        <h3 className="font-semibold">{persona.name}</h3>
                        {persona.isDefault && (
                          <Star className="h-4 w-4 text-yellow-500 fill-yellow-500" />
                        )}
                      </div>
                      {persona.description && (
                        <p className="text-sm text-muted-foreground mt-1">{persona.description}</p>
                      )}
                    </div>
                    <div className="flex gap-2">
                      {!persona.isDefault && (
                        <Button variant="outline" size="sm" onClick={() => setDefaultMutation.mutate({ id: persona.id })}>
                          <Star className="h-4 w-4" />
                        </Button>
                      )}
                      <Button variant="outline" size="sm" onClick={() => openEdit(persona)}>
                        <Edit className="h-4 w-4" />
                      </Button>
                      <Button variant="outline" size="sm" onClick={() => {
                        if (confirm('Delete this persona?')) deleteMutation.mutate({ id: persona.id });
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
            <Users className="h-12 w-12 text-muted-foreground/50 mx-auto mb-4" />
            <h3 className="text-lg font-semibold">No personas yet</h3>
            <p className="text-muted-foreground mt-1">Create a persona to customize your identity in chats.</p>
            <Button className="mt-4" onClick={() => setCreateOpen(true)}><Plus className="h-4 w-4 mr-2" />Create Persona</Button>
          </div>
        )}

        {/* Edit Dialog */}
        <Dialog open={editOpen} onOpenChange={setEditOpen}>
          <DialogContent>
            <DialogHeader><DialogTitle>Edit Persona</DialogTitle></DialogHeader>
            <div className="space-y-4">
              <div className="space-y-2">
                <Label>Name</Label>
                <Input value={name} onChange={(e) => setName(e.target.value)} />
              </div>
              <div className="space-y-2">
                <Label>Description</Label>
                <Textarea value={description} onChange={(e) => setDescription(e.target.value)} rows={3} />
              </div>
            </div>
            <DialogFooter>
              <Button variant="outline" onClick={() => { setEditOpen(false); resetForm(); }}>Cancel</Button>
              <Button onClick={() => editId && updateMutation.mutate({ id: editId, data: { name, description } })} disabled={!name.trim() || updateMutation.isPending}>
                {updateMutation.isPending ? <Loader2 className="h-4 w-4 animate-spin mr-2" /> : null}
                Save
              </Button>
            </DialogFooter>
          </DialogContent>
        </Dialog>
      </main>
    </div>
  );
}
