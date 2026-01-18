# PickupCoins Admin Frontend 구현 완료 문서

## 개요
PickupCoins 백엔드 Admin API를 소비하는 React 기반 관리자 프론트엔드 애플리케이션

**프로젝트 위치**: `C:\Users\hajub\pickupcoins-admin`
**생성일**: 2026-01-18

---

## 기술 스택

| 기술 | 버전 | 용도 |
|------|------|------|
| React | 18.x | UI 프레임워크 |
| TypeScript | 5.x | 타입 안전성 |
| Vite | 5.x | 빌드 도구 |
| TanStack Query | 5.x | 서버 상태 관리 |
| React Router | 6.x | 라우팅 |
| Axios | 1.x | HTTP 클라이언트 |
| Tailwind CSS | 3.x | 스타일링 |
| shadcn/ui | Latest | UI 컴포넌트 |
| React Hook Form | Latest | 폼 관리 |
| Zod | Latest | 폼 검증 |
| Lucide React | Latest | 아이콘 |
| date-fns | Latest | 날짜 처리 |
| Sonner | Latest | 토스트 알림 |

---

## 구현 완료 기능

### 1. 인증 시스템
- **로그인 페이지** (`/login`)
  - 아이디/비밀번호 입력
  - Remember Me 체크박스
  - 폼 유효성 검사 (Zod)
  - 에러 메시지 표시

- **토큰 관리**
  - JWT Access Token / Refresh Token 저장 (localStorage)
  - 401 응답 시 자동 토큰 갱신
  - 갱신 실패 시 로그인 페이지 리다이렉트

- **라우트 보호**
  - `ProtectedRoute`: 인증 필요 페이지 보호
  - `PublicRoute`: 인증된 사용자 대시보드로 리다이렉트

### 2. 레이아웃
- **AdminLayout**
  - 사이드바 + 헤더 + 메인 컨텐츠 영역
  - 반응형 디자인 (모바일 사이드바 토글)

- **Sidebar**
  - 네비게이션 메뉴 (대시보드, 쿠키, 포인트 URL, 포인트 로그, 사이트)
  - 현재 페이지 하이라이트
  - 로고 영역

- **Header**
  - 모바일 메뉴 버튼
  - 사용자 정보 드롭다운
  - 로그아웃 기능

### 3. 대시보드 (`/dashboard`)
- 오늘 적립 포인트 (전일 대비 비율)
- 이번 주 적립 포인트 (전주 대비 비율)
- 수집된 URL 수 (오늘/이번 주)
- 1분마다 자동 새로고침

### 4. 쿠키 관리 (`/cookies`)
- 쿠키 목록 테이블
- 새 쿠키 등록 (다이얼로그)
- 쿠키 수정
- 쿠키 삭제 (확인 다이얼로그)
- 유효성 토글 (PATCH)

### 5. 포인트 URL 관리 (`/point-urls`)
- URL 목록 테이블
- 새 URL 등록
- URL 수정
- URL 삭제
- 영구 플래그 토글

### 6. 사이트 관리 (`/sites`)
- 사이트 목록 테이블
- 새 사이트 등록
- 사이트 수정
- 사이트 삭제

### 7. 포인트 로그 (`/point-logs`)
- 날짜 범위 필터 (시작일 ~ 종료일)
- 프리셋 버튼 (최근 7일, 최근 30일)
- 페이지네이션 (10개씩)
- 로그 삭제

---

## 디렉토리 구조

