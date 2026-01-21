import { useAuth } from "@/_core/hooks/useAuth";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from "@/components/ui/card";
import { Label } from "@/components/ui/label";
import { Textarea } from "@/components/ui/textarea";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { trpc } from "@/lib/trpc";
import { getLoginUrl } from "@/const";
import { Link, useLocation } from "wouter";
import { ArrowLeft, Upload, Loader2, FileJson, Check } from "lucide-react";
import { useEffect, useState, useRef } from "react";
import { toast } from "sonner";

export default function CharacterImport() {
  const { isAuthenticated, loading } = useAuth();
  const [, navigate] = useLocation();
  const [format, setFormat] = useState<'tavernai' | 'sillytavern' | 'chubai'>('sillytavern');
  const [jsonData, setJsonData] = useState('');
  const [preview, setPreview] = useState<any>(null);
  const fileInputRef = useRef<HTMLInputElement>(null);

  const importMutation = trpc.character.import.useMutation({
    onSuccess: (data) => {
      toast.success('Character imported successfully!');
      navigate(`/character/${data.id}`);
    },
    onError: (error) => {
      toast.error(error.message || 'Failed to import character');
    },
  });

  useEffect(() => {
    if (!loading && !isAuthenticated) {
      window.location.href = getLoginUrl();
    }
  }, [loading, isAuthenticated]);

  useEffect(() => {
    if (jsonData) {
      try {
        const parsed = JSON.parse(jsonData);
        setPreview({
          name: parsed.name || parsed.char_name || 'Unnamed',
          description: parsed.description || parsed.char_persona || '',
          personality: parsed.personality || '',
          scenario: parsed.scenario || parsed.world_scenario || '',
          firstMessage: parsed.first_mes || parsed.char_greeting || '',
        });
      } catch {
        setPreview(null);
      }
    } else {
      setPreview(null);
    }
  }, [jsonData]);

  const handleFileUpload = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;

    const reader = new FileReader();
    reader.onload = (event) => {
      const content = event.target?.result as string;
      setJsonData(content);
    };
    reader.readAsText(file);
  };

  const handleImport = () => {
    if (!jsonData.trim()) {
      toast.error('Please provide character data');
      return;
    }
    importMutation.mutate({ format, data: jsonData });
  };

  if (loading) {
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
            <Link href="/my-characters"><ArrowLeft className="h-5 w-5" /></Link>
          </Button>
          <div className="flex items-center gap-2">
            <Upload className="h-5 w-5 text-primary" />
            <span className="font-semibold">Import Character</span>
          </div>
        </div>
      </header>

      <main className="container py-8 max-w-2xl">
        <Card className="mb-6">
          <CardHeader>
            <CardTitle>Import Character Card</CardTitle>
            <CardDescription>
              Import a character from TavernAI, SillyTavern, or Chub.AI format
            </CardDescription>
          </CardHeader>
          <CardContent className="space-y-6">
            <div className="space-y-2">
              <Label>Format</Label>
              <Select value={format} onValueChange={(v: any) => setFormat(v)}>
                <SelectTrigger>
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="sillytavern">SillyTavern</SelectItem>
                  <SelectItem value="tavernai">TavernAI</SelectItem>
                  <SelectItem value="chubai">Chub.AI</SelectItem>
                </SelectContent>
              </Select>
            </div>

            <div className="space-y-2">
              <Label>Upload JSON File</Label>
              <input
                ref={fileInputRef}
                type="file"
                accept=".json"
                onChange={handleFileUpload}
                className="hidden"
              />
              <Button
                variant="outline"
                className="w-full h-24 border-dashed"
                onClick={() => fileInputRef.current?.click()}
              >
                <div className="flex flex-col items-center gap-2">
                  <FileJson className="h-8 w-8 text-muted-foreground" />
                  <span className="text-muted-foreground">Click to upload JSON file</span>
                </div>
              </Button>
            </div>

            <div className="relative">
              <div className="absolute inset-0 flex items-center">
                <span className="w-full border-t" />
              </div>
              <div className="relative flex justify-center text-xs uppercase">
                <span className="bg-background px-2 text-muted-foreground">Or paste JSON</span>
              </div>
            </div>

            <div className="space-y-2">
              <Label>Character JSON</Label>
              <Textarea
                value={jsonData}
                onChange={(e) => setJsonData(e.target.value)}
                placeholder='{"name": "Character Name", "description": "...", ...}'
                rows={8}
                className="font-mono text-sm"
              />
            </div>

            {preview && (
              <Card className="bg-muted/50">
                <CardContent className="p-4">
                  <div className="flex items-center gap-2 mb-3">
                    <Check className="h-4 w-4 text-green-500" />
                    <span className="font-medium">Preview</span>
                  </div>
                  <div className="space-y-2 text-sm">
                    <p><strong>Name:</strong> {preview.name}</p>
                    {preview.description && (
                      <p><strong>Description:</strong> {preview.description.slice(0, 100)}...</p>
                    )}
                    {preview.personality && (
                      <p><strong>Personality:</strong> {preview.personality.slice(0, 100)}...</p>
                    )}
                    {preview.firstMessage && (
                      <p><strong>First Message:</strong> {preview.firstMessage.slice(0, 100)}...</p>
                    )}
                  </div>
                </CardContent>
              </Card>
            )}

            <Button
              className="w-full"
              onClick={handleImport}
              disabled={!jsonData.trim() || importMutation.isPending}
            >
              {importMutation.isPending ? (
                <Loader2 className="h-4 w-4 animate-spin mr-2" />
              ) : (
                <Upload className="h-4 w-4 mr-2" />
              )}
              Import Character
            </Button>
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle className="text-base">Supported Formats</CardTitle>
          </CardHeader>
          <CardContent className="text-sm text-muted-foreground space-y-2">
            <p><strong>SillyTavern:</strong> Standard character card format with name, description, personality, scenario, first_mes, mes_example</p>
            <p><strong>TavernAI:</strong> Original TavernAI format with char_name, char_persona, world_scenario, char_greeting</p>
            <p><strong>Chub.AI:</strong> Chub.AI export format with extended metadata</p>
          </CardContent>
        </Card>
      </main>
    </div>
  );
}
