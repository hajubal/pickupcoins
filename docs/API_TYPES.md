# PickupCoins Admin API TypeScript 타입 정의

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
  loginId: string;
  userName: string;
}

// 토큰 갱신 응답
interface RefreshTokenResponse {
  accessToken: string;
  refreshToken: string;
}

// JWT Payload
interface JwtPayload {
  sub: bigint;
  loginId: string;
  userName: string;
  type: 'access' | 'refresh';
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

interface CreateCookieDto {
  userName: string;
  siteName: string;
  cookie?: string;
  isValid?: boolean;
}

interface UpdateCookieDto {
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

interface CreatePointUrlDto {
  name: string;
  url: string;
  permanent?: boolean;
}

interface UpdatePointUrlDto {
  name?: string;
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
  startDate?: string;
  endDate?: string;
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

interface CreateSiteDto {
  name: string;
  domain: string;
  url: string;
}

interface UpdateSiteDto {
  name?: string;
  domain?: string;
  url?: string;
}
```

## 공통 (Common)

```typescript
interface PaginationMeta {
  total: number;
  page: number;
  limit: number;
  totalPages: number;
  hasNextPage: boolean;
  hasPrevPage: boolean;
}

interface PaginatedResult<T> {
  data: T[];
  meta: PaginationMeta;
}

interface ApiError {
  statusCode: number;
  message: string;
  error?: string;
}
```

## API 엔드포인트

| 메서드 | 경로 | 설명 |
|--------|------|------|
| POST | `/auth/login` | 로그인 |
| POST | `/auth/refresh` | 토큰 갱신 |
| GET | `/admin/dashboard/stats` | 통계 조회 |
| GET | `/admin/cookies` | 쿠키 목록 |
| GET | `/admin/cookies/:id` | 쿠키 상세 |
| POST | `/admin/cookies` | 쿠키 생성 |
| PUT | `/admin/cookies/:id` | 쿠키 수정 |
| DELETE | `/admin/cookies/:id` | 쿠키 삭제 |
| PATCH | `/admin/cookies/:id/toggle-validity` | 유효성 토글 |
| GET | `/admin/point-urls` | URL 목록 |
| GET | `/admin/point-urls/:id` | URL 상세 |
| POST | `/admin/point-urls` | URL 생성 |
| PUT | `/admin/point-urls/:id` | URL 수정 |
| DELETE | `/admin/point-urls/:id` | URL 삭제 |
| PATCH | `/admin/point-urls/:id/toggle-permanent` | 영구 토글 |
| GET | `/admin/point-logs` | 로그 목록 |
| GET | `/admin/point-logs/:id` | 로그 상세 |
| DELETE | `/admin/point-logs/:id` | 로그 삭제 |
| GET | `/admin/sites` | 사이트 목록 |
| GET | `/admin/sites/:id` | 사이트 상세 |
| POST | `/admin/sites` | 사이트 생성 |
| PUT | `/admin/sites/:id` | 사이트 수정 |
| DELETE | `/admin/sites/:id` | 사이트 삭제 |
