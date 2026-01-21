import { Toaster } from "@/components/ui/sonner";
import { TooltipProvider } from "@/components/ui/tooltip";
import NotFound from "@/pages/NotFound";
import { Route, Switch } from "wouter";
import ErrorBoundary from "./components/ErrorBoundary";
import { ThemeProvider } from "./contexts/ThemeContext";
import Home from "./pages/Home";
import Discover from "./pages/Discover";
import CharacterView from "./pages/CharacterView";
import CharacterCreate from "./pages/CharacterCreate";
import CharacterEdit from "./pages/CharacterEdit";
import Chat from "./pages/Chat";
import ChatList from "./pages/ChatList";
import MyCharacters from "./pages/MyCharacters";
import Lorebooks from "./pages/Lorebooks";
import LorebookEdit from "./pages/LorebookEdit";
import Personas from "./pages/Personas";
import Settings from "./pages/Settings";
import Admin from "./pages/Admin";
import CharacterImport from "./pages/CharacterImport";
import { useAuth } from "@/_core/hooks/useAuth";

function Router() {
  const { user, loading } = useAuth();
  
  return (
    <Switch>
      {/* Public routes */}
      <Route path="/" component={Home} />
      <Route path="/discover" component={Discover} />
      <Route path="/character/:id" component={CharacterView} />
      
      {/* Protected routes */}
      <Route path="/create" component={CharacterCreate} />
      <Route path="/character/:id/edit" component={CharacterEdit} />
      <Route path="/chat/:id" component={Chat} />
      <Route path="/chats" component={ChatList} />
      <Route path="/my-characters" component={MyCharacters} />
      <Route path="/import" component={CharacterImport} />
      <Route path="/lorebooks" component={Lorebooks} />
      <Route path="/lorebook/:id" component={LorebookEdit} />
      <Route path="/personas" component={Personas} />
      <Route path="/settings" component={Settings} />
      
      {/* Admin routes */}
      <Route path="/admin" component={Admin} />
      
      <Route path="/404" component={NotFound} />
      <Route component={NotFound} />
    </Switch>
  );
}

function App() {
  return (
    <ErrorBoundary>
      <ThemeProvider defaultTheme="dark">
        <TooltipProvider>
          <Toaster />
          <Router />
        </TooltipProvider>
      </ThemeProvider>
    </ErrorBoundary>
  );
}

export default App;
