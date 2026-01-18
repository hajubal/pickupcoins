# PickupCoins Admin 컴포넌트 명세

## UI 컴포넌트 (shadcn/ui)

### 필수 컴포넌트
- `Button` - 버튼
- `Input` - 입력 필드
- `Label` - 라벨
- `Card` - 카드 레이아웃
- `Table` - 데이터 테이블
- `Dialog` - 모달 다이얼로그
- `DropdownMenu` - 드롭다운 메뉴
- `Badge` - 상태 뱃지
- `Checkbox` - 체크박스
- `Toast` / `Sonner` - 알림

## 공통 컴포넌트

### LoadingSpinner
```typescript
interface LoadingSpinnerProps {
  size?: 'sm' | 'md' | 'lg';
  className?: string;
}
```

### ConfirmDialog
```typescript
interface ConfirmDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  title: string;
  description: string;
  onConfirm: () => void;
  confirmText?: string;
  cancelText?: string;
  variant?: 'default' | 'destructive';
}
```

### StatusBadge
```typescript
interface StatusBadgeProps {
  status: boolean;
  trueLabel?: string;
  falseLabel?: string;
}
```

## 데이터 테이블 컴포넌트

### DataTable
```typescript
interface DataTableProps<T> {
  columns: ColumnDef<T>[];
  data: T[];
  loading?: boolean;
}
```

### Pagination
```typescript
interface PaginationProps {
  currentPage: number;
  totalPages: number;
  onPageChange: (page: number) => void;
  hasNextPage: boolean;
  hasPrevPage: boolean;
}
```

## 폼 컴포넌트

### CookieForm
```typescript
interface CookieFormProps {
  defaultValues?: Partial<Cookie>;
  onSubmit: (data: CreateCookieDto | UpdateCookieDto) => void;
  isLoading?: boolean;
}
```

### PointUrlForm
```typescript
interface PointUrlFormProps {
  defaultValues?: Partial<PointUrl>;
  onSubmit: (data: CreatePointUrlDto | UpdatePointUrlDto) => void;
  isLoading?: boolean;
}
```

### SiteForm
```typescript
interface SiteFormProps {
  defaultValues?: Partial<Site>;
  onSubmit: (data: CreateSiteDto | UpdateSiteDto) => void;
  isLoading?: boolean;
}
```

## 레이아웃 컴포넌트

### AdminLayout
- 사이드바 + 헤더 + 메인 컨텐츠 영역
- 반응형 (모바일에서 사이드바 토글)

### Sidebar
- 네비게이션 메뉴
- 현재 경로 하이라이트
- 로고 영역

### Header
- 사용자 정보 표시
- 로그아웃 버튼

## 기능별 컴포넌트

### Dashboard
- `StatsCard` - 통계 카드 (값, 비율, 아이콘)

### Cookies
- `CookieDialog` - 쿠키 생성/수정 다이얼로그
- `CookieTable` - 쿠키 목록 테이블

### PointUrls
- `PointUrlDialog` - URL 생성/수정 다이얼로그
- `PointUrlTable` - URL 목록 테이블

### PointLogs
- `DateFilter` - 날짜 범위 필터
- `PointLogTable` - 로그 목록 테이블

### Sites
- `SiteDialog` - 사이트 생성/수정 다이얼로그
- `SiteTable` - 사이트 목록 테이블

## 커스텀 훅

### useAuth
```typescript
function useAuth(): {
  user: User | null;
  login: (credentials: LoginRequest) => Promise<void>;
  logout: () => void;
  isAuthenticated: boolean;
}
```

### useCookies
```typescript
function useCookies(): {
  cookies: Cookie[];
  isLoading: boolean;
  createCookie: UseMutationResult;
  updateCookie: UseMutationResult;
  deleteCookie: UseMutationResult;
  toggleValidity: UseMutationResult;
}
```

### usePointUrls
```typescript
function usePointUrls(): {
  pointUrls: PointUrl[];
  isLoading: boolean;
  createPointUrl: UseMutationResult;
  updatePointUrl: UseMutationResult;
  deletePointUrl: UseMutationResult;
  togglePermanent: UseMutationResult;
}
```

### useSites
```typescript
function useSites(): {
  sites: Site[];
  isLoading: boolean;
  createSite: UseMutationResult;
  updateSite: UseMutationResult;
  deleteSite: UseMutationResult;
}
```

### usePointLogs
```typescript
function usePointLogs(query: SavedPointQuery): {
  pointLogs: PaginatedResult<SavedPoint>;
  isLoading: boolean;
  deletePointLog: UseMutationResult;
}
```

### useDashboardStats
```typescript
function useDashboardStats(): {
  stats: DashboardStats | null;
  isLoading: boolean;
  refetch: () => void;
}
```
