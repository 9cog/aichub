import { useAuth } from "@/_core/hooks/useAuth";
import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { Badge } from "@/components/ui/badge";
import { Input } from "@/components/ui/input";
import { Tabs, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { trpc } from "@/lib/trpc";
import { Link, useLocation, useSearch } from "wouter";
import { 
  Search, 
  Sparkles, 
  MessageSquare, 
  Heart,
  TrendingUp,
  Clock,
  Star,
  Filter,
  ArrowLeft
} from "lucide-react";
import { useState, useEffect } from "react";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";

function CharacterCard({ character }: { character: any }) {
  return (
    <Link href={`/character/${character.id}`}>
      <Card className="card-hover cursor-pointer overflow-hidden bg-card/50 border-border/50">
        <CardContent className="p-0">
          <div className="aspect-square relative bg-muted">
            {character.avatarUrl ? (
              <img 
                src={character.avatarUrl} 
                alt={character.name}
                className="w-full h-full object-cover"
              />
            ) : (
              <div className="w-full h-full flex items-center justify-center">
                <Sparkles className="h-12 w-12 text-muted-foreground/50" />
              </div>
            )}
            {character.contentRating === 'nsfw' && (
              <Badge variant="destructive" className="absolute top-2 right-2 text-xs">
                NSFW
              </Badge>
            )}
          </div>
          <div className="p-4">
            <h3 className="font-semibold truncate">{character.name}</h3>
            <p className="text-sm text-muted-foreground line-clamp-2 mt-1">
              {character.description || 'No description'}
            </p>
            <div className="flex items-center gap-4 mt-3 text-xs text-muted-foreground">
              <span className="flex items-center gap-1">
                <MessageSquare className="h-3 w-3" />
                {character.chatCount || 0}
              </span>
              <span className="flex items-center gap-1">
                <Heart className="h-3 w-3" />
                {character.likeCount || 0}
              </span>
            </div>
            {character.tags && character.tags.length > 0 && (
              <div className="flex flex-wrap gap-1 mt-3">
                {character.tags.slice(0, 3).map((tag: string) => (
                  <Badge key={tag} variant="secondary" className="text-xs">
                    {tag}
                  </Badge>
                ))}
              </div>
            )}
          </div>
        </CardContent>
      </Card>
    </Link>
  );
}

export default function Discover() {
  const searchParams = new URLSearchParams(window.location.search);
  const initialQuery = searchParams.get('q') || '';
  const initialSort = (searchParams.get('sort') as 'recent' | 'popular' | 'likes') || 'recent';
  
  const [searchQuery, setSearchQuery] = useState(initialQuery);
  const [sortBy, setSortBy] = useState<'recent' | 'popular' | 'likes'>(initialSort);
  const [contentRating, setContentRating] = useState<'sfw' | 'nsfw' | undefined>('sfw');
  const [, navigate] = useLocation();

  const { data: characters, isLoading, refetch } = trpc.character.search.useQuery({
    query: searchQuery || undefined,
    sortBy,
    contentRating,
    limit: 24,
  });

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault();
    refetch();
  };

  return (
    <div className="min-h-screen bg-background">
      {/* Header */}
      <header className="sticky top-0 z-50 border-b border-border/50 bg-background/80 backdrop-blur-lg">
        <div className="container flex h-16 items-center gap-4">
          <Button variant="ghost" size="icon" asChild>
            <Link href="/">
              <ArrowLeft className="h-5 w-5" />
            </Link>
          </Button>
          <div className="flex items-center gap-2">
            <Sparkles className="h-5 w-5 text-primary" />
            <span className="font-semibold">Discover</span>
          </div>
        </div>
      </header>

      <main className="container py-8">
        {/* Search and Filters */}
        <div className="flex flex-col md:flex-row gap-4 mb-8">
          <form onSubmit={handleSearch} className="flex-1 flex gap-2">
            <div className="relative flex-1">
              <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground" />
              <Input 
                placeholder="Search characters..." 
                className="pl-10"
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
              />
            </div>
            <Button type="submit">Search</Button>
          </form>
          
          <div className="flex gap-2">
            <Select value={sortBy} onValueChange={(v) => setSortBy(v as any)}>
              <SelectTrigger className="w-[140px]">
                <SelectValue placeholder="Sort by" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="recent">
                  <div className="flex items-center gap-2">
                    <Clock className="h-4 w-4" />
                    Recent
                  </div>
                </SelectItem>
                <SelectItem value="popular">
                  <div className="flex items-center gap-2">
                    <TrendingUp className="h-4 w-4" />
                    Popular
                  </div>
                </SelectItem>
                <SelectItem value="likes">
                  <div className="flex items-center gap-2">
                    <Heart className="h-4 w-4" />
                    Most Liked
                  </div>
                </SelectItem>
              </SelectContent>
            </Select>
            
            <Select 
              value={contentRating || 'all'} 
              onValueChange={(v) => setContentRating(v === 'all' ? undefined : v as any)}
            >
              <SelectTrigger className="w-[120px]">
                <SelectValue placeholder="Rating" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="all">All</SelectItem>
                <SelectItem value="sfw">SFW Only</SelectItem>
                <SelectItem value="nsfw">NSFW Only</SelectItem>
              </SelectContent>
            </Select>
          </div>
        </div>

        {/* Results */}
        {isLoading ? (
          <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-6 gap-4">
            {[...Array(12)].map((_, i) => (
              <Card key={i} className="animate-pulse">
                <CardContent className="p-0">
                  <div className="aspect-square bg-muted" />
                  <div className="p-4 space-y-2">
                    <div className="h-4 bg-muted rounded w-3/4" />
                    <div className="h-3 bg-muted rounded w-full" />
                  </div>
                </CardContent>
              </Card>
            ))}
          </div>
        ) : characters && characters.length > 0 ? (
          <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-6 gap-4">
            {characters.map((character) => (
              <CharacterCard key={character.id} character={character} />
            ))}
          </div>
        ) : (
          <div className="text-center py-16">
            <Sparkles className="h-12 w-12 text-muted-foreground/50 mx-auto mb-4" />
            <h3 className="text-lg font-semibold">No characters found</h3>
            <p className="text-muted-foreground mt-1">
              Try adjusting your search or filters
            </p>
            <Button className="mt-4" asChild>
              <Link href="/create">Create a Character</Link>
            </Button>
          </div>
        )}
      </main>
    </div>
  );
}
