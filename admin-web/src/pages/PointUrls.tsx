import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import axiosInstance from '@/lib/axios';

interface PointUrl {
  id: number;
  name: string;
  url: string;
  pointUrlType: string;
  permanent: boolean;
  createdDate: string;
  modifiedDate: string;
}

interface PointUrlRequest {
  url: string;
  permanent?: boolean;
}

export default function PointUrls() {
  const queryClient = useQueryClient();
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingPointUrl, setEditingPointUrl] = useState<PointUrl | null>(null);
  const [formData, setFormData] = useState<PointUrlRequest>({
    url: '',
    permanent: false,
  });

  // Fetch point URLs
  const { data: pointUrls = [], isLoading } = useQuery<PointUrl[]>({
    queryKey: ['point-urls'],
    queryFn: async () => {
      const response = await axiosInstance.get('/point-urls');
      return response.data;
    },
  });

  // Create point URL mutation
  const createMutation = useMutation({
    mutationFn: async (data: PointUrlRequest) => {
      const response = await axiosInstance.post('/point-urls', data);
      return response.data;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['point-urls'] });
      closeModal();
    },
  });

  // Update point URL mutation
  const updateMutation = useMutation({
    mutationFn: async ({ id, data }: { id: number; data: PointUrlRequest }) => {
      const response = await axiosInstance.put(`/point-urls/${id}`, data);
      return response.data;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['point-urls'] });
      closeModal();
    },
  });

  // Delete point URL mutation
  const deleteMutation = useMutation({
    mutationFn: async (id: number) => {
      await axiosInstance.delete(`/point-urls/${id}`);
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['point-urls'] });
    },
  });

  // Toggle permanent mutation
  const togglePermanentMutation = useMutation({
    mutationFn: async (id: number) => {
      const response = await axiosInstance.patch(`/point-urls/${id}/toggle-permanent`);
      return response.data;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['point-urls'] });
    },
  });

  const openModal = (pointUrl?: PointUrl) => {
    if (pointUrl) {
      setEditingPointUrl(pointUrl);
      setFormData({
        url: pointUrl.url,
        permanent: pointUrl.permanent,
      });
    } else {
      setEditingPointUrl(null);
      setFormData({
        url: '',
        permanent: false,
      });
    }
    setIsModalOpen(true);
  };

  const closeModal = () => {
    setIsModalOpen(false);
    setEditingPointUrl(null);
    setFormData({
      url: '',
      permanent: false,
    });
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (editingPointUrl) {
      updateMutation.mutate({ id: editingPointUrl.id, data: formData });
    } else {
      createMutation.mutate(formData);
    }
  };

  const handleDelete = (id: number) => {
    if (window.confirm('정말 삭제하시겠습니까?')) {
      deleteMutation.mutate(id);
    }
  };

  const handleTogglePermanent = (id: number) => {
    togglePermanentMutation.mutate(id);
  };

  const getTypeColor = (type: string) => {
    switch (type) {
      case 'NAVER':
        return 'bg-emerald-100 text-emerald-800';
      case 'OFW_NAVER':
        return 'bg-blue-100 text-blue-800';
      case 'UNSUPPORT':
        return 'bg-gray-100 text-gray-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
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
          <h1 className="text-3xl font-bold">Point URLs Management</h1>
          <p className="text-muted-foreground mt-1">포인트 URL을 관리합니다</p>
        </div>
        <button
          onClick={() => openModal()}
          className="bg-primary text-primary-foreground px-4 py-2 rounded-md hover:bg-primary/90 transition-colors flex items-center gap-2"
        >
          <i className="bx bx-plus"></i>
          Add Point URL
        </button>
      </div>

      <div className="bg-card rounded-xl border shadow-md overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full">
            <thead className="bg-muted/50 border-b">
              <tr>
                <th className="px-6 py-4 text-left text-sm font-semibold">ID</th>
                <th className="px-6 py-4 text-left text-sm font-semibold">Name</th>
                <th className="px-6 py-4 text-left text-sm font-semibold">URL</th>
                <th className="px-6 py-4 text-left text-sm font-semibold">Type</th>
                <th className="px-6 py-4 text-left text-sm font-semibold">Permanent</th>
                <th className="px-6 py-4 text-left text-sm font-semibold">Created</th>
                <th className="px-6 py-4 text-right text-sm font-semibold">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y">
              {pointUrls.length === 0 ? (
                <tr>
                  <td colSpan={7} className="px-4 py-8 text-center text-muted-foreground">
                    등록된 포인트 URL이 없습니다
                  </td>
                </tr>
              ) : (
                pointUrls.map((pointUrl) => (
                  <tr key={pointUrl.id} className="hover:bg-muted/30 transition-all duration-150">
                    <td className="px-6 py-4 text-sm">{pointUrl.id}</td>
                    <td className="px-6 py-4 text-sm font-medium">{pointUrl.name || '-'}</td>
                    <td className="px-6 py-4 text-sm">
                      <a
                        href={pointUrl.url}
                        target="_blank"
                        rel="noopener noreferrer"
                        className="text-blue-600 hover:underline flex items-center gap-1 font-mono text-xs max-w-md truncate"
                      >
                        {pointUrl.url}
                        <i className="bx bx-link-external text-xs"></i>
                      </a>
                    </td>
                    <td className="px-6 py-4 text-sm">
                      <span className={`px-3 py-1.5 rounded-full text-xs font-medium ${getTypeColor(pointUrl.pointUrlType)}`}>
                        {pointUrl.pointUrlType}
                      </span>
                    </td>
                    <td className="px-6 py-4 text-sm">
                      <button
                        onClick={() => handleTogglePermanent(pointUrl.id)}
                        className={`px-3 py-1.5 rounded-full text-xs font-medium transition-all duration-200 ${
                          pointUrl.permanent
                            ? 'bg-emerald-100 text-emerald-800 hover:bg-emerald-200'
                            : 'bg-gray-100 text-gray-800 hover:bg-gray-200'
                        }`}
                      >
                        {pointUrl.permanent ? 'Yes' : 'No'}
                      </button>
                    </td>
                    <td className="px-6 py-4 text-sm text-muted-foreground">
                      {new Date(pointUrl.createdDate).toLocaleDateString()}
                    </td>
                    <td className="px-6 py-4 text-sm text-right">
                      <div className="flex justify-end gap-2">
                        <button
                          onClick={() => openModal(pointUrl)}
                          className="text-blue-600 hover:text-blue-800 transition-colors"
                        >
                          <i className="bx bx-edit"></i>
                        </button>
                        <button
                          onClick={() => handleDelete(pointUrl.id)}
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
              {editingPointUrl ? 'Edit Point URL' : 'Add Point URL'}
            </h2>
            <form onSubmit={handleSubmit}>
              <div className="space-y-4">
                <div>
                  <label className="block text-sm font-medium mb-1 text-gray-700">URL</label>
                  <input
                    type="url"
                    value={formData.url}
                    onChange={(e) => setFormData({ ...formData, url: e.target.value })}
                    className="w-full px-3 py-2 border border-gray-300 rounded-md bg-white text-gray-900 focus:ring-2 focus:ring-blue-500 focus:border-transparent font-mono text-sm"
                    placeholder="e.g., https://example.com/points"
                    required
                  />
                </div>
                <div>
                  <label className="flex items-center gap-2 text-sm font-medium text-gray-700">
                    <input
                      type="checkbox"
                      checked={formData.permanent || false}
                      onChange={(e) => setFormData({ ...formData, permanent: e.target.checked })}
                      className="w-4 h-4 text-blue-600 border-gray-300 rounded focus:ring-blue-500"
                    />
                    Permanent URL
                  </label>
                  <p className="text-xs text-gray-500 mt-1 ml-6">
                    영구적으로 유지할 URL로 표시합니다
                  </p>
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
                  {editingPointUrl ? 'Update' : 'Create'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
