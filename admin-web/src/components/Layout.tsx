import { type ReactNode, useState, useEffect, useRef } from 'react';
import { Link, useLocation } from 'react-router-dom';
import { useAuth } from '@/hooks/useAuth';
import { Button } from '@/components/ui/button';
import { cn } from '@/lib/utils';

interface LayoutProps {
  children: ReactNode;
}

export default function Layout({ children }: LayoutProps) {
  const location = useLocation();
  const { logout, userInfo } = useAuth();
  const [isDropdownOpen, setIsDropdownOpen] = useState(false);
  const dropdownRef = useRef<HTMLDivElement>(null);

  const handleLogout = () => {
    logout();
  };

  const isActive = (path: string) => {
    return location.pathname === path;
  };

  const toggleDropdown = () => {
    setIsDropdownOpen(!isDropdownOpen);
  };

  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target as Node)) {
        setIsDropdownOpen(false);
      }
    };

    document.addEventListener('mousedown', handleClickOutside);
    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, []);

  return (
    <div className="flex min-h-screen">
      {/* Sidebar */}
      <div className="hidden border-r bg-background md:block md:w-[220px] lg:w-[280px]">
        <div className="flex h-full max-h-screen flex-col gap-2">
          <div className="flex h-14 items-center border-b px-4 lg:h-[60px] lg:px-6">
            <Link to="/" className="flex items-center gap-2 font-semibold">
              <i className="bx bx-coin h-6 w-6"></i>
              <span>Pickup Coins</span>
            </Link>
          </div>
          <div className="flex-1 overflow-auto py-2">
            <nav className="grid items-start px-2 text-sm font-medium lg:px-4">
              <Link
                to="/"
                className={cn(
                  "flex items-center gap-3 rounded-lg px-3 py-2 transition-all hover:text-primary",
                  isActive('/') ? "bg-muted text-primary" : "text-muted-foreground"
                )}
              >
                <i className="bx bx-home-circle h-4 w-4"></i>
                Dashboard
              </Link>
              <Link
                to="/cookies"
                className={cn(
                  "flex items-center gap-3 rounded-lg px-3 py-2 transition-all hover:text-primary",
                  isActive('/cookies') ? "bg-muted text-primary" : "text-muted-foreground"
                )}
              >
                <i className="bx bx-cookie h-4 w-4"></i>
                Cookies
              </Link>
              <Link
                to="/sites"
                className={cn(
                  "flex items-center gap-3 rounded-lg px-3 py-2 transition-all hover:text-primary",
                  isActive('/sites') ? "bg-muted text-primary" : "text-muted-foreground"
                )}
              >
                <i className="bx bx-world h-4 w-4"></i>
                Sites
              </Link>
              <Link
                to="/point-urls"
                className={cn(
                  "flex items-center gap-3 rounded-lg px-3 py-2 transition-all hover:text-primary",
                  isActive('/point-urls') ? "bg-muted text-primary" : "text-muted-foreground"
                )}
              >
                <i className="bx bx-link h-4 w-4"></i>
                Point URLs
              </Link>
              <Link
                to="/point-logs"
                className={cn(
                  "flex items-center gap-3 rounded-lg px-3 py-2 transition-all hover:text-primary",
                  isActive('/point-logs') ? "bg-muted text-primary" : "text-muted-foreground"
                )}
              >
                <i className="bx bx-history h-4 w-4"></i>
                Logs
              </Link>
            </nav>
          </div>
        </div>
      </div>
      <div className="flex flex-col flex-1">
        <header className="flex h-14 items-center justify-between border-b bg-background px-6 lg:h-[60px]">
          <div></div>
          <div className="flex items-center gap-4">
            <a
              href="https://github.com/hajubal/pickupcoins"
              target="_blank"
              rel="noopener noreferrer"
              className="text-sm font-medium text-muted-foreground hover:text-primary transition-colors"
            >
              GitHub
            </a>
            <div className="relative" ref={dropdownRef}>
              <Button
                variant="ghost"
                size="icon"
                className="rounded-full"
                onClick={toggleDropdown}
              >
                <i className="bx bx-user h-5 w-5"></i>
                <span className="sr-only">Toggle user menu</span>
              </Button>
              {isDropdownOpen && (
                <div className="absolute right-0 mt-2 w-56 rounded-md border bg-popover p-1 shadow-lg z-50">
                  <div className="px-2 py-1.5">
                    <p className="text-sm font-medium">{userInfo.userName || 'admin'}</p>
                    <p className="text-xs text-muted-foreground">{userInfo.loginId || 'admin'}</p>
                  </div>
                  <div className="h-px bg-border my-1"></div>
                  <button
                    onClick={handleLogout}
                    className="relative flex w-full cursor-pointer select-none items-center rounded-sm px-2 py-1.5 text-sm outline-none transition-colors hover:bg-accent focus:bg-accent"
                  >
                    <i className="bx bx-power-off mr-2 h-4 w-4"></i>
                    Log out
                  </button>
                </div>
              )}
            </div>
          </div>
        </header>
        <main className="flex-1 overflow-y-auto p-8">
          <div className="mx-auto max-w-7xl space-y-6">
            {children}
          </div>
        </main>
      </div>
    </div>
  );
}
