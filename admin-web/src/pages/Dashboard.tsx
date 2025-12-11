import { useQuery } from '@tanstack/react-query';
import axiosInstance from '@/lib/axios';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';

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
    refetchInterval: 30000,
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
    <>
      <div className="flex items-center justify-between">
        <h2 className="text-3xl font-bold tracking-tight">Dashboard</h2>
        <div className="flex items-center gap-2">
          <Button variant="outline" size="sm" onClick={() => refetch()} disabled={isLoading}>
            <i className={`bx bx-refresh mr-2 h-4 w-4 ${isLoading ? 'animate-spin' : ''}`}></i>
            Refresh
          </Button>
          <Badge variant={isLoading ? 'secondary' : 'success'} className="gap-1.5">
            {!isLoading && <span className="h-2 w-2 rounded-full bg-white animate-pulse" />}
            {isLoading ? 'Loading...' : 'Live'}
          </Badge>
        </div>
      </div>
      <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
          <Card>
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
              <CardTitle className="text-sm font-medium">Today's Points</CardTitle>
              <i className="bx bx-trending-up h-4 w-4 text-muted-foreground"></i>
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold">{displayStats.savedDayPoint.toLocaleString()}</div>
              <p className="text-xs text-muted-foreground flex items-center">
                <span className={displayStats.savedDayPointRatioDayBefore >= 0 ? 'text-emerald-600' : 'text-rose-600'}>
                  <i className={`bx ${displayStats.savedDayPointRatioDayBefore >= 0 ? 'bx-trending-up' : 'bx-trending-down'} mr-1`}></i>
                  {displayStats.savedDayPointRatioDayBefore}%
                </span>
                <span className="ml-1">from yesterday</span>
              </p>
            </CardContent>
          </Card>
          <Card>
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
              <CardTitle className="text-sm font-medium">Weekly Points</CardTitle>
              <i className="bx bx-calendar h-4 w-4 text-muted-foreground"></i>
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold">{displayStats.savedWeekPoint.toLocaleString()}</div>
              <p className="text-xs text-muted-foreground flex items-center">
                <span className={displayStats.savedWeekPointRatioWeekBefore >= 0 ? 'text-emerald-600' : 'text-rose-600'}>
                  <i className={`bx ${displayStats.savedWeekPointRatioWeekBefore >= 0 ? 'bx-trending-up' : 'bx-trending-down'} mr-1`}></i>
                  {displayStats.savedWeekPointRatioWeekBefore}%
                </span>
                <span className="ml-1">from last week</span>
              </p>
            </CardContent>
          </Card>
          <Card>
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
              <CardTitle className="text-sm font-medium">Daily URLs</CardTitle>
              <i className="bx bx-link h-4 w-4 text-muted-foreground"></i>
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold">{displayStats.pointUrlDayCnt}</div>
              <p className="text-xs text-muted-foreground">URLs found today</p>
            </CardContent>
          </Card>
          <Card>
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
              <CardTitle className="text-sm font-medium">Weekly URLs</CardTitle>
              <i className="bx bx-collection h-4 w-4 text-muted-foreground"></i>
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold">{displayStats.pointUrlWeekCnt}</div>
              <p className="text-xs text-muted-foreground">URLs found this week</p>
            </CardContent>
          </Card>
      </div>
      <Card>
        <CardHeader>
          <CardTitle>Quick Actions</CardTitle>
        </CardHeader>
          <CardContent className="grid gap-4 md:grid-cols-3">
            <button
              onClick={() => (window.location.href = '/point-logs')}
              className="flex flex-col items-center space-y-2 rounded-lg border p-4 hover:bg-accent transition-colors"
            >
              <div className="flex h-10 w-10 items-center justify-center rounded-full bg-primary/10">
                <i className="bx bx-history h-5 w-5"></i>
              </div>
              <div className="space-y-0.5 text-center">
                <div className="text-sm font-medium">View Point Logs</div>
                <p className="text-xs text-muted-foreground">Check transaction history</p>
              </div>
            </button>
            <button
              onClick={() => (window.location.href = '/point-urls')}
              className="flex flex-col items-center space-y-2 rounded-lg border p-4 hover:bg-accent transition-colors"
            >
              <div className="flex h-10 w-10 items-center justify-center rounded-full bg-primary/10">
                <i className="bx bx-link h-5 w-5"></i>
              </div>
              <div className="space-y-0.5 text-center">
                <div className="text-sm font-medium">Manage URLs</div>
                <p className="text-xs text-muted-foreground">Configure point URLs</p>
              </div>
            </button>
            <button
              onClick={() => (window.location.href = '/cookies')}
              className="flex flex-col items-center space-y-2 rounded-lg border p-4 hover:bg-accent transition-colors"
            >
              <div className="flex h-10 w-10 items-center justify-center rounded-full bg-primary/10">
                <i className="bx bx-cookie h-5 w-5"></i>
              </div>
              <div className="space-y-0.5 text-center">
                <div className="text-sm font-medium">Manage Cookies</div>
                <p className="text-xs text-muted-foreground">Update authentication</p>
              </div>
            </button>
        </CardContent>
      </Card>
    </>
  );
}
