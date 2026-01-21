import { useAuth } from "@/_core/hooks/useAuth";
import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { Badge } from "@/components/ui/badge";
import { Input } from "@/components/ui/input";
import { trpc } from "@/lib/trpc";
import { getLoginUrl } from "@/const";
import { Link, useLocation } from "wouter";
import { 
  Search, 
  Sparkles, 
  MessageSquare, 
  Users, 
  BookOpen, 
  Plus,
  Heart,
  TrendingUp,
  Clock,
  Menu,
  X,
  LogOut,
  Settings,
  User as UserIcon
} from "lucide-react";
import { useState } from "react";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";

function Navbar() {
  const { user, isAuthenticated, logout } = useAuth();
  const [, navigate] = useLocation();
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);

  return (
    <nav className="sticky top-0 z-50 border-b border-border/50 bg-background/80 backdrop-blur-lg">
      <div className="container flex h-16 items-center justify-between">
        <div className="flex items-center gap-8">
          <Link href="/" className="flex items-center gap-2">
            <Sparkles className="h-6 w-6 text-primary" />
            <span className="text-xl font-bold gradient-text">AIChub</span>
          </Link>
          
          <div className="hidden md:flex items-center gap-6">
            <Link href="/discover" className="text-muted-foreground hover:text-foreground transition-colors">
              Discover
            </Link>
            {isAuthenticated && (
              <>
                <Link href="/chats" className="text-muted-foreground hover:text-foreground transition-colors">
                  Chats
                </Link>
                <Link href="/my-characters" className="text-muted-foreground hover:text-foreground transition-colors">
                  My Characters
                </Link>
              </>
            )}
          </div>
        </div>

        <div className="flex items-center gap-4">
          {isAuthenticated ? (
            <>
              <Button variant="outline" size="sm" asChild className="hidden sm:flex">
                <Link href="/create">
                  <Plus className="h-4 w-4 mr-2" />
                  Create
                </Link>
              </Button>
              
              <DropdownMenu>
                <DropdownMenuTrigger asChild>
                  <Button variant="ghost" size="icon" className="rounded-full">
                    <Avatar className="h-8 w-8">
                      <AvatarFallback>{user?.name?.[0] || 'U'}</AvatarFallback>
                    </Avatar>
                  </Button>
                </DropdownMenuTrigger>
                <DropdownMenuContent align="end" className="w-56">
                  <div className="px-2 py-1.5">
                    <p className="text-sm font-medium">{user?.name || 'User'}</p>
                    <p className="text-xs text-muted-foreground">{user?.email}</p>
                  </div>
                  <DropdownMenuSeparator />
                  <DropdownMenuItem onClick={() => navigate('/my-characters')}>
                    <UserIcon className="h-4 w-4 mr-2" />
                    My Characters
                  </DropdownMenuItem>
                  <DropdownMenuItem onClick={() => navigate('/personas')}>
                    <Users className="h-4 w-4 mr-2" />
                    Personas
                  </DropdownMenuItem>
                  <DropdownMenuItem onClick={() => navigate('/lorebooks')}>
                    <BookOpen className="h-4 w-4 mr-2" />
                    Lorebooks
                  </DropdownMenuItem>
                  <DropdownMenuItem onClick={() => navigate('/settings')}>
                    <Settings className="h-4 w-4 mr-2" />
                    Settings
                  </DropdownMenuItem>
                  {user?.role === 'admin' && (
                    <>
                      <DropdownMenuSeparator />
                      <DropdownMenuItem onClick={() => navigate('/admin')}>
                        <Sparkles className="h-4 w-4 mr-2" />
                        Admin Panel
                      </DropdownMenuItem>
                    </>
                  )}
                  <DropdownMenuSeparator />
                  <DropdownMenuItem onClick={() => logout()}>
                    <LogOut className="h-4 w-4 mr-2" />
                    Sign Out
                  </DropdownMenuItem>
                </DropdownMenuContent>
              </DropdownMenu>
            </>
          ) : (
            <Button asChild>
              <a href={getLoginUrl()}>Sign In</a>
            </Button>
          )}
          
          <Button 
            variant="ghost" 
            size="icon" 
            className="md:hidden"
            onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
          >
            {mobileMenuOpen ? <X className="h-5 w-5" /> : <Menu className="h-5 w-5" />}
          </Button>
        </div>
      </div>
      
      {/* Mobile menu */}
      {mobileMenuOpen && (
        <div className="md:hidden border-t border-border/50 bg-background p-4">
          <div className="flex flex-col gap-4">
            <Link href="/discover" className="text-muted-foreground hover:text-foreground">
              Discover
            </Link>
            {isAuthenticated && (
              <>
                <Link href="/chats" className="text-muted-foreground hover:text-foreground">
                  Chats
                </Link>
                <Link href="/my-characters" className="text-muted-foreground hover:text-foreground">
                  My Characters
                </Link>
                <Link href="/create" className="text-muted-foreground hover:text-foreground">
                  Create Character
                </Link>
              </>
            )}
          </div>
        </div>
      )}
    </nav>
  );
}

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

