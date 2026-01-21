import { useAuth } from "@/_core/hooks/useAuth";
import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { trpc } from "@/lib/trpc";
import { getLoginUrl } from "@/const";
import { Link, useLocation } from "wouter";
import { ArrowLeft, MessageSquare, Sparkles, Trash2, Loader2 } from "lucide-react";
import { useEffect } from "react";
import { toast } from "sonner";
import { formatDistanceToNow } from "date-fns";

export default function ChatList() {
  const { isAuthenticated, loading } = useAuth();
  const [, navigate] = useLocation();

  const { data: chats, isLoading, refetch } = trpc.chat.list.useQuery(
    { limit: 50 },
    { enabled: isAuthenticated }
  );

  const deleteMutation = trpc.chat.delete.useMutation({
    onSuccess: () => {
      toast.success('Chat deleted');
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
        <div className="container flex h-16 items-center gap-4">
          <Button variant="ghost" size="icon" asChild>
            <Link href="/"><ArrowLeft className="h-5 w-5" /></Link>
          </Button>
          <div className="flex items-center gap-2">
            <MessageSquare className="h-5 w-5 text-primary" />
            <span className="font-semibold">My Chats</span>
          </div>
        </div>
      </header>

      <main className="container py-8 max-w-2xl">
        {chats && chats.length > 0 ? (
          <div className="space-y-2">
            {chats.map((chat) => (
              <Card key={chat.id} className="card-hover cursor-pointer" onClick={() => navigate(`/chat/${chat.id}`)}>
                <CardContent className="p-4">
                  <div className="flex items-center gap-4">
                    <Avatar className="h-12 w-12">
                      <AvatarFallback><Sparkles className="h-6 w-6" /></AvatarFallback>
                    </Avatar>
                    <div className="flex-1 min-w-0">
                      <h3 className="font-semibold truncate">{chat.title || 'Untitled Chat'}</h3>
                      <p className="text-sm text-muted-foreground">
                        {formatDistanceToNow(new Date(chat.updatedAt), { addSuffix: true })}
                      </p>
                    </div>
                    <Button variant="ghost" size="icon" onClick={(e) => {
                      e.stopPropagation();
                      if (confirm('Delete this chat?')) deleteMutation.mutate({ id: chat.id });
                    }}>
                      <Trash2 className="h-4 w-4 text-muted-foreground" />
                    </Button>
                  </div>
                </CardContent>
              </Card>
            ))}
          </div>
        ) : (
          <div className="text-center py-16">
            <MessageSquare className="h-12 w-12 text-muted-foreground/50 mx-auto mb-4" />
            <h3 className="text-lg font-semibold">No chats yet</h3>
            <p className="text-muted-foreground mt-1">Start chatting with a character!</p>
            <Button className="mt-4" asChild><Link href="/discover">Discover Characters</Link></Button>
          </div>
        )}
      </main>
    </div>
  );
}
