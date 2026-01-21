import { useAuth } from "@/_core/hooks/useAuth";
import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { trpc } from "@/lib/trpc";
import { getLoginUrl } from "@/const";
import { Link, useLocation } from "wouter";
import { ArrowLeft, Plus, Sparkles, Edit, Trash2, Loader2, MessageSquare, Heart, Eye, EyeOff } from "lucide-react";
import { useEffect } from "react";
import { toast } from "sonner";

export default function MyCharacters() {
  const { isAuthenticated, loading } = useAuth();
  const [, navigate] = useLocation();

  const { data: characters, isLoading, refetch } = trpc.character.myCharacters.useQuery(
    {},
    { enabled: isAuthenticated }
  );

  const deleteMutation = trpc.character.delete.useMutation({
    onSuccess: () => {
      toast.success('Character deleted');
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
            <span className="font-semibold">My Characters</span>
          </div>
          <Button asChild>
            <Link href="/create"><Plus className="h-4 w-4 mr-2" />Create</Link>
          </Button>
        </div>
      </header>

      <main className="container py-8">
        {characters && characters.length > 0 ? (
          <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5 gap-4">
            {characters.map((character) => (
              <Card key={character.id} className="card-hover overflow-hidden">
                <CardContent className="p-0">
                  <Link href={`/character/${character.id}`}>
                    <div className="aspect-square relative bg-muted">
                      {character.avatarUrl ? (
                        <img src={character.avatarUrl} alt={character.name} className="w-full h-full object-cover" />
                      ) : (
                        <div className="w-full h-full flex items-center justify-center">
                          <Sparkles className="h-12 w-12 text-muted-foreground/50" />
                        </div>
                      )}
                      <div className="absolute top-2 right-2 flex gap-1">
                        {character.contentRating === 'nsfw' && (
                          <Badge variant="destructive" className="text-xs">NSFW</Badge>
                        )}
                        {character.isPublic ? (
                          <Badge variant="secondary" className="text-xs"><Eye className="h-3 w-3" /></Badge>
                        ) : (
                          <Badge variant="outline" className="text-xs bg-background/80"><EyeOff className="h-3 w-3" /></Badge>
                        )}
                      </div>
                    </div>
                  </Link>
                  <div className="p-4">
                    <h3 className="font-semibold truncate">{character.name}</h3>
                    <div className="flex items-center gap-3 mt-2 text-xs text-muted-foreground">
                      <span className="flex items-center gap-1"><MessageSquare className="h-3 w-3" />{character.chatCount || 0}</span>
                      <span className="flex items-center gap-1"><Heart className="h-3 w-3" />{character.likeCount || 0}</span>
                    </div>
                    <div className="flex gap-2 mt-3">
                      <Button variant="outline" size="sm" className="flex-1" asChild>
                        <Link href={`/character/${character.id}/edit`}><Edit className="h-3 w-3 mr-1" />Edit</Link>
                      </Button>
                      <Button variant="outline" size="sm" onClick={() => {
                        if (confirm('Delete this character?')) deleteMutation.mutate({ id: character.id });
                      }}>
                        <Trash2 className="h-3 w-3" />
                      </Button>
                    </div>
                  </div>
                </CardContent>
              </Card>
            ))}
          </div>
        ) : (
          <div className="text-center py-16">
            <Sparkles className="h-12 w-12 text-muted-foreground/50 mx-auto mb-4" />
            <h3 className="text-lg font-semibold">No characters yet</h3>
            <p className="text-muted-foreground mt-1">Create your first AI character!</p>
            <Button className="mt-4" asChild><Link href="/create"><Plus className="h-4 w-4 mr-2" />Create Character</Link></Button>
          </div>
        )}
      </main>
    </div>
  );
}
