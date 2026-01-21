import { useAuth } from "@/_core/hooks/useAuth";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Badge } from "@/components/ui/badge";
import { trpc } from "@/lib/trpc";
import { getLoginUrl } from "@/const";
import { Link } from "wouter";
import { ArrowLeft, Settings as SettingsIcon, Loader2, Crown, Sparkles, Zap } from "lucide-react";
import { useEffect } from "react";

const SUBSCRIPTION_TIERS = {
  free: {
    name: 'Free',
    icon: Sparkles,
    color: 'text-muted-foreground',
    models: ['Basic models'],
    features: ['5 chats per day', 'Basic characters', 'Community support'],
  },
  mercury: {
    name: 'Mercury',
    icon: Zap,
    color: 'text-blue-500',
    models: ['GPT-4o-mini', 'Claude Haiku'],
    features: ['Unlimited chats', 'Priority support', 'Early access features'],
  },
  mars: {
    name: 'Mars',
    icon: Crown,
    color: 'text-yellow-500',
    models: ['GPT-4o', 'Claude Sonnet', 'All models'],
    features: ['Everything in Mercury', 'Premium models', 'API access', 'Custom integrations'],
  },
};

export default function Settings() {
  const { user, isAuthenticated, loading } = useAuth();

  useEffect(() => {
    if (!loading && !isAuthenticated) {
      window.location.href = getLoginUrl();
    }
  }, [loading, isAuthenticated]);

  if (loading) {
    return (
      <div className="min-h-screen bg-background flex items-center justify-center">
        <Loader2 className="h-8 w-8 animate-spin text-primary" />
      </div>
    );
  }

  const currentTier = (user as any)?.subscriptionTier || 'free';
  const tierInfo = SUBSCRIPTION_TIERS[currentTier as keyof typeof SUBSCRIPTION_TIERS] || SUBSCRIPTION_TIERS.free;
  const TierIcon = tierInfo.icon;

  return (
    <div className="min-h-screen bg-background">
      <header className="sticky top-0 z-50 border-b border-border/50 bg-background/80 backdrop-blur-lg">
        <div className="container flex h-16 items-center gap-4">
          <Button variant="ghost" size="icon" asChild>
            <Link href="/"><ArrowLeft className="h-5 w-5" /></Link>
          </Button>
          <div className="flex items-center gap-2">
            <SettingsIcon className="h-5 w-5 text-primary" />
            <span className="font-semibold">Settings</span>
          </div>
        </div>
      </header>

      <main className="container py-8 max-w-2xl">
        {/* Profile */}
        <Card className="mb-6">
          <CardHeader>
            <CardTitle>Profile</CardTitle>
            <CardDescription>Your account information</CardDescription>
          </CardHeader>
          <CardContent className="space-y-4">
            <div className="space-y-2">
              <Label>Name</Label>
              <Input value={user?.name || ''} disabled />
            </div>
            <div className="space-y-2">
              <Label>Email</Label>
              <Input value={user?.email || ''} disabled />
            </div>
          </CardContent>
        </Card>

        {/* Subscription */}
        <Card className="mb-6">
          <CardHeader>
            <CardTitle>Subscription</CardTitle>
            <CardDescription>Your current plan and available features</CardDescription>
          </CardHeader>
          <CardContent>
            <div className="flex items-center gap-3 mb-6">
              <div className={`h-12 w-12 rounded-full bg-muted flex items-center justify-center ${tierInfo.color}`}>
                <TierIcon className="h-6 w-6" />
              </div>
              <div>
                <h3 className="font-semibold text-lg">{tierInfo.name}</h3>
                <p className="text-sm text-muted-foreground">Current plan</p>
              </div>
            </div>

            <div className="space-y-4">
              <div>
                <h4 className="text-sm font-medium mb-2">Available Models</h4>
                <div className="flex flex-wrap gap-2">
                  {tierInfo.models.map((model) => (
                    <Badge key={model} variant="secondary">{model}</Badge>
                  ))}
                </div>
              </div>

              <div>
                <h4 className="text-sm font-medium mb-2">Features</h4>
                <ul className="text-sm text-muted-foreground space-y-1">
                  {tierInfo.features.map((feature) => (
                    <li key={feature}>• {feature}</li>
                  ))}
                </ul>
              </div>
            </div>

            {currentTier === 'free' && (
              <div className="mt-6 p-4 bg-primary/5 rounded-lg border border-primary/20">
                <p className="text-sm mb-3">Upgrade to unlock more models and features!</p>
                <Button disabled>
                  Upgrade (Coming Soon)
                </Button>
              </div>
            )}
          </CardContent>
        </Card>

        {/* All Tiers */}
        <Card>
          <CardHeader>
            <CardTitle>All Plans</CardTitle>
            <CardDescription>Compare subscription tiers</CardDescription>
          </CardHeader>
          <CardContent>
            <div className="grid gap-4">
              {Object.entries(SUBSCRIPTION_TIERS).map(([key, tier]) => {
                const Icon = tier.icon;
                const isCurrent = key === currentTier;
                return (
                  <div 
                    key={key} 
                    className={`p-4 rounded-lg border ${isCurrent ? 'border-primary bg-primary/5' : 'border-border'}`}
                  >
                    <div className="flex items-center gap-3 mb-2">
                      <Icon className={`h-5 w-5 ${tier.color}`} />
                      <span className="font-semibold">{tier.name}</span>
                      {isCurrent && <Badge variant="outline">Current</Badge>}
                    </div>
                    <div className="flex flex-wrap gap-1 mb-2">
                      {tier.models.map((model) => (
                        <Badge key={model} variant="secondary" className="text-xs">{model}</Badge>
                      ))}
                    </div>
                    <ul className="text-xs text-muted-foreground">
                      {tier.features.slice(0, 2).map((f) => (
                        <li key={f}>• {f}</li>
                      ))}
                    </ul>
                  </div>
                );
              })}
            </div>
          </CardContent>
        </Card>
      </main>
    </div>
  );
}
