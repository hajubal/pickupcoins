import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import axiosInstance from '@/lib/axios';

interface Cookie {
  id: number;
  userName: string;
  siteName: string;
  cookie: string;
  isValid: boolean;
  createdDate: string;
  modifiedDate: string;
}

interface CookieRequest {
  userName: string;
  siteName: string;
  cookie: string;
  isValid?: boolean;
}

interface DialogState {
  isOpen: boolean;
  title: string;
  message: string;
  onConfirm?: () => void;
}

export default function Cookies() {
  const queryClient = useQueryClient();
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingCookie, setEditingCookie] = useState<Cookie | null>(null);
  const [formData, setFormData] = useState<CookieRequest>({
    userName: '',
    siteName: '',
    cookie: '',
    isValid: true,
  });
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

  // Fetch cookies
  const { data: cookies = [], isLoading } = useQuery<Cookie[]>({
    queryKey: ['cookies'],
    queryFn: async () => {
      const response = await axiosInstance.get('/cookies');
      return response.data;
    },
  });

  // Create cookie mutation
  const createMutation = useMutation({
    mutationFn: async (data: CookieRequest) => {
      const response = await axiosInstance.post('/cookies', data);
      return response.data;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['cookies'] });
      closeModal();
    },
  });

  // Update cookie mutation
  const updateMutation = useMutation({
    mutationFn: async ({ id, data }: { id: number; data: CookieRequest }) => {
      const response = await axiosInstance.put(`/cookies/${id}`, data);
      return response.data;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['cookies'] });
      closeModal();
    },
  });

  // Delete cookie mutation
  const deleteMutation = useMutation({
    mutationFn: async (id: number) => {
      await axiosInstance.delete(`/cookies/${id}`);
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['cookies'] });
    },
  });

  // Toggle validity mutation
  const toggleValidityMutation = useMutation({
    mutationFn: async (id: number) => {
      const response = await axiosInstance.patch(`/cookies/${id}/toggle-validity`);
      return response.data;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['cookies'] });
    },
  });

  const openModal = (cookie?: Cookie) => {
    if (cookie) {
      setEditingCookie(cookie);
      setFormData({
        userName: cookie.userName,
        siteName: cookie.siteName,
        cookie: cookie.cookie,
        isValid: cookie.isValid,
      });
    } else {
      setEditingCookie(null);
      setFormData({
        userName: '',
        siteName: '',
        cookie: '',
        isValid: true,
      });
    }
    setIsModalOpen(true);
  };

  const closeModal = () => {
    setIsModalOpen(false);
    setEditingCookie(null);
    setFormData({
      userName: '',
      siteName: '',
      cookie: '',
      isValid: true,
    });
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (editingCookie) {
      updateMutation.mutate({ id: editingCookie.id, data: formData });
    } else {
      createMutation.mutate(formData);
    }
  };

  const handleDelete = (id: number) => {
    showConfirm(
      '삭제 확인',
      '정말 삭제하시겠습니까?',
      () => deleteMutation.mutate(id)
    );
  };

  const handleToggleValidity = (id: number) => {
    toggleValidityMutation.mutate(id);
  };

  if (isLoading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="text-muted-foreground">Loading...</div>
      </div>
    );
  }

  return (
    <div className="max-w-7xl mx-auto">
      <div className="flex justify-between items-center mb-6">
        <div>
          <h1 className="text-3xl font-bold">Cookies Management</h1>
          <p className="text-muted-foreground mt-1">사이트 쿠키를 관리합니다</p>
        </div>
        <button
          onClick={() => openModal()}
          className="bg-primary text-primary-foreground px-4 py-2 rounded-md hover:bg-primary/90 transition-colors flex items-center gap-2"
        >
          <i className="bx bx-plus"></i>
          Add Cookie
        </button>
      </div>

      <div className="bg-card rounded-xl border shadow-md overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full">
            <thead className="bg-muted/50 border-b">
              <tr>
                <th className="px-6 py-4 text-left text-sm font-semibold">ID</th>
                <th className="px-6 py-4 text-left text-sm font-semibold">Site Name</th>
                <th className="px-6 py-4 text-left text-sm font-semibold">User Name</th>
                <th className="px-6 py-4 text-left text-sm font-semibold">Cookie</th>
                <th className="px-6 py-4 text-left text-sm font-semibold">Status</th>
                <th className="px-6 py-4 text-left text-sm font-semibold">Created</th>
                <th className="px-6 py-4 text-right text-sm font-semibold">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y">
              {cookies.length === 0 ? (
                <tr>
                  <td colSpan={7} className="px-4 py-8 text-center text-muted-foreground">
                    등록된 쿠키가 없습니다
                  </td>
                </tr>
              ) : (
                cookies.map((cookie) => (
                  <tr key={cookie.id} className="hover:bg-muted/30 transition-all duration-150">
                    <td className="px-6 py-4 text-sm">{cookie.id}</td>
                    <td className="px-6 py-4 text-sm font-medium">{cookie.siteName}</td>
                    <td className="px-6 py-4 text-sm">{cookie.userName}</td>
                    <td className="px-6 py-4 text-sm">
                      <div className="max-w-xs truncate font-mono text-xs bg-muted/50 px-3 py-1.5 rounded-md">
                        {cookie.cookie}
                      </div>
                    </td>
                    <td className="px-6 py-4 text-sm">
                      <button
                        onClick={() => handleToggleValidity(cookie.id)}
                        className={`px-3 py-1.5 rounded-full text-xs font-medium transition-all duration-200 ${
                          cookie.isValid
                            ? 'bg-emerald-100 text-emerald-800 hover:bg-emerald-200'
                            : 'bg-rose-100 text-rose-800 hover:bg-rose-200'
                        }`}
                      >
                        {cookie.isValid ? 'Valid' : 'Invalid'}
                      </button>
                    </td>
                    <td className="px-6 py-4 text-sm text-muted-foreground">
                      {new Date(cookie.createdDate).toLocaleDateString()}
                    </td>
                    <td className="px-6 py-4 text-sm text-right">
                      <div className="flex justify-end gap-2">
                        <button
                          onClick={() => openModal(cookie)}
                          className="text-blue-600 hover:text-blue-800 transition-colors"
                        >
                          <i className="bx bx-edit"></i>
                        </button>
                        <button
                          onClick={() => handleDelete(cookie.id)}
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
      </div>

      {/* Modal */}
      {isModalOpen && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
          <div className="bg-white rounded-lg shadow-xl w-full max-w-md p-6">
            <h2 className="text-2xl font-bold mb-4 text-gray-900">
              {editingCookie ? 'Edit Cookie' : 'Add Cookie'}
            </h2>
            <form onSubmit={handleSubmit}>
              <div className="space-y-4">
                <div>
                  <label className="block text-sm font-medium mb-1 text-gray-700">Site Name</label>
                  <input
                    type="text"
                    value={formData.siteName}
                    onChange={(e) => setFormData({ ...formData, siteName: e.target.value })}
                    className="w-full px-3 py-2 border border-gray-300 rounded-md bg-white text-gray-900 focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                    required
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium mb-1 text-gray-700">User Name</label>
                  <input
                    type="text"
                    value={formData.userName}
                    onChange={(e) => setFormData({ ...formData, userName: e.target.value })}
                    className="w-full px-3 py-2 border border-gray-300 rounded-md bg-white text-gray-900 focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                    required
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium mb-1 text-gray-700">Cookie</label>
                  <textarea
                    value={formData.cookie}
                    onChange={(e) => setFormData({ ...formData, cookie: e.target.value })}
                    className="w-full px-3 py-2 border border-gray-300 rounded-md bg-white text-gray-900 font-mono text-sm focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                    rows={4}
                    required
                  />
                </div>
                <div className="flex items-center">
                  <input
                    type="checkbox"
                    id="isValid"
                    checked={formData.isValid}
                    onChange={(e) => setFormData({ ...formData, isValid: e.target.checked })}
                    className="mr-2 h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300 rounded"
                  />
                  <label htmlFor="isValid" className="text-sm font-medium text-gray-700">
                    Valid Cookie
                  </label>
                </div>
              </div>
              <div className="flex justify-end gap-2 mt-6">
                <button
                  type="button"
                  onClick={closeModal}
                  className="px-4 py-2 border border-gray-300 rounded-md hover:bg-gray-50 transition-colors text-gray-700"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  disabled={createMutation.isPending || updateMutation.isPending}
                  className="bg-blue-600 text-white px-4 py-2 rounded-md hover:bg-blue-700 transition-colors disabled:opacity-50"
                >
                  {editingCookie ? 'Update' : 'Create'}
                </button>
              </div>
            </form>
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
