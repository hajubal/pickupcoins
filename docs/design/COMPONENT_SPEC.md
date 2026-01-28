# PickupCoins Admin 컴포넌트 명세

> 최종 업데이트: 2026-01-28

## UI 컴포넌트 (Radix UI 기반)

### 기본 컴포넌트
| 컴포넌트 | 파일 | 설명 |
|----------|------|------|
| `Button` | `button.tsx` | 버튼 (variant: default, destructive, outline, secondary, ghost, link) |
| `Input` | `input.tsx` | 입력 필드 |
| `Textarea` | `textarea.tsx` | 여러 줄 입력 필드 |
| `Label` | `label.tsx` | 라벨 |
| `Card` | `card.tsx` | 카드 레이아웃 (Header, Title, Description, Content, Footer) |
| `Badge` | `badge.tsx` | 상태 뱃지 (variant: default, secondary, destructive, outline) |
| `Separator` | `separator.tsx` | 구분선 |

### 인터랙션 컴포넌트
| 컴포넌트 | 파일 | 설명 |
|----------|------|------|
| `Dialog` | `dialog.tsx` | 모달 다이얼로그 |
| `AlertDialog` | `alert-dialog.tsx` | 확인 다이얼로그 (삭제 등) |
| `DropdownMenu` | `dropdown-menu.tsx` | 드롭다운 메뉴 |
| `Select` | `select.tsx` | 선택 컴포넌트 |
| `Switch` | `switch.tsx` | 토글 스위치 |

### 데이터 표시 컴포넌트
| 컴포넌트 | 파일 | 설명 |
|----------|------|------|
| `Table` | `table.tsx` | 데이터 테이블 (Header, Body, Row, Head, Cell 등) |
| `Toast` | `toast.tsx` | 토스트 알림 |
| `Toaster` | `toaster.tsx` | 토스트 컨테이너 |

## 타입 정의

### Auth Types
```typescript
interface LoginRequest {
  loginId: string;
  password: string;
  rememberMe?: boolean;
}

interface LoginResponse {
  accessToken: string;
  refreshToken: string;
  userName: string;
  loginId: string;
}

interface AuthState {
  accessToken: string | null;
  refreshToken: string | null;
  userName: string | null;
  loginId: string | null;
  isAuthenticated: boolean;
}
```

### Cookie Types
```typescript
interface Cookie {
  id: string;
  userName: string;
  siteName: string;
  cookie: string | null;
  isValid: boolean;
  createdDate: string;
  modifiedDate: string;
}

interface CreateCookieRequest {
  userName: string;
  siteName: string;
  cookie?: string;
  isValid?: boolean;
}

interface UpdateCookieRequest {
  userName?: string;
  siteName?: string;
  cookie?: string;
  isValid?: boolean;
}
```

### PointUrl Types
```typescript
type PointUrlType = 'NAVER' | 'OFW_NAVER' | 'UNSUPPORT';

interface PointUrl {
  id: string;
  name: string;
  url: string;
  pointUrlType: PointUrlType | null;
  permanent: boolean;
  createdDate: string;
  modifiedDate: string;
}

interface CreatePointUrlRequest {
  url: string;
  permanent?: boolean;
}

interface UpdatePointUrlRequest {
  url?: string;
  permanent?: boolean;
}
```

### SavedPoint Types
```typescript
interface SavedPoint {
  id: string;
  cookieId: string;
  amount: number;
  responseBody: string | null;
  createdDate: string;
  modifiedDate: string;
  userName?: string;
  siteName?: string;
}

interface SavedPointQuery {
  page?: number;
  size?: number;
  startDate?: string;
  endDate?: string;
}

interface SavedPointListResponse {
  content: SavedPoint[];
  currentPage: number;
  totalItems: number;
  totalPages: number;
}
```

### Site Types
```typescript
interface Site {
  id: string;
  name: string;
  domain: string;
  url: string;
  createdDate: string;
  modifiedDate: string;
}

interface CreateSiteRequest {
  name: string;
  domain: string;
  url: string;
}

interface UpdateSiteRequest {
  name?: string;
  domain?: string;
  url?: string;
}
```

### Dashboard Types
```typescript
interface DashboardStats {
  savedDayPoint: number;
  savedDayPointRatioDayBefore: number;
  savedWeekPoint: number;
  savedWeekPointRatioWeekBefore: number;
  pointUrlDayCnt: number;
  pointUrlWeekCnt: number;
}
```

## 레이아웃 컴포넌트

### AdminLayout
- Outlet 기반 중첩 라우팅
- 사이드바 + 헤더 + 메인 컨텐츠 영역
- 반응형 (모바일에서 사이드바 토글)

### Sidebar
- 네비게이션 메뉴
  - 대시보드 (`/dashboard`)
  - 쿠키 관리 (`/cookies`)
  - 포인트 URL (`/point-urls`)
  - 포인트 로그 (`/point-logs`)
  - 사이트 관리 (`/sites`)
- 현재 경로 하이라이트
- 로고 영역

### Header
- 사용자 정보 표시
- 드롭다운 메뉴 (로그아웃)

## Feature 페이지 컴포넌트

### LoginPage (`/login`)
- 로그인 폼 (React Hook Form + Zod)
- 토큰 저장 (localStorage)
- 인증 후 대시보드로 리다이렉트

### DashboardPage (`/dashboard`)
- 통계 카드 (오늘/이번주 포인트, URL 수)
- 전일/전주 대비 비율 표시

### CookiesPage (`/cookies`)
- TanStack Table 기반 목록
- Dialog를 통한 생성/수정
- AlertDialog를 통한 삭제 확인
- Switch를 통한 유효성 토글

### PointUrlsPage (`/point-urls`)
- TanStack Table 기반 목록
- Dialog를 통한 생성/수정
- AlertDialog를 통한 삭제 확인
- Switch를 통한 영구 플래그 토글

### PointLogsPage (`/point-logs`)
- 날짜 필터 (시작일, 종료일)
- TanStack Table 기반 목록
- 페이지네이션
- AlertDialog를 통한 삭제 확인

### SitesPage (`/sites`)
- TanStack Table 기반 목록
- Dialog를 통한 생성/수정
- AlertDialog를 통한 삭제 확인

## API 레이어

### client.ts
- Axios 인스턴스 생성
- Base URL 설정
- 요청 인터셉터 (Authorization 헤더 추가)
- 응답 인터셉터 (401 에러 시 토큰 갱신)

### API 모듈
| 파일 | 기능 |
|------|------|
| `auth.ts` | 로그인, 토큰 갱신 |
| `dashboard.ts` | 대시보드 통계 조회 |
| `cookies.ts` | 쿠키 CRUD, 유효성 토글 |
| `point-urls.ts` | 포인트 URL CRUD, 영구 토글 |
| `point-logs.ts` | 포인트 로그 조회, 삭제 |
| `sites.ts` | 사이트 CRUD |

## 유틸리티

### lib/utils.ts
- `cn()`: clsx + tailwind-merge 조합
- className 병합 유틸리티

### hooks/use-toast.ts
- Toast 상태 관리
- `toast()` 함수 제공
