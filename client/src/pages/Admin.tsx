import { useAuth } from "@/_core/hooks/useAuth";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { trpc } from "@/lib/trpc";
import { getLoginUrl } from "@/const";
import { Link, useLocation } from "wouter";
import { 
  ArrowLeft, 
  Shield, 
  Loader2, 
  Users, 
  Flag, 
  Sparkles, 
  Check, 
  X,
  Eye,
  Ban,
  AlertTriangle
} from "lucide-react";
import { useEffect } from "react";
import { toast } from "sonner";
import { formatDistanceToNow } from "date-fns";

export default function Admin() {
  const { user, isAuthenticated, loading } = useAuth();
  const [, navigate] = useLocation();

  const { data: reports, isLoading: reportsLoading, refetch: refetchReports } = trpc.admin.reports.useQuery(
    { status: 'pending' },
    { enabled: isAuthenticated && user?.role === 'admin' }
  );

  const { data: users, isLoading: usersLoading } = trpc.admin.users.useQuery(
    { limit: 50 },
    { enabled: isAuthenticated && user?.role === 'admin' }
  );

  const { data: stats } = trpc.admin.stats.useQuery(
    undefined,
    { enabled: isAuthenticated && user?.role === 'admin' }
  );

  const updateReportMutation = trpc.admin.updateReport.useMutation({
    onSuccess: () => {
      toast.success('Report updated');
      refetchReports();
    },
  });

  const deleteCharacterMutation = trpc.admin.deleteCharacter.useMutation({
    onSuccess: () => {
      toast.success('Character deleted');
      refetchReports();
    },
  });

  useEffect(() => {
    if (!loading && !isAuthenticated) {
      window.location.href = getLoginUrl();
    }
    if (!loading && isAuthenticated && user?.role !== 'admin') {
      navigate('/');
    }
  }, [loading, isAuthenticated, user]);

  if (loading || reportsLoading) {
    return (
      <div className="min-h-screen bg-background flex items-center justify-center">
        <Loader2 className="h-8 w-8 animate-spin text-primary" />
      </div>
    );
  }

  if (user?.role !== 'admin') {
    return (
      <div className="min-h-screen bg-background flex items-center justify-center">
        <div className="text-center">
          <Shield className="h-12 w-12 text-muted-foreground/50 mx-auto mb-4" />
          <h2 className="text-xl font-semibold">Access Denied</h2>
          <p className="text-muted-foreground mt-1">You don't have permission to access this page.</p>
          <Button className="mt-4" asChild><Link href="/">Go Home</Link></Button>
        </div>
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
            <Shield className="h-5 w-5 text-primary" />
            <span className="font-semibold">Admin Panel</span>
          </div>
        </div>
      </header>

      <main className="container py-8">
        {/* Stats */}
        <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-8">
          <Card>
            <CardContent className="p-4">
              <div className="flex items-center gap-3">
                <Users className="h-8 w-8 text-primary" />
                <div>
                  <p className="text-2xl font-bold">{stats?.users || 0}</p>
                  <p className="text-sm text-muted-foreground">Users</p>
                </div>
              </div>
            </CardContent>
          </Card>
          <Card>
            <CardContent className="p-4">
              <div className="flex items-center gap-3">
                <Sparkles className="h-8 w-8 text-primary" />
                <div>
                  <p className="text-2xl font-bold">{stats?.characters || 0}</p>
                  <p className="text-sm text-muted-foreground">Characters</p>
                </div>
              </div>
            </CardContent>
          </Card>
          <Card>
            <CardContent className="p-4">
              <div className="flex items-center gap-3">
                <Flag className="h-8 w-8 text-destructive" />
                <div>
                  <p className="text-2xl font-bold">{(stats as any)?.pendingReports || 0}</p>
                  <p className="text-sm text-muted-foreground">Pending Reports</p>
                </div>
              </div>
            </CardContent>
          </Card>
          <Card>
            <CardContent className="p-4">
              <div className="flex items-center gap-3">
                <AlertTriangle className="h-8 w-8 text-yellow-500" />
                <div>
                  <p className="text-2xl font-bold">{(stats as any)?.bannedCharacters || 0}</p>
                  <p className="text-sm text-muted-foreground">Banned</p>
                </div>
              </div>
            </CardContent>
          </Card>
        </div>

        <Tabs defaultValue="reports">
          <TabsList className="mb-6">
            <TabsTrigger value="reports">
              <Flag className="h-4 w-4 mr-2" />
              Reports
              {reports && reports.length > 0 && (
                <Badge variant="destructive" className="ml-2">{reports.length}</Badge>
              )}
            </TabsTrigger>
            <TabsTrigger value="users">
              <Users className="h-4 w-4 mr-2" />
              Users
            </TabsTrigger>
          </TabsList>

          <TabsContent value="reports">
            <Card>
              <CardHeader>
                <CardTitle>Content Reports</CardTitle>
                <CardDescription>Review and moderate reported content</CardDescription>
              </CardHeader>
              <CardContent>
                {reports && reports.length > 0 ? (
                  <div className="space-y-4">
                    {reports.map((report) => (
                      <div key={report.id} className="p-4 border rounded-lg">
                        <div className="flex items-start justify-between">
                          <div className="flex-1">
                            <div className="flex items-center gap-2 mb-2">
                              <Badge variant={report.status === 'pending' ? 'destructive' : 'secondary'}>
                                {report.status}
                              </Badge>
                              <span className="text-sm text-muted-foreground">
                                {formatDistanceToNow(new Date(report.createdAt), { addSuffix: true })}
                              </span>
                            </div>
                            <p className="font-medium">Character ID: {report.characterId}</p>
                            <p className="text-sm text-muted-foreground mt-1">{report.reason}</p>
                          </div>
                          <div className="flex gap-2">
                            <Button 
                              variant="outline" 
                              size="sm"
                              onClick={() => navigate(`/character/${report.characterId}`)}
                            >
                              <Eye className="h-4 w-4 mr-1" />
                              View
                            </Button>
                            <Button 
                              variant="outline" 
                              size="sm"
                              onClick={() => updateReportMutation.mutate({ 
                                id: report.id, 
                                status: 'dismissed',
                                reviewNotes: 'No action needed'
                              })}
                            >
                              <Check className="h-4 w-4 mr-1" />
                              Dismiss
                            </Button>
                            <Button 
                              variant="destructive" 
                              size="sm"
                              onClick={() => {
                                if (confirm('Delete this character?')) {
                                  deleteCharacterMutation.mutate({ id: report.characterId! });
                                  updateReportMutation.mutate({
                                    id: report.id,
                                    status: 'resolved',
                                    reviewNotes: 'Character deleted'
                                  });
                                }
                              }}
                            >
                              <Ban className="h-4 w-4 mr-1" />
                              Ban
                            </Button>
                          </div>
                        </div>
                      </div>
                    ))}
                  </div>
                ) : (
                  <div className="text-center py-12">
                    <Check className="h-12 w-12 text-green-500 mx-auto mb-4" />
                    <h3 className="font-semibold">All clear!</h3>
                    <p className="text-muted-foreground">No pending reports to review.</p>
                  </div>
                )}
              </CardContent>
            </Card>
          </TabsContent>

          <TabsContent value="users">
            <Card>
              <CardHeader>
                <CardTitle>User Management</CardTitle>
                <CardDescription>View and manage platform users</CardDescription>
              </CardHeader>
              <CardContent>
                {usersLoading ? (
                  <div className="flex justify-center py-8">
                    <Loader2 className="h-8 w-8 animate-spin" />
                  </div>
                ) : users && users.length > 0 ? (
                  <div className="space-y-2">
                    {users.map((u) => (
                      <div key={u.id} className="flex items-center justify-between p-3 border rounded-lg">
                        <div>
                          <p className="font-medium">{u.name || 'Unnamed User'}</p>
                          <p className="text-sm text-muted-foreground">{u.email}</p>
                        </div>
                        <div className="flex items-center gap-2">
                          <Badge variant={u.role === 'admin' ? 'default' : 'secondary'}>
                            {u.role}
                          </Badge>
                          <Badge variant="outline">
                            {(u as any).subscriptionTier || 'free'}
                          </Badge>
                        </div>
                      </div>
                    ))}
                  </div>
                ) : (
                  <p className="text-center text-muted-foreground py-8">No users found.</p>
                )}
              </CardContent>
            </Card>
          </TabsContent>
        </Tabs>
      </main>
    </div>
  );
}
