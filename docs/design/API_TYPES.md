# PickupCoins Admin API TypeScript 타입 정의

> 최종 업데이트: 2026-01-28

## 인증 (Auth)

```typescript
// 로그인 요청
interface LoginRequest {
  loginId: string;
  password: string;
  rememberMe?: boolean;
}

// 로그인 응답
interface LoginResponse {
  accessToken: string;
  refreshToken: string;
  userName: string;
  loginId: string;
}

// 토큰 갱신 요청
interface RefreshTokenRequest {
  refreshToken: string;
}

// 토큰 갱신 응답
interface RefreshTokenResponse {
  accessToken: string;
  refreshToken: string;
}
```

## 대시보드 (Dashboard)

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

## 쿠키 (Cookie)

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

## 포인트 URL (PointUrl)

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

// name 필드는 서버에서 URL 기반으로 자동 생성됨
interface CreatePointUrlRequest {
  url: string;
  permanent?: boolean;
}

interface UpdatePointUrlRequest {
  url?: string;
  permanent?: boolean;
}
```

## 포인트 로그 (SavedPoint)

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
  limit?: number;
  startDate?: string;  // YYYY-MM-DD
  endDate?: string;    // YYYY-MM-DD
}

// 페이지네이션 응답
interface SavedPointListResponse {
  content: SavedPoint[];
  currentPage: number;
  totalItems: number;
  totalPages: number;
}
```

## 사이트 (Site)

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

## 공통 (Common)

```typescript
// 인증 상태
interface AuthState {
  accessToken: string | null;
  refreshToken: string | null;
  userName: string | null;
  loginId: string | null;
  isAuthenticated: boolean;
}

// API 에러
interface ApiError {
  statusCode: number;
  message: string;
  error?: string;
}
```

## API 엔드포인트

Base URL: `http://localhost:8080/api/v1`

### 인증
| 메서드 | 경로 | 설명 |
|--------|------|------|
| POST | `/auth/login` | 로그인 |
| POST | `/auth/refresh` | 토큰 갱신 |

### 대시보드
| 메서드 | 경로 | 설명 |
|--------|------|------|
| GET | `/dashboard/stats` | 통계 조회 |

### 쿠키 관리
| 메서드 | 경로 | 설명 |
|--------|------|------|
| GET | `/cookies` | 쿠키 목록 |
| GET | `/cookies/:id` | 쿠키 상세 |
| POST | `/cookies` | 쿠키 생성 |
| PUT | `/cookies/:id` | 쿠키 수정 |
| DELETE | `/cookies/:id` | 쿠키 삭제 |
| PATCH | `/cookies/:id/toggle-validity` | 유효성 토글 |

### 포인트 URL 관리
| 메서드 | 경로 | 설명 |
|--------|------|------|
| GET | `/point-urls` | URL 목록 |
| GET | `/point-urls/:id` | URL 상세 |
| POST | `/point-urls` | URL 생성 |
| PUT | `/point-urls/:id` | URL 수정 |
| DELETE | `/point-urls/:id` | URL 삭제 |
| PATCH | `/point-urls/:id/toggle-permanent` | 영구 토글 |

### 포인트 로그
| 메서드 | 경로 | 설명 |
|--------|------|------|
| GET | `/point-logs` | 로그 목록 (페이지네이션, 날짜 필터 지원) |
| GET | `/point-logs/:id` | 로그 상세 |
| DELETE | `/point-logs/:id` | 로그 삭제 |

### 사이트 관리
| 메서드 | 경로 | 설명 |
|--------|------|------|
| GET | `/sites` | 사이트 목록 |
| GET | `/sites/:id` | 사이트 상세 |
| POST | `/sites` | 사이트 생성 |
| PUT | `/sites/:id` | 사이트 수정 |
| DELETE | `/sites/:id` | 사이트 삭제 |

### 헬스체크
| 메서드 | 경로 | 설명 |
|--------|------|------|
| GET | `/health` | 서버 상태 확인 |
