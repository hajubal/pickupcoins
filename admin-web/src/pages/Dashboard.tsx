import { useQuery } from '@tanstack/react-query';
import axiosInstance from '@/lib/axios';

interface DashboardStats {
  savedDayPoint: number;
  savedDayPointRatioDayBefore: number;
  savedWeekPoint: number;
  savedWeekPointRatioWeekBefore: number;
  pointUrlDayCnt: number;
  pointUrlWeekCnt: number;
}

export default function Dashboard() {
  const { data: stats, isLoading, refetch } = useQuery<DashboardStats>({
    queryKey: ['dashboard-stats'],
    queryFn: async () => {
      const response = await axiosInstance.get('/dashboard/stats');
      return response.data;
    },
    refetchInterval: 30000, // 30초마다 자동 새로고침
    placeholderData: {
      savedDayPoint: 0,
      savedDayPointRatioDayBefore: 0,
      savedWeekPoint: 0,
      savedWeekPointRatioWeekBefore: 0,
      pointUrlDayCnt: 0,
      pointUrlWeekCnt: 0,
    },
  });

  const displayStats = stats || {
    savedDayPoint: 0,
    savedDayPointRatioDayBefore: 0,
    savedWeekPoint: 0,
    savedWeekPointRatioWeekBefore: 0,
    pointUrlDayCnt: 0,
    pointUrlWeekCnt: 0,
  };

  return (
    <div className="max-w-7xl mx-auto">
      {/* Header Section */}
      <div className="mb-8 space-y-4">
        <div className="flex items-center justify-between">
          <div className="space-y-1">
            <h1 className="text-3xl font-bold tracking-tight">Dashboard</h1>
            <p className="text-muted-foreground">Welcome back! Here's what's happening with your points.</p>
          </div>
          <div className="flex items-center space-x-2">
            <button
              onClick={() => refetch()}
              disabled={isLoading}
              className="px-3 py-2 border rounded-md text-sm hover:bg-accent disabled:opacity-50"
            >
              <i className={`bx bx-refresh h-4 w-4 mr-2 inline ${isLoading ? 'animate-spin' : ''}`}></i>
              Refresh
            </button>
            <div className={`px-2 py-1 rounded-md text-xs ${isLoading ? 'bg-yellow-50 text-yellow-600' : 'bg-green-50 text-green-600'}`}>
              <i className="bx bx-time h-3 w-3 mr-1 inline"></i>
              {isLoading ? 'Loading...' : 'Live Data'}
            </div>
          </div>
        </div>
      </div>

      {/* Stats Grid */}
      <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-4 mb-8">
        {/* Day Point Card */}
        <div className="bg-card p-6 rounded-lg border">
          <div className="flex items-center justify-between">
            <div className="space-y-2">
              <p className="text-sm font-medium text-muted-foreground">Today's Points</p>
              <div className="flex items-baseline space-x-2">
                <p className="text-2xl font-bold">{displayStats.savedDayPoint}</p>
                <div
                  className={`flex items-center text-xs ${
                    displayStats.savedDayPointRatioDayBefore >= 0 ? 'text-emerald-600' : 'text-destructive'
                  }`}
                >
                  <i
                    className={`bx h-3 w-3 mr-1 ${
                      displayStats.savedDayPointRatioDayBefore >= 0 ? 'bx-trending-up' : 'bx-trending-down'
                    }`}
                  ></i>
                  <span>{displayStats.savedDayPointRatioDayBefore}%</span>
                </div>
              </div>
              <p className="text-xs text-muted-foreground">from yesterday</p>
            </div>
            <div className="flex h-12 w-12 items-center justify-center rounded-full bg-blue-50 text-blue-600">
              <i className="bx bx-trending-up h-6 w-6"></i>
            </div>
          </div>
        </div>

        {/* Week Point Card */}
        <div className="bg-card p-6 rounded-lg border">
          <div className="flex items-center justify-between">
            <div className="space-y-2">
              <p className="text-sm font-medium text-muted-foreground">Weekly Points</p>
              <div className="flex items-baseline space-x-2">
                <p className="text-2xl font-bold">{displayStats.savedWeekPoint}</p>
                <div
                  className={`flex items-center text-xs ${
                    displayStats.savedWeekPointRatioWeekBefore >= 0 ? 'text-emerald-600' : 'text-destructive'
                  }`}
                >
                  <i
                    className={`bx h-3 w-3 mr-1 ${
                      displayStats.savedWeekPointRatioWeekBefore >= 0 ? 'bx-trending-up' : 'bx-trending-down'
                    }`}
                  ></i>
                  <span>{displayStats.savedWeekPointRatioWeekBefore}%</span>
                </div>
              </div>
              <p className="text-xs text-muted-foreground">from last week</p>
            </div>
            <div className="flex h-12 w-12 items-center justify-center rounded-full bg-green-50 text-green-600">
              <i className="bx bx-calendar h-6 w-6"></i>
            </div>
          </div>
        </div>

        {/* Day Point URL Card */}
        <div className="bg-card p-6 rounded-lg border">
          <div className="flex items-center justify-between">
            <div className="space-y-2">
              <p className="text-sm font-medium text-muted-foreground">Daily URLs</p>
              <div className="flex items-baseline space-x-2">
                <p className="text-2xl font-bold">{displayStats.pointUrlDayCnt}</p>
                <span className="text-xs text-muted-foreground">collected</span>
              </div>
              <p className="text-xs text-muted-foreground">URLs found today</p>
            </div>
            <div className="flex h-12 w-12 items-center justify-center rounded-full bg-purple-50 text-purple-600">
              <i className="bx bx-link h-6 w-6"></i>
            </div>
          </div>
        </div>

        {/* Week Point URL Card */}
        <div className="bg-card p-6 rounded-lg border">
          <div className="flex items-center justify-between">
            <div className="space-y-2">
              <p className="text-sm font-medium text-muted-foreground">Weekly URLs</p>
              <div className="flex items-baseline space-x-2">
                <p className="text-2xl font-bold">{displayStats.pointUrlWeekCnt}</p>
                <span className="text-xs text-muted-foreground">collected</span>
              </div>
              <p className="text-xs text-muted-foreground">URLs found this week</p>
            </div>
            <div className="flex h-12 w-12 items-center justify-center rounded-full bg-orange-50 text-orange-600">
              <i className="bx bx-collection h-6 w-6"></i>
            </div>
          </div>
        </div>
      </div>

      {/* Quick Actions */}
      <div className="bg-card rounded-lg border">
        <div className="p-6 border-b">
          <div className="flex items-center justify-between">
            <div className="space-y-1">
              <div className="text-lg font-semibold">Quick Actions</div>
              <div className="text-sm text-muted-foreground">Common tasks and shortcuts</div>
            </div>
          </div>
        </div>
        <div className="p-6">
          <div className="grid gap-4 md:grid-cols-3">
            <button
              onClick={() => (window.location.href = '/point-logs')}
              className="border rounded-lg p-6 h-auto flex flex-col items-center space-y-2 hover:bg-accent transition-colors"
            >
              <div className="flex h-12 w-12 items-center justify-center rounded-full bg-blue-50">
                <i className="bx bx-history h-6 w-6 text-blue-600"></i>
              </div>
              <div className="text-center">
                <div className="font-medium">View Point Log</div>
                <div className="text-xs text-muted-foreground">Check transaction history</div>
              </div>
            </button>

            <button
              onClick={() => (window.location.href = '/point-urls')}
              className="border rounded-lg p-6 h-auto flex flex-col items-center space-y-2 hover:bg-accent transition-colors"
            >
              <div className="flex h-12 w-12 items-center justify-center rounded-full bg-green-50">
                <i className="bx bx-link h-6 w-6 text-green-600"></i>
              </div>
              <div className="text-center">
                <div className="font-medium">Manage URLs</div>
                <div className="text-xs text-muted-foreground">Configure point URLs</div>
              </div>
            </button>

            <button
              onClick={() => (window.location.href = '/cookies')}
              className="border rounded-lg p-6 h-auto flex flex-col items-center space-y-2 hover:bg-accent transition-colors"
            >
              <div className="flex h-12 w-12 items-center justify-center rounded-full bg-purple-50">
                <i className="bx bx-cookie h-6 w-6 text-purple-600"></i>
              </div>
              <div className="text-center">
                <div className="font-medium">Manage Cookies</div>
                <div className="text-xs text-muted-foreground">Update authentication</div>
              </div>
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}
