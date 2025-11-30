import { type ReactNode } from 'react';
import { Link, useLocation } from 'react-router-dom';
import { useAuth } from '@/hooks/useAuth';

interface LayoutProps {
  children: ReactNode;
}

export default function Layout({ children }: LayoutProps) {
  const location = useLocation();
  const { logout, userInfo } = useAuth();

  const handleLogout = () => {
    logout();
  };

  const isActive = (path: string) => {
    return location.pathname === path;
  };

  return (
    <div className="flex min-h-screen">
      {/* Sidebar */}
      <aside className="w-64 bg-card border-r flex flex-col">
        <div className="p-6 border-b border-border">
          <Link to="/" className="flex items-center space-x-3">
            <div className="flex h-8 w-8 items-center justify-center rounded-lg bg-primary text-primary-foreground">
              <i className="bx bx-coin text-sm"></i>
            </div>
            <span className="text-xl font-semibold">Pickup Coins</span>
          </Link>
        </div>

        <nav className="flex-1 px-3 py-4 space-y-1">
          <Link
            to="/"
            className={`flex items-center gap-3 rounded-lg px-3 py-2 transition-all ${
              isActive('/') ? 'bg-accent text-primary' : 'text-muted-foreground hover:text-primary'
            }`}
          >
            <i className="bx bx-home-circle h-4 w-4"></i>
            Dashboard
          </Link>

          <Link
            to="/cookies"
            className={`flex items-center gap-3 rounded-lg px-3 py-2 transition-all ${
              isActive('/cookies') ? 'bg-accent text-primary' : 'text-muted-foreground hover:text-primary'
            }`}
          >
            <i className="bx bx-cookie h-4 w-4"></i>
            Cookies
          </Link>

          <Link
            to="/sites"
            className={`flex items-center gap-3 rounded-lg px-3 py-2 transition-all ${
              isActive('/sites') ? 'bg-accent text-primary' : 'text-muted-foreground hover:text-primary'
            }`}
          >
            <i className="bx bx-world h-4 w-4"></i>
            Sites
          </Link>

          <Link
            to="/point-urls"
            className={`flex items-center gap-3 rounded-lg px-3 py-2 transition-all ${
              isActive('/point-urls') ? 'bg-accent text-primary' : 'text-muted-foreground hover:text-primary'
            }`}
          >
            <i className="bx bx-link h-4 w-4"></i>
            Point URLs
          </Link>

          <Link
            to="/point-logs"
            className={`flex items-center gap-3 rounded-lg px-3 py-2 transition-all ${
              isActive('/point-logs') ? 'bg-accent text-primary' : 'text-muted-foreground hover:text-primary'
            }`}
          >
            <i className="bx bx-history h-4 w-4"></i>
            Logs
          </Link>
        </nav>
      </aside>

      {/* Main Content */}
      <div className="flex-1 flex flex-col">
        {/* Header */}
        <header className="bg-background border-b border-border">
          <div className="flex items-center justify-between px-6 py-3">
            <div className="flex items-center">
              <button className="md:hidden">
                <i className="bx bx-menu h-4 w-4"></i>
              </button>
            </div>

            <div className="flex items-center space-x-4">
              <div className="relative group">
                <button className="flex items-center space-x-3 rounded-lg px-3 py-2 hover:bg-accent transition-colors">
                  <div className="flex h-8 w-8 items-center justify-center rounded-full bg-muted">
                    <i className="bx bx-user h-4 w-4"></i>
                  </div>
                  <div className="hidden md:block text-left">
                    <div className="text-sm font-medium">{userInfo.userName || 'Admin'}</div>
                    <div className="text-xs text-muted-foreground">{userInfo.loginId || 'Administrator'}</div>
                  </div>
                  <i className="bx bx-chevron-down h-4 w-4 text-muted-foreground"></i>
                </button>

                <div className="hidden group-hover:block absolute right-0 mt-2 w-48 bg-popover rounded-md border shadow-md py-1 z-50">
                  <button
                    onClick={handleLogout}
                    className="w-full flex items-center px-3 py-2 text-sm hover:bg-accent rounded-sm mx-1 text-destructive"
                  >
                    <i className="bx bx-power-off mr-2 h-4 w-4"></i>
                    Log Out
                  </button>
                </div>
              </div>
            </div>
          </div>
        </header>

        {/* Content */}
        <main className="flex-1 p-6 overflow-y-auto bg-muted/30">{children}</main>

        {/* Footer */}
        <footer className="bg-card border-t border-border px-6 py-3">
          <div className="flex justify-between items-center text-sm text-muted-foreground">
            <div></div>
            <div>
              <a
                href="https://github.com/hajubal/pickupcoins"
                target="_blank"
                rel="noopener noreferrer"
                className="hover:text-primary transition-colors"
              >
                v2.0.0
              </a>
            </div>
          </div>
        </footer>
      </div>
    </div>
  );
}
