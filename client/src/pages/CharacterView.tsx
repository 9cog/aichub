import { useAuth } from "@/_core/hooks/useAuth";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { Badge } from "@/components/ui/badge";
import { trpc } from "@/lib/trpc";
import { getLoginUrl } from "@/const";
import { Link, useParams, useLocation } from "wouter";
import { 
  ArrowLeft, 
  Heart, 
  MessageSquare, 
  Edit, 
  Share2, 
  Flag,
  Sparkles,
  BookOpen,
  Play
} from "lucide-react";
import { toast } from "sonner";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";
import { Textarea } from "@/components/ui/textarea";
import { useState } from "react";

export default function CharacterView() {
  const params = useParams<{ id: string }>();
  const characterId = parseInt(params.id || '0');
  const { user, isAuthenticated } = useAuth();
  const [, navigate] = useLocation();
  const [reportReason, setReportReason] = useState('');
  const [reportDialogOpen, setReportDialogOpen] = useState(false);

  const { data: character, isLoading, error } = trpc.character.get.useQuery(
    { id: characterId },
    { enabled: characterId > 0 }
  );

  const { data: hasLiked } = trpc.character.hasLiked.useQuery(
    { characterId },
    { enabled: isAuthenticated && characterId > 0 }
  );

  const likeMutation = trpc.character.like.useMutation({
    onSuccess: () => {
      toast.success(hasLiked ? 'Removed from favorites' : 'Added to favorites');
    },
  });

  const createChatMutation = trpc.chat.create.useMutation({
    onSuccess: (data) => {
      navigate(`/chat/${data.id}`);
    },
    onError: () => {
      toast.error('Failed to start chat');
    },
  });

  const reportMutation = trpc.report.create.useMutation({
    onSuccess: () => {
      toast.success('Report submitted. Thank you for helping keep our community safe.');
      setReportDialogOpen(false);
      setReportReason('');
    },
    onError: () => {
      toast.error('Failed to submit report');
    },
  });

  const handleStartChat = () => {
    if (!isAuthenticated) {
      window.location.href = getLoginUrl();
      return;
    }
    createChatMutation.mutate({ characterId });
  };

  const handleLike = () => {
    if (!isAuthenticated) {
      window.location.href = getLoginUrl();
      return;
    }
    likeMutation.mutate({ characterId });
  };

  const handleReport = () => {
    if (reportReason.length < 10) {
      toast.error('Please provide more details about your report');
      return;
    }
    reportMutation.mutate({ characterId, reason: reportReason });
  };

  if (isLoading) {
    return (
      <div className="min-h-screen bg-background flex items-center justify-center">
        <div className="animate-pulse text-muted-foreground">Loading...</div>
      </div>
    );
  }

  if (error || !character) {
    return (
      <div className="min-h-screen bg-background flex items-center justify-center">
        <div className="text-center">
          <Sparkles className="h-12 w-12 text-muted-foreground/50 mx-auto mb-4" />
          <h2 className="text-xl font-semibold">Character not found</h2>
          <Button className="mt-4" asChild>
            <Link href="/discover">Browse Characters</Link>
          </Button>
        </div>
      </div>
    );
  }

  const isOwner = user?.id === character.creatorId;

  return (
    <div className="min-h-screen bg-background">
      {/* Header */}
      <header className="sticky top-0 z-50 border-b border-border/50 bg-background/80 backdrop-blur-lg">
        <div className="container flex h-16 items-center justify-between">
          <div className="flex items-center gap-4">
            <Button variant="ghost" size="icon" asChild>
              <Link href="/discover">
                <ArrowLeft className="h-5 w-5" />
              </Link>
            </Button>
            <span className="font-semibold truncate">{character.name}</span>
          </div>
          <div className="flex items-center gap-2">
            {isOwner && (
              <Button variant="outline" size="sm" asChild>
                <Link href={`/character/${characterId}/edit`}>
                  <Edit className="h-4 w-4 mr-2" />
                  Edit
                </Link>
              </Button>
            )}
          </div>
        </div>
      </header>

      <main className="container py-8">
        <div className="grid lg:grid-cols-3 gap-8">
          {/* Left column - Avatar and actions */}
          <div className="lg:col-span-1">
            <Card className="overflow-hidden">
              <div className="aspect-square relative bg-muted">
                {character.avatarUrl ? (
                  <img 
                    src={character.avatarUrl} 
                    alt={character.name}
                    className="w-full h-full object-cover"
                  />
                ) : (
                  <div className="w-full h-full flex items-center justify-center">
                    <Sparkles className="h-16 w-16 text-muted-foreground/50" />
                  </div>
                )}
                {character.contentRating === 'nsfw' && (
                  <Badge variant="destructive" className="absolute top-4 right-4">
                    NSFW
                  </Badge>
                )}
              </div>
              <CardContent className="p-6">
                <div className="flex items-center justify-between mb-4">
                  <div className="flex items-center gap-4 text-sm text-muted-foreground">
                    <span className="flex items-center gap-1">
                      <MessageSquare className="h-4 w-4" />
                      {character.chatCount || 0} chats
                    </span>
                    <span className="flex items-center gap-1">
                      <Heart className="h-4 w-4" />
                      {character.likeCount || 0} likes
                    </span>
                  </div>
                </div>

                <Button 
                  className="w-full mb-3" 
                  size="lg"
                  onClick={handleStartChat}
                  disabled={createChatMutation.isPending}
                >
                  <Play className="h-4 w-4 mr-2" />
                  {createChatMutation.isPending ? 'Starting...' : 'Start Chat'}
                </Button>

                <div className="flex gap-2">
                  <Button 
                    variant="outline" 
                    className="flex-1"
                    onClick={handleLike}
                    disabled={likeMutation.isPending}
                  >
                    <Heart className={`h-4 w-4 mr-2 ${hasLiked ? 'fill-current text-red-500' : ''}`} />
                    {hasLiked ? 'Liked' : 'Like'}
                  </Button>
                  
                  <Dialog open={reportDialogOpen} onOpenChange={setReportDialogOpen}>
                    <DialogTrigger asChild>
                      <Button variant="outline" size="icon">
                        <Flag className="h-4 w-4" />
                      </Button>
                    </DialogTrigger>
                    <DialogContent>
                      <DialogHeader>
                        <DialogTitle>Report Character</DialogTitle>
                        <DialogDescription>
                          Please describe why you're reporting this character. Reports help us maintain a safe community.
                        </DialogDescription>
                      </DialogHeader>
                      <Textarea
                        placeholder="Describe the issue..."
                        value={reportReason}
                        onChange={(e) => setReportReason(e.target.value)}
                        rows={4}
                      />
                      <DialogFooter>
                        <Button variant="outline" onClick={() => setReportDialogOpen(false)}>
                          Cancel
                        </Button>
                        <Button 
                          onClick={handleReport}
                          disabled={reportMutation.isPending}
                        >
                          Submit Report
                        </Button>
                      </DialogFooter>
                    </DialogContent>
                  </Dialog>
                </div>
              </CardContent>
            </Card>
          </div>

          {/* Right column - Details */}
          <div className="lg:col-span-2 space-y-6">
            <div>
              <h1 className="text-3xl font-bold">{character.name}</h1>
              {character.tags && character.tags.length > 0 && (
                <div className="flex flex-wrap gap-2 mt-3">
                  {(character.tags as string[]).map((tag: string) => (
                    <Badge key={tag} variant="secondary">
                      {tag}
                    </Badge>
                  ))}
                </div>
              )}
            </div>

            {character.description && (
              <Card>
                <CardHeader>
                  <CardTitle className="text-lg">Description</CardTitle>
                </CardHeader>
                <CardContent>
                  <p className="whitespace-pre-wrap">{character.description}</p>
                </CardContent>
              </Card>
            )}

            {character.personality && (
              <Card>
                <CardHeader>
                  <CardTitle className="text-lg">Personality</CardTitle>
                </CardHeader>
                <CardContent>
                  <p className="whitespace-pre-wrap">{character.personality}</p>
                </CardContent>
              </Card>
            )}

            {character.scenario && (
              <Card>
                <CardHeader>
                  <CardTitle className="text-lg">Scenario</CardTitle>
                </CardHeader>
                <CardContent>
                  <p className="whitespace-pre-wrap">{character.scenario}</p>
                </CardContent>
              </Card>
            )}

            {character.firstMessage && (
              <Card>
                <CardHeader>
                  <CardTitle className="text-lg">First Message</CardTitle>
                </CardHeader>
                <CardContent>
                  <div className="bg-muted/50 rounded-lg p-4">
                    <p className="whitespace-pre-wrap italic">{character.firstMessage}</p>
                  </div>
                </CardContent>
              </Card>
            )}

            {character.lorebookId && (
              <Card>
                <CardHeader>
                  <CardTitle className="text-lg flex items-center gap-2">
                    <BookOpen className="h-5 w-5" />
                    Lorebook Attached
                  </CardTitle>
                </CardHeader>
                <CardContent>
                  <p className="text-muted-foreground">
                    This character has a lorebook with world-building information that will be used during conversations.
                  </p>
                </CardContent>
              </Card>
            )}
          </div>
        </div>
      </main>
    </div>
  );
}
