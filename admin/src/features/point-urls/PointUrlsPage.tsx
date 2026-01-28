import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { pointUrlsApi } from '@/api/point-urls';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { PointUrl, CreatePointUrlRequest, UpdatePointUrlRequest } from '@/types';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Switch } from '@/components/ui/switch';
import { Badge } from '@/components/ui/badge';
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table';
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog';
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
import { formatDate } from '@/lib/utils';
import { Plus, Pencil, Trash2, ExternalLink } from 'lucide-react';

const pointUrlSchema = z.object({
  url: z.string().url('올바른 URL을 입력해주세요'),
  permanent: z.boolean().optional(),
});

type PointUrlFormData = z.infer<typeof pointUrlSchema>;

export function PointUrlsPage() {
  const queryClient = useQueryClient();
  const { toast } = useToast();
  const [isDialogOpen, setIsDialogOpen] = useState(false);
  const [isDeleteDialogOpen, setIsDeleteDialogOpen] = useState(false);
  const [editingPointUrl, setEditingPointUrl] = useState<PointUrl | null>(null);
  const [deletingPointUrlId, setDeletingPointUrlId] = useState<string | null>(null);

  const { data: pointUrls, isLoading } = useQuery({
    queryKey: ['point-urls'],
    queryFn: pointUrlsApi.findAll,
  });

  const createMutation = useMutation({
    mutationFn: (data: CreatePointUrlRequest) => pointUrlsApi.create(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['point-urls'] });
      toast({ title: 'Point URL이 생성되었습니다.' });
      handleCloseDialog();
    },
    onError: () => {
      toast({ variant: 'destructive', title: 'Point URL 생성에 실패했습니다.' });
    },
  });

  const updateMutation = useMutation({
    mutationFn: ({ id, data }: { id: string; data: UpdatePointUrlRequest }) =>
      pointUrlsApi.update(id, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['point-urls'] });
      toast({ title: 'Point URL이 수정되었습니다.' });
      handleCloseDialog();
    },
    onError: () => {
      toast({ variant: 'destructive', title: 'Point URL 수정에 실패했습니다.' });
    },
  });

  const deleteMutation = useMutation({
    mutationFn: (id: string) => pointUrlsApi.delete(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['point-urls'] });
      toast({ title: 'Point URL이 삭제되었습니다.' });
    },
    onError: () => {
      toast({ variant: 'destructive', title: 'Point URL 삭제에 실패했습니다.' });
    },
  });

  const togglePermanentMutation = useMutation({
    mutationFn: (id: string) => pointUrlsApi.togglePermanent(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['point-urls'] });
    },
    onError: () => {
      toast({ variant: 'destructive', title: '영구 상태 변경에 실패했습니다.' });
    },
  });

  const {
    register,
    handleSubmit,
    reset,
    setValue,
    watch,
    formState: { errors },
  } = useForm<PointUrlFormData>({
    resolver: zodResolver(pointUrlSchema),
    defaultValues: {
      permanent: false,
    },
  });

  const handleOpenCreate = () => {
    setEditingPointUrl(null);
    reset({ url: '', permanent: false });
    setIsDialogOpen(true);
  };

  const handleOpenEdit = (pointUrl: PointUrl) => {
    setEditingPointUrl(pointUrl);
    reset({
      url: pointUrl.url,
      permanent: pointUrl.permanent,
    });
    setIsDialogOpen(true);
  };

  const handleCloseDialog = () => {
    setIsDialogOpen(false);
    setEditingPointUrl(null);
    reset();
  };

  const handleDelete = (id: string) => {
    setDeletingPointUrlId(id);
    setIsDeleteDialogOpen(true);
  };

  const confirmDelete = () => {
    if (deletingPointUrlId) {
      deleteMutation.mutate(deletingPointUrlId);
    }
    setIsDeleteDialogOpen(false);
    setDeletingPointUrlId(null);
  };

  const onSubmit = (data: PointUrlFormData) => {
    if (editingPointUrl) {
      updateMutation.mutate({ id: editingPointUrl.id, data });
    } else {
      createMutation.mutate(data);
    }
  };

  const permanentValue = watch('permanent');

  const getTypeBadgeVariant = (type: string | null) => {
    switch (type) {
      case 'NAVER':
        return 'success';
      case 'OFW_NAVER':
        return 'warning';
      case 'UNSUPPORT':
        return 'destructive';
      default:
        return 'secondary';
    }
  };

  if (isLoading) {
    return (
      <div className="flex items-center justify-center h-64">
        <p className="text-muted-foreground">Loading...</p>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h2 className="text-3xl font-bold tracking-tight">Point URLs</h2>
        <Button onClick={handleOpenCreate}>
          <Plus className="h-4 w-4 mr-2" />
          Add Point URL
        </Button>
      </div>

      <div className="rounded-md border">
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead>이름</TableHead>
              <TableHead>URL</TableHead>
              <TableHead>타입</TableHead>
              <TableHead>영구</TableHead>
              <TableHead>생성일</TableHead>
              <TableHead className="w-[100px]">Actions</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {pointUrls?.map((pointUrl) => (
              <TableRow key={pointUrl.id}>
                <TableCell className="font-medium">{pointUrl.name}</TableCell>
                <TableCell>
                  <div className="flex items-center gap-2 max-w-xs">
                    <span className="truncate text-sm">{pointUrl.url}</span>
                    <a
                      href={pointUrl.url}
                      target="_blank"
                      rel="noopener noreferrer"
                      className="text-muted-foreground hover:text-foreground"
                    >
                      <ExternalLink className="h-4 w-4" />
                    </a>
                  </div>
                </TableCell>
                <TableCell>
                  <Badge variant={getTypeBadgeVariant(pointUrl.pointUrlType)}>
                    {pointUrl.pointUrlType || 'UNKNOWN'}
                  </Badge>
                </TableCell>
                <TableCell>
                  <Switch
                    checked={pointUrl.permanent}
                    onCheckedChange={() => togglePermanentMutation.mutate(pointUrl.id)}
                  />
                </TableCell>
                <TableCell>{formatDate(pointUrl.createdDate)}</TableCell>
                <TableCell>
                  <div className="flex gap-2">
                    <Button
                      variant="ghost"
                      size="icon"
                      onClick={() => handleOpenEdit(pointUrl)}
                    >
                      <Pencil className="h-4 w-4" />
                    </Button>
                    <Button
                      variant="ghost"
                      size="icon"
                      onClick={() => handleDelete(pointUrl.id)}
                    >
                      <Trash2 className="h-4 w-4 text-destructive" />
                    </Button>
                  </div>
                </TableCell>
              </TableRow>
            ))}
            {(!pointUrls || pointUrls.length === 0) && (
              <TableRow>
                <TableCell colSpan={6} className="text-center text-muted-foreground">
                  No point URLs found
                </TableCell>
              </TableRow>
            )}
          </TableBody>
        </Table>
      </div>

      <Dialog open={isDialogOpen} onOpenChange={setIsDialogOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>{editingPointUrl ? 'Edit Point URL' : 'Add Point URL'}</DialogTitle>
            <DialogDescription>
              {editingPointUrl ? 'Point URL 정보를 수정합니다.' : '새로운 Point URL을 추가합니다.'}
            </DialogDescription>
          </DialogHeader>
          <form onSubmit={handleSubmit(onSubmit)}>
            <div className="space-y-4 py-4">
              <div className="space-y-2">
                <Label htmlFor="url">URL</Label>
                <Input id="url" placeholder="https://..." {...register('url')} />
                {errors.url && (
                  <p className="text-sm text-destructive">{errors.url.message}</p>
                )}
              </div>
              <div className="flex items-center space-x-2">
                <Switch
                  id="permanent"
                  checked={permanentValue}
                  onCheckedChange={(checked) => setValue('permanent', checked)}
                />
                <Label htmlFor="permanent">영구 URL</Label>
              </div>
            </div>
            <DialogFooter>
              <Button type="button" variant="outline" onClick={handleCloseDialog}>
                Cancel
              </Button>
              <Button type="submit" disabled={createMutation.isPending || updateMutation.isPending}>
                {editingPointUrl ? 'Update' : 'Create'}
              </Button>
            </DialogFooter>
          </form>
        </DialogContent>
      </Dialog>

      <AlertDialog open={isDeleteDialogOpen} onOpenChange={setIsDeleteDialogOpen}>
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>정말 삭제하시겠습니까?</AlertDialogTitle>
            <AlertDialogDescription>
              이 작업은 되돌릴 수 없습니다. Point URL이 영구적으로 삭제됩니다.
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