function HeroSection() {
  const { isAuthenticated } = useAuth();
  const [searchQuery, setSearchQuery] = useState('');
  const [, navigate] = useLocation();

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault();
    if (searchQuery.trim()) {
      navigate(`/discover?q=${encodeURIComponent(searchQuery)}`);
    }
  };

  return (
    <section className="relative py-20 overflow-hidden">
      {/* Background gradient */}
      <div className="absolute inset-0 bg-gradient-to-b from-primary/5 via-transparent to-transparent" />
      
      <div className="container relative">
        <div className="max-w-3xl mx-auto text-center">
          <h1 className="text-4xl md:text-6xl font-bold tracking-tight">
            Chat with <span className="gradient-text">AI Characters</span>
          </h1>
          <p className="mt-6 text-lg text-muted-foreground">
            Create, discover, and interact with AI-powered characters. 
            Build immersive conversations with personalities crafted by the community.
          </p>
          
          <form onSubmit={handleSearch} className="mt-8 flex gap-2 max-w-md mx-auto">
            <div className="relative flex-1">
              <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground" />
              <Input 
                placeholder="Search characters..." 
                className="pl-10 bg-background/50"
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
              />
            </div>
            <Button type="submit">Search</Button>
          </form>
          
          <div className="mt-8 flex flex-wrap justify-center gap-4">
            <Button size="lg" asChild>
              <Link href="/discover">
                <Sparkles className="h-4 w-4 mr-2" />
                Explore Characters
              </Link>
            </Button>
            {isAuthenticated ? (
              <Button size="lg" variant="outline" asChild>
                <Link href="/create">
                  <Plus className="h-4 w-4 mr-2" />
                  Create Character
                </Link>
              </Button>
            ) : (
              <Button size="lg" variant="outline" asChild>
                <a href={getLoginUrl()}>Get Started</a>
              </Button>
            )}
          </div>
        </div>
      </div>
    </section>
  );
}

function FeaturedSection() {
  const { data: trendingCharacters, isLoading: trendingLoading } = trpc.character.search.useQuery({
    sortBy: 'popular',
    limit: 8,
    contentRating: 'sfw',
  });

  const { data: recentCharacters, isLoading: recentLoading } = trpc.character.search.useQuery({
    sortBy: 'recent',
    limit: 8,
    contentRating: 'sfw',
  });

  return (
    <section className="py-16">
      <div className="container">
        {/* Trending */}
        <div className="mb-12">
          <div className="flex items-center justify-between mb-6">
            <div className="flex items-center gap-2">
              <TrendingUp className="h-5 w-5 text-primary" />
              <h2 className="text-2xl font-bold">Trending</h2>
            </div>
            <Button variant="ghost" asChild>
              <Link href="/discover?sort=popular">View All</Link>
            </Button>
          </div>
          
          {trendingLoading ? (
            <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
              {[...Array(4)].map((_, i) => (
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
          ) : (
            <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
              {trendingCharacters?.map((character) => (
                <CharacterCard key={character.id} character={character} />
              ))}
            </div>
          )}
        </div>

        {/* Recent */}
        <div>
          <div className="flex items-center justify-between mb-6">
            <div className="flex items-center gap-2">
              <Clock className="h-5 w-5 text-primary" />
              <h2 className="text-2xl font-bold">Recently Added</h2>
            </div>
            <Button variant="ghost" asChild>
              <Link href="/discover?sort=recent">View All</Link>
            </Button>
          </div>
          
          {recentLoading ? (
            <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
              {[...Array(4)].map((_, i) => (
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
          ) : (
            <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
              {recentCharacters?.map((character) => (
                <CharacterCard key={character.id} character={character} />
              ))}
            </div>
          )}
        </div>
      </div>
    </section>
  );
}

function FeaturesSection() {
  const features = [
    {
      icon: <Sparkles className="h-6 w-6" />,
      title: "AI-Powered Characters",
      description: "Engage with characters powered by advanced AI models for natural, immersive conversations."
    },
    {
      icon: <BookOpen className="h-6 w-6" />,
      title: "Lorebooks",
      description: "Create rich world-building with lorebooks that inject context into your conversations."
    },
    {
      icon: <Users className="h-6 w-6" />,
      title: "Personas",
      description: "Define your own identity with custom personas for different roleplay scenarios."
    },
    {
      icon: <MessageSquare className="h-6 w-6" />,
      title: "Import & Export",
      description: "Compatible with TavernAI and SillyTavern formats for easy character sharing."
    },
  ];

  return (
    <section className="py-16 bg-muted/30">
      <div className="container">
        <div className="text-center mb-12">
          <h2 className="text-3xl font-bold">Everything You Need</h2>
          <p className="mt-2 text-muted-foreground">
            A complete platform for AI character interactions
          </p>
        </div>
        
        <div className="grid md:grid-cols-2 lg:grid-cols-4 gap-6">
          {features.map((feature, i) => (
            <Card key={i} className="bg-card/50 border-border/50">
              <CardContent className="p-6">
                <div className="h-12 w-12 rounded-lg bg-primary/10 flex items-center justify-center text-primary mb-4">
                  {feature.icon}
                </div>
                <h3 className="font-semibold mb-2">{feature.title}</h3>
                <p className="text-sm text-muted-foreground">{feature.description}</p>
              </CardContent>
            </Card>
          ))}
        </div>
      </div>
    </section>
  );
}

export default function Home() {
  return (
    <div className="min-h-screen bg-background">
      <Navbar />
      <HeroSection />
      <FeaturedSection />
      <FeaturesSection />
      
      {/* Footer */}
      <footer className="border-t border-border/50 py-8">
        <div className="container">
          <div className="flex flex-col md:flex-row justify-between items-center gap-4">
            <div className="flex items-center gap-2">
              <Sparkles className="h-5 w-5 text-primary" />
              <span className="font-semibold">AIChub</span>
            </div>
            <p className="text-sm text-muted-foreground">
              © 2026 AIChub. All rights reserved.
            </p>
          </div>
        </div>
      </footer>
    </div>
  );
}
