import { useQuery } from '@tanstack/react-query';
import { dashboardApi } from '@/api/dashboard';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { formatNumber } from '@/lib/utils';
import { TrendingUp, TrendingDown, Coins, Link } from 'lucide-react';

export function DashboardPage() {
  const { data: stats, isLoading } = useQuery({
    queryKey: ['dashboard', 'stats'],
    queryFn: dashboardApi.getStats,
  });

  if (isLoading) {
    return (
      <div className="flex items-center justify-center h-64">
        <p className="text-muted-foreground">Loading...</p>
      </div>
    );
  }

  const StatCard = ({
    title,
    value,
    ratio,
    icon: Icon,
    ratioLabel,
  }: {
    title: string;
    value: number;
    ratio?: number;
    icon: React.ElementType;
    ratioLabel?: string;
  }) => (
    <Card>
      <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
        <CardTitle className="text-sm font-medium">{title}</CardTitle>
        <Icon className="h-4 w-4 text-muted-foreground" />
      </CardHeader>
      <CardContent>
        <div className="text-2xl font-bold">{formatNumber(value)}</div>
        {ratio !== undefined && (
          <p className="text-xs text-muted-foreground flex items-center gap-1">
            {ratio >= 0 ? (
              <TrendingUp className="h-3 w-3 text-green-500" />
            ) : (
              <TrendingDown className="h-3 w-3 text-red-500" />
            )}
            <span className={ratio >= 0 ? 'text-green-500' : 'text-red-500'}>
              {ratio >= 0 ? '+' : ''}{ratio.toFixed(1)}%
            </span>
            <span className="ml-1">{ratioLabel}</span>
          </p>
        )}
      </CardContent>
    </Card>
  );

  return (
    <div className="space-y-6">
      <h2 className="text-3xl font-bold tracking-tight">Dashboard</h2>

      <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
        <StatCard
          title="오늘 적립 포인트"
          value={stats?.savedDayPoint || 0}
          ratio={stats?.savedDayPointRatioDayBefore}
          icon={Coins}
          ratioLabel="전일 대비"
        />
        <StatCard
          title="이번 주 적립 포인트"
          value={stats?.savedWeekPoint || 0}
          ratio={stats?.savedWeekPointRatioWeekBefore}
          icon={Coins}
          ratioLabel="전주 대비"
        />
        <StatCard
          title="오늘 수집 URL"
          value={stats?.pointUrlDayCnt || 0}
          icon={Link}
        />
        <StatCard
          title="이번 주 수집 URL"
          value={stats?.pointUrlWeekCnt || 0}
          icon={Link}
        />
      </div>
    </div>
  );
}
