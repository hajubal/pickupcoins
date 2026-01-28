import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { pointLogsApi } from '@/api/point-logs';
import { SavedPointQuery } from '@/types';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table';
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
} from '@/components/ui/alert-dialog';
import { useToast } from '@/components/ui/use-toast';
import { formatDate, formatNumber } from '@/lib/utils';
import { Trash2, ChevronLeft, ChevronRight, Search } from 'lucide-react';

export function PointLogsPage() {
  const queryClient = useQueryClient();
  const { toast } = useToast();
  const [isDeleteDialogOpen, setIsDeleteDialogOpen] = useState(false);
  const [deletingLogId, setDeletingLogId] = useState<string | null>(null);
  const [query, setQuery] = useState<SavedPointQuery>({
    page: 1,
    size: 20,
  });
  const [filterStartDate, setFilterStartDate] = useState('');
  const [filterEndDate, setFilterEndDate] = useState('');

  const { data, isLoading } = useQuery({
    queryKey: ['point-logs', query],
    queryFn: () => pointLogsApi.findAll(query),
  });

  const deleteMutation = useMutation({
    mutationFn: (id: string) => pointLogsApi.delete(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['point-logs'] });
      toast({ title: '포인트 로그가 삭제되었습니다.' });
    },
    onError: () => {
      toast({ variant: 'destructive', title: '포인트 로그 삭제에 실패했습니다.' });
    },
  });

  const handleDelete = (id: string) => {
    setDeletingLogId(id);
    setIsDeleteDialogOpen(true);
  };

  const confirmDelete = () => {
    if (deletingLogId) {
      deleteMutation.mutate(deletingLogId);
    }
    setIsDeleteDialogOpen(false);
    setDeletingLogId(null);
  };

  const handleSearch = () => {
    setQuery((prev) => ({
      ...prev,
      page: 1,
      startDate: filterStartDate || undefined,
      endDate: filterEndDate || undefined,
    }));
  };

  const handlePageChange = (newPage: number) => {
    setQuery((prev) => ({
      ...prev,
      page: newPage,
    }));
  };

  const handleClearFilters = () => {
    setFilterStartDate('');
    setFilterEndDate('');
    setQuery({
      page: 1,
      size: 20,
    });
  };

  if (isLoading) {
    return (
      <div className="flex items-center justify-center h-64">
        <p className="text-muted-foreground">Loading...</p>
      </div>
    );
  }

  const logs = data?.content || [];
  const currentPage = data?.currentPage || 1;
  const totalPages = data?.totalPages || 1;
  const totalItems = data?.totalItems || 0;

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h2 className="text-3xl font-bold tracking-tight">Point Logs</h2>
      </div>

      <div className="flex flex-wrap items-end gap-4 p-4 bg-card rounded-lg border">
        <div className="space-y-2">
          <Label htmlFor="startDate">시작 날짜</Label>
          <Input
            id="startDate"
            type="date"
            value={filterStartDate}
            onChange={(e) => setFilterStartDate(e.target.value)}
            className="w-40"
          />
        </div>
        <div className="space-y-2">
          <Label htmlFor="endDate">종료 날짜</Label>
          <Input
            id="endDate"
            type="date"
            value={filterEndDate}
            onChange={(e) => setFilterEndDate(e.target.value)}
            className="w-40"
          />
        </div>
        <Button onClick={handleSearch}>
          <Search className="h-4 w-4 mr-2" />
          검색
        </Button>
        <Button variant="outline" onClick={handleClearFilters}>
          초기화
        </Button>
      </div>

      <div className="rounded-md border">
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead>ID</TableHead>
              <TableHead>사용자</TableHead>
              <TableHead>사이트</TableHead>
              <TableHead>적립 금액</TableHead>
              <TableHead>생성일</TableHead>
              <TableHead className="w-[80px]">Actions</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {logs.map((log) => (
              <TableRow key={log.id}>
                <TableCell className="font-mono text-sm">{log.id}</TableCell>
                <TableCell>{log.userName || '-'}</TableCell>
                <TableCell>{log.siteName || '-'}</TableCell>
                <TableCell className="font-medium">{formatNumber(log.amount)}원</TableCell>
                <TableCell>{formatDate(log.createdDate)}</TableCell>
                <TableCell>
                  <Button
                    variant="ghost"
                    size="icon"
                    onClick={() => handleDelete(log.id)}
                  >
                    <Trash2 className="h-4 w-4 text-destructive" />
                  </Button>
                </TableCell>
              </TableRow>
            ))}
            {logs.length === 0 && (
              <TableRow>
                <TableCell colSpan={6} className="text-center text-muted-foreground">
                  No point logs found
                </TableCell>
              </TableRow>
            )}
          </TableBody>
        </Table>
      </div>

      <div className="flex items-center justify-between">
        <p className="text-sm text-muted-foreground">
          총 {formatNumber(totalItems)}개 중 {(currentPage - 1) * 20 + 1}-
          {Math.min(currentPage * 20, totalItems)}개 표시
        </p>
        <div className="flex items-center gap-2">
          <Button
            variant="outline"
            size="sm"
            onClick={() => handlePageChange(currentPage - 1)}
            disabled={currentPage <= 1}
          >
            <ChevronLeft className="h-4 w-4" />
            이전
          </Button>
          <span className="text-sm">
            {currentPage} / {totalPages}
          </span>
          <Button
            variant="outline"
            size="sm"
            onClick={() => handlePageChange(currentPage + 1)}
            disabled={currentPage >= totalPages}
          >
            다음
            <ChevronRight className="h-4 w-4" />
          </Button>
        </div>
      </div>

      <AlertDialog open={isDeleteDialogOpen} onOpenChange={setIsDeleteDialogOpen}>
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>정말 삭제하시겠습니까?</AlertDialogTitle>
            <AlertDialogDescription>
              이 작업은 되돌릴 수 없습니다. 포인트 로그가 영구적으로 삭제됩니다.
            </AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter>
            <AlertDialogCancel>Cancel</AlertDialogCancel>
            <AlertDialogAction onClick={confirmDelete}>Delete</AlertDialogAction>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>
    </div>
  );
}
