import { useAuth } from "@/_core/hooks/useAuth";
import { Button } from "@/components/ui/button";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { Input } from "@/components/ui/input";
import { trpc } from "@/lib/trpc";
import { getLoginUrl } from "@/const";
import { Link, useParams, useLocation } from "wouter";
import { 
  ArrowLeft, 
  Send, 
  Loader2, 
  Sparkles,
  Settings,
  RotateCcw,
  Mic,
  MicOff,
  MoreVertical,
  Trash2
} from "lucide-react";
import { useState, useEffect, useRef } from "react";
import { toast } from "sonner";
import { Streamdown } from "streamdown";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";

interface Message {
  id: number;
  turn: number;
  role: 'user' | 'assistant' | 'system';
  content: string;
  createdAt?: Date;
}

export default function Chat() {
  const params = useParams<{ id: string }>();
  const chatId = parseInt(params.id || '0');
  const { user, isAuthenticated, loading: authLoading } = useAuth();
  const [, navigate] = useLocation();
  
  const [inputValue, setInputValue] = useState('');
  const [isRecording, setIsRecording] = useState(false);
  const [messages, setMessages] = useState<Message[]>([]);
  const messagesEndRef = useRef<HTMLDivElement>(null);
  const inputRef = useRef<HTMLInputElement>(null);

  const { data: chatData, isLoading, refetch } = trpc.chat.get.useQuery(
    { id: chatId },
    { enabled: chatId > 0 && isAuthenticated }
  );

  const sendMessageMutation = trpc.chat.sendMessage.useMutation({
    onSuccess: (data) => {
      setMessages(prev => [...prev, data.assistantMessage]);
    },
    onError: () => {
      toast.error('Failed to send message');
    },
  });

  const deleteChatMutation = trpc.chat.delete.useMutation({
    onSuccess: () => {
      toast.success('Chat deleted');
      navigate('/chats');
    },
  });

  const transcribeMutation = trpc.chat.transcribeVoice.useMutation({
    onSuccess: (data) => {
      setInputValue(data.text);
      setIsRecording(false);
    },
    onError: () => {
      toast.error('Failed to transcribe audio');
      setIsRecording(false);
    },
  });

  useEffect(() => {
    if (chatData?.messages) {
      setMessages(chatData.messages);
    }
  }, [chatData]);

  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages]);

  useEffect(() => {
    if (!authLoading && !isAuthenticated) {
      window.location.href = getLoginUrl();
    }
  }, [authLoading, isAuthenticated]);

  const handleSendMessage = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!inputValue.trim() || sendMessageMutation.isPending) return;

    const userMessage: Message = {
      id: Date.now(),
      turn: messages.length + 1,
      role: 'user',
      content: inputValue.trim(),
    };

    setMessages(prev => [...prev, userMessage]);
    setInputValue('');

    sendMessageMutation.mutate({
      chatSessionId: chatId,
      content: userMessage.content,
    });
  };

  const handleVoiceInput = async () => {
    if (isRecording) {
      setIsRecording(false);
      return;
    }

    try {
      const stream = await navigator.mediaDevices.getUserMedia({ audio: true });
      const mediaRecorder = new MediaRecorder(stream);
      const chunks: Blob[] = [];

      mediaRecorder.ondataavailable = (e) => chunks.push(e.data);
      mediaRecorder.onstop = async () => {
        const blob = new Blob(chunks, { type: 'audio/webm' });
        // In production, upload to S3 and get URL
        // For now, show a placeholder
        toast.info('Voice input feature requires audio upload setup');
        stream.getTracks().forEach(track => track.stop());
      };

      setIsRecording(true);
      mediaRecorder.start();

      setTimeout(() => {
        if (mediaRecorder.state === 'recording') {
          mediaRecorder.stop();
        }
      }, 10000); // Max 10 seconds
    } catch {
      toast.error('Could not access microphone');
    }
  };

  if (isLoading || authLoading) {
    return (
      <div className="min-h-screen bg-background flex items-center justify-center">
        <Loader2 className="h-8 w-8 animate-spin text-primary" />
      </div>
    );
  }

  if (!chatData) {
    return (
      <div className="min-h-screen bg-background flex items-center justify-center">
        <div className="text-center">
          <Sparkles className="h-12 w-12 text-muted-foreground/50 mx-auto mb-4" />
          <h2 className="text-xl font-semibold">Chat not found</h2>
          <Button className="mt-4" asChild>
            <Link href="/chats">View All Chats</Link>
          </Button>
        </div>
      </div>
    );
  }

  const character = chatData.character;

  return (
    <div className="h-screen flex flex-col bg-background">
      {/* Header */}
      <header className="flex-shrink-0 border-b border-border/50 bg-background/80 backdrop-blur-lg">
        <div className="container flex h-16 items-center justify-between">
          <div className="flex items-center gap-4">
            <Button variant="ghost" size="icon" asChild>
              <Link href="/chats">
                <ArrowLeft className="h-5 w-5" />
              </Link>
            </Button>
            <div className="flex items-center gap-3">
              <Avatar className="h-10 w-10">
                {character?.avatarUrl ? (
                  <AvatarImage src={character.avatarUrl} alt={character.name} />
                ) : null}
                <AvatarFallback>
                  <Sparkles className="h-5 w-5" />
                </AvatarFallback>
              </Avatar>
              <div>
                <h1 className="font-semibold">{character?.name || 'Chat'}</h1>
                <p className="text-xs text-muted-foreground">
                  {chatData.title}
                </p>
              </div>
            </div>
          </div>
          
          <div className="flex items-center gap-2">
            <Button variant="ghost" size="icon" asChild>
              <Link href={`/character/${character?.id}`}>
                <Settings className="h-5 w-5" />
              </Link>
            </Button>
            <DropdownMenu>
              <DropdownMenuTrigger asChild>
                <Button variant="ghost" size="icon">
                  <MoreVertical className="h-5 w-5" />
                </Button>
              </DropdownMenuTrigger>
              <DropdownMenuContent align="end">
                <DropdownMenuItem 
                  className="text-destructive"
                  onClick={() => {
                    if (confirm('Delete this chat?')) {
                      deleteChatMutation.mutate({ id: chatId });
                    }
                  }}
                >
                  <Trash2 className="h-4 w-4 mr-2" />
                  Delete Chat
                </DropdownMenuItem>
              </DropdownMenuContent>
            </DropdownMenu>
          </div>
        </div>
      </header>

      {/* Messages */}
      <div className="flex-1 overflow-y-auto chat-scrollbar">
        <div className="container py-4 space-y-4 max-w-3xl">
          {messages.length === 0 && (
            <div className="text-center py-12">
              <Avatar className="h-20 w-20 mx-auto mb-4">
                {character?.avatarUrl ? (
                  <AvatarImage src={character.avatarUrl} alt={character.name} />
                ) : null}
                <AvatarFallback>
                  <Sparkles className="h-10 w-10" />
                </AvatarFallback>
              </Avatar>
              <h2 className="text-xl font-semibold">{character?.name}</h2>
              <p className="text-muted-foreground mt-2 max-w-md mx-auto">
                {character?.description || 'Start a conversation!'}
              </p>
            </div>
          )}

          {messages.map((message, index) => (
            <div
              key={message.id || index}
              className={`flex gap-3 animate-message-in ${
                message.role === 'user' ? 'flex-row-reverse' : ''
              }`}
            >
              <Avatar className="h-8 w-8 flex-shrink-0">
                {message.role === 'assistant' ? (
                  <>
                    {character?.avatarUrl ? (
                      <AvatarImage src={character.avatarUrl} alt={character.name} />
                    ) : null}
                    <AvatarFallback>
                      <Sparkles className="h-4 w-4" />
                    </AvatarFallback>
                  </>
                ) : (
                  <AvatarFallback>{user?.name?.[0] || 'U'}</AvatarFallback>
                )}
              </Avatar>
              
              <div
                className={`max-w-[80%] rounded-2xl px-4 py-2 ${
                  message.role === 'user'
                    ? 'bg-primary text-primary-foreground'
                    : 'bg-muted'
                }`}
              >
                {message.role === 'assistant' ? (
                  <Streamdown>{message.content}</Streamdown>
                ) : (
                  <p className="whitespace-pre-wrap">{message.content}</p>
                )}
              </div>
            </div>
          ))}

          {sendMessageMutation.isPending && (
            <div className="flex gap-3">
              <Avatar className="h-8 w-8">
                {character?.avatarUrl ? (
                  <AvatarImage src={character.avatarUrl} alt={character.name} />
                ) : null}
                <AvatarFallback>
                  <Sparkles className="h-4 w-4" />
                </AvatarFallback>
              </Avatar>
              <div className="bg-muted rounded-2xl px-4 py-3">
                <div className="flex gap-1">
                  <div className="w-2 h-2 bg-muted-foreground/50 rounded-full typing-dot" />
                  <div className="w-2 h-2 bg-muted-foreground/50 rounded-full typing-dot" />
                  <div className="w-2 h-2 bg-muted-foreground/50 rounded-full typing-dot" />
                </div>
              </div>
            </div>
          )}

          <div ref={messagesEndRef} />
        </div>
      </div>

      {/* Input */}
      <div className="flex-shrink-0 border-t border-border/50 bg-background">
        <form onSubmit={handleSendMessage} className="container py-4 max-w-3xl">
          <div className="flex gap-2">
            <Button
              type="button"
              variant="outline"
              size="icon"
              onClick={handleVoiceInput}
              className={isRecording ? 'text-destructive' : ''}
            >
              {isRecording ? <MicOff className="h-5 w-5" /> : <Mic className="h-5 w-5" />}
            </Button>
            
            <Input
              ref={inputRef}
              placeholder="Type a message..."
              value={inputValue}
              onChange={(e) => setInputValue(e.target.value)}
              disabled={sendMessageMutation.isPending}
              className="flex-1"
            />
            
            <Button 
              type="submit" 
              disabled={!inputValue.trim() || sendMessageMutation.isPending}
            >
              {sendMessageMutation.isPending ? (
                <Loader2 className="h-5 w-5 animate-spin" />
              ) : (
                <Send className="h-5 w-5" />
              )}
            </Button>
          </div>
        </form>
      </div>
    </div>
  );
}
