import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import axiosInstance from '@/lib/axios';

interface SavedPoint {
  id: number;
  cookieId: number;
  cookieUserName: string;
  cookieSiteName: string;
  amount: number;
  responseBody: string;
  createdDate: string;
  modifiedDate: string;
}

interface PointLogsResponse {
  content: SavedPoint[];
  currentPage: number;
  totalItems: number;
  totalPages: number;
}

interface DialogState {
  isOpen: boolean;
  title: string;
  message: string;
  onConfirm?: () => void;
}

export default function PointLogs() {
  const queryClient = useQueryClient();
  const [page, setPage] = useState(0);
  const [startDate, setStartDate] = useState('');
  const [endDate, setEndDate] = useState('');
  const [selectedPoint, setSelectedPoint] = useState<SavedPoint | null>(null);
  const [isDetailModalOpen, setIsDetailModalOpen] = useState(false);
  const [dialog, setDialog] = useState<DialogState>({
    isOpen: false,
    title: '',
    message: '',
  });

  const closeDialog = () => {
    setDialog({ ...dialog, isOpen: false });
  };

  const showConfirm = (title: string, message: string, onConfirm: () => void) => {
    setDialog({
      isOpen: true,
      title,
      message,
      onConfirm,
    });
  };

  // Fetch point logs
  const { data: pointLogsData, isLoading } = useQuery<PointLogsResponse>({
    queryKey: ['point-logs', page, startDate, endDate],
    queryFn: async () => {
      const params = new URLSearchParams({
        page: page.toString(),
        size: '20',
      });
      if (startDate) params.append('startDate', startDate);
      if (endDate) params.append('endDate', endDate);

      const response = await axiosInstance.get(`/point-logs?${params.toString()}`);
      return response.data;
    },
  });

  // Delete mutation
  const deleteMutation = useMutation({
    mutationFn: async (id: number) => {
      await axiosInstance.delete(`/point-logs/${id}`);
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['point-logs'] });
    },
  });

  const handleDelete = (id: number) => {
    showConfirm(
      '삭제 확인',
      '정말 삭제하시겠습니까?',
      () => deleteMutation.mutate(id)
    );
  };

  const handleViewDetail = (point: SavedPoint) => {
    setSelectedPoint(point);
    setIsDetailModalOpen(true);
  };

  const handleClearFilter = () => {
    setStartDate('');
    setEndDate('');
    setPage(0);
  };

  if (isLoading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="text-muted-foreground">Loading...</div>
      </div>
    );
  }

  const pointLogs = pointLogsData?.content || [];
  const totalPages = pointLogsData?.totalPages || 0;
  const totalItems = pointLogsData?.totalItems || 0;

  return (
    <div className="max-w-7xl mx-auto">
      <div className="flex justify-between items-center mb-6">
        <div>
          <h1 className="text-3xl font-bold">Point Logs</h1>
          <p className="text-muted-foreground mt-1">포인트 적립 내역을 조회합니다</p>
        </div>
        <div className="text-sm text-muted-foreground">
          Total: {totalItems.toLocaleString()} items
        </div>
      </div>

      {/* Filter Section */}
      <div className="bg-card rounded-xl border shadow-md p-6 mb-6">
        <div className="flex items-end gap-4">
          <div className="flex-1">
            <label className="block text-sm font-semibold mb-2">Start Date</label>
            <input
              type="date"
              value={startDate}
              onChange={(e) => setStartDate(e.target.value)}
              className="w-full px-3 py-2 border rounded-md bg-background focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all"
            />
          </div>
          <div className="flex-1">
            <label className="block text-sm font-semibold mb-2">End Date</label>
            <input
              type="date"
              value={endDate}
              onChange={(e) => setEndDate(e.target.value)}
              className="w-full px-3 py-2 border rounded-md bg-background focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all"
            />
          </div>
          <button
            onClick={handleClearFilter}
            className="px-4 py-2 border rounded-md hover:bg-muted transition-all duration-200 font-medium"
          >
            Clear
          </button>
        </div>
      </div>

      {/* Table */}
      <div className="bg-card rounded-xl border shadow-md overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full">
            <thead className="bg-muted/50 border-b">
              <tr>
                <th className="px-6 py-4 text-left text-sm font-semibold">ID</th>
                <th className="px-6 py-4 text-left text-sm font-semibold">User</th>
                <th className="px-6 py-4 text-left text-sm font-semibold">Site</th>
                <th className="px-6 py-4 text-right text-sm font-semibold">Amount</th>
                <th className="px-6 py-4 text-left text-sm font-semibold">Created</th>
                <th className="px-6 py-4 text-right text-sm font-semibold">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y">
              {pointLogs.length === 0 ? (
                <tr>
                  <td colSpan={6} className="px-4 py-8 text-center text-muted-foreground">
                    포인트 적립 내역이 없습니다
                  </td>
                </tr>
              ) : (
                pointLogs.map((point) => (
                  <tr key={point.id} className="hover:bg-muted/30 transition-all duration-150">
                    <td className="px-6 py-4 text-sm">{point.id}</td>
                    <td className="px-6 py-4 text-sm font-medium">{point.cookieUserName}</td>
                    <td className="px-6 py-4 text-sm">{point.cookieSiteName}</td>
                    <td className="px-6 py-4 text-sm text-right">
                      <span className="inline-flex items-center px-3 py-1.5 rounded-full text-xs font-medium bg-emerald-100 text-emerald-800">
                        +{point.amount.toLocaleString()}
                      </span>
                    </td>
                    <td className="px-6 py-4 text-sm text-muted-foreground">
                      {new Date(point.createdDate).toLocaleString()}
                    </td>
                    <td className="px-6 py-4 text-sm text-right">
                      <div className="flex justify-end gap-2">
                        <button
                          onClick={() => handleViewDetail(point)}
                          className="text-blue-600 hover:text-blue-800 transition-colors"
                          title="View Response"
                        >
                          <i className="bx bx-file"></i>
                        </button>
                        <button
                          onClick={() => handleDelete(point.id)}
                          className="text-red-600 hover:text-red-800 transition-colors"
                        >
                          <i className="bx bx-trash"></i>
                        </button>
                      </div>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>

        {/* Pagination */}
        {totalPages > 0 && (
          <div className="border-t p-6 flex items-center justify-between">
            <div className="text-sm text-muted-foreground font-medium">
              Page {page + 1} of {totalPages}
            </div>
            <div className="flex gap-2">
              <button
                onClick={() => setPage(Math.max(0, page - 1))}
                disabled={page === 0}
                className="px-4 py-2 border rounded-md hover:bg-muted transition-all duration-200 disabled:opacity-50 disabled:cursor-not-allowed font-medium"
              >
                Previous
              </button>
              <button
                onClick={() => setPage(Math.min(totalPages - 1, page + 1))}
                disabled={page >= totalPages - 1}
                className="px-4 py-2 border rounded-md hover:bg-muted transition-all duration-200 disabled:opacity-50 disabled:cursor-not-allowed font-medium"
              >
                Next
              </button>
            </div>
          </div>
        )}
      </div>

      {/* Detail Modal */}
      {isDetailModalOpen && selectedPoint && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-lg shadow-xl w-full max-w-3xl max-h-[90vh] overflow-hidden flex flex-col">
            <div className="p-6 border-b">
              <h2 className="text-2xl font-bold text-gray-900">Response Detail</h2>
              <div className="mt-2 text-sm text-gray-600">
                <p>Point ID: {selectedPoint.id}</p>
                <p>User: {selectedPoint.cookieUserName}</p>
                <p>Amount: +{selectedPoint.amount.toLocaleString()}</p>
                <p>Created: {new Date(selectedPoint.createdDate).toLocaleString()}</p>
              </div>
            </div>
            <div className="flex-1 overflow-auto p-6">
              <pre className="bg-gray-50 p-4 rounded-md text-xs font-mono overflow-x-auto text-gray-900 whitespace-pre-wrap break-words">
                {selectedPoint.responseBody || 'No response body'}
              </pre>
            </div>
            <div className="border-t p-4 flex justify-end">
              <button
                onClick={() => setIsDetailModalOpen(false)}
                className="px-4 py-2 bg-gray-600 text-white rounded-md hover:bg-gray-700 transition-colors"
              >
                Close
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Confirm Dialog */}
      {dialog.isOpen && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
          <div className="bg-white rounded-lg shadow-xl w-full max-w-sm p-6">
            <div className="flex flex-col items-center text-center">
              <i className="bx bx-error text-4xl text-amber-500"></i>
              <h3 className="text-lg font-semibold mt-4 text-gray-900">{dialog.title}</h3>
              <p className="text-gray-600 mt-2">{dialog.message}</p>
            </div>
            <div className="flex justify-center gap-3 mt-6">
              <button
                onClick={closeDialog}
                className="px-4 py-2 border border-gray-300 rounded-md hover:bg-gray-50 transition-colors text-gray-700 min-w-[80px]"
              >
                취소
              </button>
              <button
                onClick={() => {
                  dialog.onConfirm?.();
                  closeDialog();
                }}
                className="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 transition-colors min-w-[80px]"
              >
                확인
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
