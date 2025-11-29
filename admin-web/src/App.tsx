import { QueryClientProvider } from '@tanstack/react-query';
import { BrowserRouter } from 'react-router-dom';
import { queryClient } from './lib/queryClient';

function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <BrowserRouter>
        <div className="min-h-screen bg-background">
          <h1 className="text-3xl font-bold p-8">Admin Dashboard</h1>
          <p className="px-8 text-muted-foreground">
            Admin API Server - Frontend is ready! 🚀
          </p>
        </div>
      </BrowserRouter>
    </QueryClientProvider>
  );
}

export default App;