```
pickupcoins-admin/
├── public/
├── src/
│   ├── api/                    # API 레이어
│   │   ├── client.ts           # Axios 인스턴스 + 인터셉터
│   │   ├── auth.api.ts
│   │   ├── dashboard.api.ts
│   │   ├── cookies.api.ts
│   │   ├── point-urls.api.ts
│   │   ├── point-logs.api.ts
│   │   ├── sites.api.ts
│   │   └── index.ts
│   │
│   ├── components/
│   │   ├── ui/                 # shadcn/ui 컴포넌트
│   │   │   ├── button.tsx
│   │   │   ├── input.tsx
│   │   │   ├── label.tsx
│   │   │   ├── card.tsx
│   │   │   ├── checkbox.tsx
│   │   │   ├── dialog.tsx
│   │   │   ├── table.tsx
│   │   │   ├── badge.tsx
│   │   │   ├── dropdown-menu.tsx
│   │   │   ├── separator.tsx
│   │   │   └── sonner.tsx
│   │   ├── forms/
│   │   │   ├── CookieForm.tsx
│   │   │   ├── PointUrlForm.tsx
│   │   │   ├── SiteForm.tsx
│   │   │   └── index.ts
│   │   ├── data-table/
│   │   │   ├── Pagination.tsx
│   │   │   └── index.ts
│   │   └── common/
│   │       ├── LoadingSpinner.tsx
│   │       ├── ConfirmDialog.tsx
│   │       ├── StatusBadge.tsx
│   │       ├── PageHeader.tsx
│   │       ├── EmptyState.tsx
│   │       └── index.ts
│   │
│   ├── features/               # 기능별 모듈
│   │   ├── auth/
│   │   │   ├── AuthContext.tsx
│   │   │   ├── LoginPage.tsx
│   │   │   ├── useAuth.ts
│   │   │   └── index.ts
│   │   ├── dashboard/
│   │   │   ├── DashboardPage.tsx
│   │   │   ├── StatsCard.tsx
│   │   │   ├── useDashboardStats.ts
│   │   │   └── index.ts
│   │   ├── cookies/
│   │   │   ├── CookiesPage.tsx
│   │   │   ├── CookieDialog.tsx
│   │   │   ├── useCookies.ts
│   │   │   └── index.ts
│   │   ├── point-urls/
│   │   │   ├── PointUrlsPage.tsx
│   │   │   ├── PointUrlDialog.tsx
│   │   │   ├── usePointUrls.ts
│   │   │   └── index.ts
│   │   ├── point-logs/
│   │   │   ├── PointLogsPage.tsx
│   │   │   ├── DateFilter.tsx
│   │   │   ├── usePointLogs.ts
│   │   │   └── index.ts
│   │   └── sites/
│   │       ├── SitesPage.tsx
│   │       ├── SiteDialog.tsx
│   │       ├── useSites.ts
│   │       └── index.ts
│   │
│   ├── layouts/
│   │   ├── AdminLayout.tsx
│   │   ├── AuthLayout.tsx
│   │   ├── Sidebar.tsx
│   │   ├── Header.tsx
│   │   └── index.ts
│   │
│   ├── routes/
│   │   ├── index.tsx
│   │   ├── ProtectedRoute.tsx
│   │   └── PublicRoute.tsx
│   │
│   ├── stores/
│   │   └── auth.store.ts
│   │
│   ├── types/
│   │   ├── auth.types.ts
│   │   ├── cookie.types.ts
│   │   ├── dashboard.types.ts
│   │   ├── point-url.types.ts
│   │   ├── point-log.types.ts
│   │   ├── site.types.ts
│   │   ├── common.types.ts
│   │   └── index.ts
│   │
│   ├── lib/
│   │   ├── utils.ts
│   │   └── constants.ts
│   │
│   ├── App.tsx
│   ├── main.tsx
│   └── index.css
│
├── .env
├── .env.example
├── index.html
├── package.json
├── postcss.config.js
├── tailwind.config.js
├── tsconfig.json
├── tsconfig.app.json
├── tsconfig.node.json
└── vite.config.ts
```

---

## 실행 방법

### 개발 서버 실행
```bash
cd C:\Users\hajub\pickupcoins-admin
npm run dev
```
- URL: http://localhost:5173

### 프로덕션 빌드
```bash
npm run build
```
- 출력 디렉토리: `dist/`

### 프리뷰 (빌드된 결과 확인)
```bash
npm run preview
```

---

## 환경 설정

### .env 파일
```env
VITE_API_BASE_URL=http://localhost:8080/api/v1
```

---

## 테스트 계정

| 항목 | 값 |
|------|-----|
| 로그인 ID | admin |
| 비밀번호 | test123 |

---

## API 연동 정보

### Base URL
- 개발: `http://localhost:8080/api/v1`
- Swagger: `http://localhost:8080/api-docs`

### 인증
- JWT Bearer Token
- Authorization 헤더에 `Bearer {token}` 형식

### 토큰 유효기간
| 토큰 | 유효기간 |
|------|---------|
| Access Token | 15분 |
| Refresh Token | 7일 |
| Remember Me | 15일 |

---

## 주요 의존성 패키지

```json
{
  "dependencies": {
    "@hookform/resolvers": "^3.x",
    "@radix-ui/react-checkbox": "^1.x",
    "@radix-ui/react-dialog": "^1.x",
    "@radix-ui/react-dropdown-menu": "^2.x",
    "@radix-ui/react-label": "^2.x",
    "@radix-ui/react-separator": "^1.x",
    "@radix-ui/react-slot": "^1.x",
    "@tanstack/react-query": "^5.x",
    "axios": "^1.x",
    "class-variance-authority": "^0.x",
    "clsx": "^2.x",
    "date-fns": "^3.x",
    "lucide-react": "^0.x",
    "react": "^18.x",
    "react-dom": "^18.x",
    "react-hook-form": "^7.x",
    "react-router-dom": "^6.x",
    "sonner": "^1.x",
    "tailwind-merge": "^2.x",
    "zod": "^3.x"
  },
  "devDependencies": {
    "@types/node": "^22.x",
    "@vitejs/plugin-react": "^4.x",
    "autoprefixer": "^10.x",
    "postcss": "^8.x",
    "tailwindcss": "^3.x",
    "tailwindcss-animate": "^1.x",
    "typescript": "^5.x",
    "vite": "^7.x"
  }
}
```

---

## 향후 개선 사항

1. **코드 스플리팅**: 큰 번들 사이즈 최적화를 위한 동적 import 적용
2. **다크 모드**: 테마 전환 기능 추가
3. **테스트**: Jest/Vitest를 이용한 단위/통합 테스트
4. **에러 바운더리**: React Error Boundary 적용
5. **PWA**: 오프라인 지원 및 설치 가능한 앱
6. **국제화**: i18n 다국어 지원

---

## 참고 문서

- [FRONTEND_DESIGN.md](./FRONTEND_DESIGN.md) - 전체 설계문서
- [API_TYPES.md](./API_TYPES.md) - TypeScript 타입 정의
- [COMPONENT_SPEC.md](./COMPONENT_SPEC.md) - 컴포넌트 명세
