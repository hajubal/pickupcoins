import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { cookiesApi } from '@/api/cookies';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { Cookie, CreateCookieRequest, UpdateCookieRequest } from '@/types';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Textarea } from '@/components/ui/textarea';
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
import { Plus, Pencil, Trash2 } from 'lucide-react';

const cookieSchema = z.object({
  userName: z.string().min(1, '사용자 이름을 입력해주세요'),
  siteName: z.string().min(1, '사이트 이름을 입력해주세요'),
  cookie: z.string().optional(),
  isValid: z.boolean().optional(),
});

type CookieFormData = z.infer<typeof cookieSchema>;

export function CookiesPage() {
  const queryClient = useQueryClient();
  const { toast } = useToast();
  const [isDialogOpen, setIsDialogOpen] = useState(false);
  const [isDeleteDialogOpen, setIsDeleteDialogOpen] = useState(false);
  const [editingCookie, setEditingCookie] = useState<Cookie | null>(null);
  const [deletingCookieId, setDeletingCookieId] = useState<string | null>(null);

  const { data: cookies, isLoading } = useQuery({
    queryKey: ['cookies'],
    queryFn: cookiesApi.findAll,
  });

  const createMutation = useMutation({
    mutationFn: (data: CreateCookieRequest) => cookiesApi.create(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['cookies'] });
      toast({ title: '쿠키가 생성되었습니다.' });
      handleCloseDialog();
    },
    onError: () => {
      toast({ variant: 'destructive', title: '쿠키 생성에 실패했습니다.' });
    },
  });

  const updateMutation = useMutation({
    mutationFn: ({ id, data }: { id: string; data: UpdateCookieRequest }) =>
      cookiesApi.update(id, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['cookies'] });
      toast({ title: '쿠키가 수정되었습니다.' });
      handleCloseDialog();
    },
    onError: () => {
      toast({ variant: 'destructive', title: '쿠키 수정에 실패했습니다.' });
    },
  });

  const deleteMutation = useMutation({
    mutationFn: (id: string) => cookiesApi.delete(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['cookies'] });
      toast({ title: '쿠키가 삭제되었습니다.' });
    },
    onError: () => {
      toast({ variant: 'destructive', title: '쿠키 삭제에 실패했습니다.' });
    },
  });

  const toggleValidityMutation = useMutation({
    mutationFn: (id: string) => cookiesApi.toggleValidity(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['cookies'] });
    },
    onError: () => {
      toast({ variant: 'destructive', title: '유효성 변경에 실패했습니다.' });
    },
  });

  const {
    register,
    handleSubmit,
    reset,
    setValue,
    watch,
    formState: { errors },
  } = useForm<CookieFormData>({
    resolver: zodResolver(cookieSchema),
    defaultValues: {
      isValid: true,
    },
  });

  const handleOpenCreate = () => {
    setEditingCookie(null);
    reset({ userName: '', siteName: '', cookie: '', isValid: true });
    setIsDialogOpen(true);
  };

  const handleOpenEdit = (cookie: Cookie) => {
    setEditingCookie(cookie);
    reset({
      userName: cookie.userName,
      siteName: cookie.siteName,
      cookie: cookie.cookie || '',
      isValid: cookie.isValid,
    });
    setIsDialogOpen(true);
  };

  const handleCloseDialog = () => {
    setIsDialogOpen(false);
    setEditingCookie(null);
    reset();
  };

  const handleDelete = (id: string) => {
    setDeletingCookieId(id);
    setIsDeleteDialogOpen(true);
  };

  const confirmDelete = () => {
    if (deletingCookieId) {
      deleteMutation.mutate(deletingCookieId);
    }
    setIsDeleteDialogOpen(false);
    setDeletingCookieId(null);
  };

  const onSubmit = (data: CookieFormData) => {
    if (editingCookie) {
      updateMutation.mutate({ id: editingCookie.id, data });
    } else {
      createMutation.mutate(data);
    }
  };

  const isValidValue = watch('isValid');

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
        <h2 className="text-3xl font-bold tracking-tight">Cookies</h2>
        <Button onClick={handleOpenCreate}>
          <Plus className="h-4 w-4 mr-2" />
          Add Cookie
        </Button>
      </div>

      <div className="rounded-md border">
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead>사용자</TableHead>
              <TableHead>사이트</TableHead>
              <TableHead>유효성</TableHead>
              <TableHead>생성일</TableHead>
              <TableHead>수정일</TableHead>
              <TableHead className="w-[100px]">Actions</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {cookies?.map((cookie) => (
              <TableRow key={cookie.id}>
                <TableCell className="font-medium">{cookie.userName}</TableCell>
                <TableCell>{cookie.siteName}</TableCell>
                <TableCell>
                  <Switch
                    checked={cookie.isValid}
                    onCheckedChange={() => toggleValidityMutation.mutate(cookie.id)}
                  />
                </TableCell>
                <TableCell>{formatDate(cookie.createdDate)}</TableCell>
                <TableCell>{formatDate(cookie.modifiedDate)}</TableCell>
                <TableCell>
                  <div className="flex gap-2">
                    <Button
                      variant="ghost"
                      size="icon"
                      onClick={() => handleOpenEdit(cookie)}
                    >
                      <Pencil className="h-4 w-4" />
                    </Button>
                    <Button
                      variant="ghost"
                      size="icon"
                      onClick={() => handleDelete(cookie.id)}
                    >
                      <Trash2 className="h-4 w-4 text-destructive" />
                    </Button>
                  </div>
                </TableCell>
              </TableRow>
            ))}
            {(!cookies || cookies.length === 0) && (
              <TableRow>
                <TableCell colSpan={6} className="text-center text-muted-foreground">
                  No cookies found
                </TableCell>
              </TableRow>
            )}
          </TableBody>
        </Table>
      </div>

      <Dialog open={isDialogOpen} onOpenChange={setIsDialogOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>{editingCookie ? 'Edit Cookie' : 'Add Cookie'}</DialogTitle>
            <DialogDescription>
              {editingCookie ? '쿠키 정보를 수정합니다.' : '새로운 쿠키를 추가합니다.'}
            </DialogDescription>
          </DialogHeader>
          <form onSubmit={handleSubmit(onSubmit)}>
            <div className="space-y-4 py-4">
              <div className="space-y-2">
                <Label htmlFor="userName">사용자 이름</Label>
                <Input id="userName" {...register('userName')} />
                {errors.userName && (
                  <p className="text-sm text-destructive">{errors.userName.message}</p>
                )}
              </div>
              <div className="space-y-2">
                <Label htmlFor="siteName">사이트 이름</Label>
                <Input id="siteName" {...register('siteName')} />
                {errors.siteName && (
                  <p className="text-sm text-destructive">{errors.siteName.message}</p>
                )}
              </div>
              <div className="space-y-2">
                <Label htmlFor="cookie">쿠키 값</Label>
                <Textarea id="cookie" rows={4} {...register('cookie')} />
              </div>
              <div className="flex items-center space-x-2">
                <Switch
                  id="isValid"
                  checked={isValidValue}
                  onCheckedChange={(checked) => setValue('isValid', checked)}
                />
                <Label htmlFor="isValid">유효함</Label>
                <Badge variant={isValidValue ? 'success' : 'secondary'}>
                  {isValidValue ? 'Valid' : 'Invalid'}
                </Badge>
              </div>
            </div>
            <DialogFooter>
              <Button type="button" variant="outline" onClick={handleCloseDialog}>
                Cancel
              </Button>
              <Button type="submit" disabled={createMutation.isPending || updateMutation.isPending}>
                {editingCookie ? 'Update' : 'Create'}
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
              이 작업은 되돌릴 수 없습니다. 쿠키가 영구적으로 삭제됩니다.
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
