import { User } from 'lucide-react';

export function Header() {
  const userName = localStorage.getItem('userName') || 'Admin';

  return (
    <header className="flex h-16 items-center justify-between border-b bg-card px-6">
      <div />
      <div className="flex items-center gap-2 text-sm text-muted-foreground">
        <User className="h-4 w-4" />
        <span>{userName}</span>
      </div>
    </header>
  );
}
