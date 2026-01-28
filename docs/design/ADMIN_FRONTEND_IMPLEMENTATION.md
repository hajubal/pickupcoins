# PickupCoins Admin Frontend 구현 완료 문서

> 최종 업데이트: 2026-01-28

## 개요
PickupCoins 백엔드 Admin API를 소비하는 React 기반 관리자 프론트엔드 애플리케이션

**프로젝트 위치**: `admin/` (모노레포 내 프론트엔드 폴더)

---

## 기술 스택

| 기술 | 버전 | 용도 |
|------|------|------|
| React | 18.3.x | UI 프레임워크 |
| TypeScript | 5.6.x | 타입 안전성 |
| Vite | 5.4.x | 빌드 도구 |
| TanStack Query | 5.59.x | 서버 상태 관리 |
| TanStack Table | 8.20.x | 테이블 컴포넌트 |
| React Router | 6.27.x | 라우팅 |
| Axios | 1.7.x | HTTP 클라이언트 |
| Tailwind CSS | 3.4.x | 스타일링 |
| Radix UI | Latest | UI 프리미티브 (Dialog, Switch, Select 등) |
| React Hook Form | 7.53.x | 폼 관리 |
| Zod | 3.23.x | 스키마 검증 |
| Lucide React | 0.453.x | 아이콘 |

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

### 2. 레이아웃
- **AdminLayout**
  - 사이드바 + 헤더 + 메인 컨텐츠 영역
  - 반응형 디자인 (모바일 사이드바 토글)
  - React Router Outlet 기반 중첩 라우팅

- **Sidebar**
  - 네비게이션 메뉴 (대시보드, 쿠키, 포인트 URL, 포인트 로그, 사이트)
  - 현재 페이지 하이라이트
  - 로고 영역

- **Header**
  - 사용자 정보 드롭다운
  - 로그아웃 기능

### 3. 대시보드 (`/dashboard`)
- 오늘 적립 포인트 (전일 대비 비율)
- 이번 주 적립 포인트 (전주 대비 비율)
- 수집된 URL 수 (오늘/이번 주)

### 4. 쿠키 관리 (`/cookies`)
- 쿠키 목록 테이블 (TanStack Table)
- 새 쿠키 등록 (Dialog)
- 쿠키 수정
- 쿠키 삭제 (AlertDialog 확인)
- 유효성 토글 (Switch + PATCH)

### 5. 포인트 URL 관리 (`/point-urls`)
- URL 목록 테이블 (TanStack Table)
- 새 URL 등록
- URL 수정
- URL 삭제 (AlertDialog 확인)
- 영구 플래그 토글 (Switch + PATCH)

### 6. 사이트 관리 (`/sites`)
- 사이트 목록 테이블 (TanStack Table)
- 새 사이트 등록
- 사이트 수정
- 사이트 삭제 (AlertDialog 확인)

### 7. 포인트 로그 (`/point-logs`)
- 날짜 범위 필터 (시작일 ~ 종료일)
- 페이지네이션
- 로그 삭제 (AlertDialog 확인)

---

## 디렉토리 구조

```
admin/
├── public/
│   └── vite.svg
├── src/
│   ├── api/                    # API 레이어
│   │   ├── client.ts           # Axios 인스턴스 + 인터셉터
│   │   ├── auth.ts
│   │   ├── dashboard.ts
│   │   ├── cookies.ts
│   │   ├── point-urls.ts
│   │   ├── point-logs.ts
│   │   ├── sites.ts
│   │   └── index.ts
│   │
│   ├── components/
│   │   └── ui/                 # Radix UI 기반 컴포넌트
│   │       ├── alert-dialog.tsx
│   │       ├── badge.tsx
│   │       ├── button.tsx
│   │       ├── card.tsx
│   │       ├── dialog.tsx
│   │       ├── dropdown-menu.tsx
│   │       ├── input.tsx
│   │       ├── label.tsx
│   │       ├── select.tsx
│   │       ├── separator.tsx
│   │       ├── switch.tsx
│   │       ├── table.tsx
│   │       ├── textarea.tsx
│   │       ├── toast.tsx
│   │       ├── toaster.tsx
│   │       └── use-toast.ts
│   │
│   ├── features/               # 기능별 모듈
│   │   ├── auth/
│   │   │   └── LoginPage.tsx
│   │   ├── dashboard/
│   │   │   └── DashboardPage.tsx
│   │   ├── cookies/
│   │   │   └── CookiesPage.tsx
│   │   ├── point-urls/
│   │   │   └── PointUrlsPage.tsx
│   │   ├── point-logs/
│   │   │   └── PointLogsPage.tsx
│   │   └── sites/
│   │       └── SitesPage.tsx
│   │
│   ├── layouts/
│   │   ├── AdminLayout.tsx
│   │   ├── Header.tsx
│   │   └── Sidebar.tsx
│   │
│   ├── routes/
│   │   └── index.tsx
│   │
│   ├── types/
│   │   └── index.ts
│   │
│   ├── lib/
│   │   └── utils.ts
│   │
│   ├── main.tsx
│   ├── index.css
│   └── vite-env.d.ts
│
├── .gitignore
├── Dockerfile
├── nginx.conf
├── index.html
├── package.json
├── postcss.config.js
├── tailwind.config.js
├── tsconfig.json
├── tsconfig.node.json
└── vite.config.ts
```

---

## 실행 방법

### 개발 서버 실행
```bash
cd admin
npm install
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

### Docker 실행
```bash
# 루트 디렉토리에서
docker-compose up admin
```

---

## 환경 설정

### 환경변수
개발 시 `vite.config.ts`에서 프록시 설정 또는 직접 API URL 지정

```typescript
// vite.config.ts
export default defineConfig({
  server: {
    proxy: {
      '/api': 'http://localhost:8080'
    }
  }
})
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
    "@hookform/resolvers": "^3.9.0",
    "@radix-ui/react-dialog": "^1.1.2",
    "@radix-ui/react-dropdown-menu": "^2.1.2",
    "@radix-ui/react-label": "^2.1.0",
    "@radix-ui/react-select": "^2.1.2",
    "@radix-ui/react-separator": "^1.1.0",
    "@radix-ui/react-slot": "^1.1.0",
    "@radix-ui/react-switch": "^1.1.1",
    "@radix-ui/react-toast": "^1.2.2",
    "@tanstack/react-query": "^5.59.0",
    "@tanstack/react-table": "^8.20.5",
    "axios": "^1.7.7",
    "class-variance-authority": "^0.7.0",
    "clsx": "^2.1.1",
    "lucide-react": "^0.453.0",
    "react": "^18.3.1",
    "react-dom": "^18.3.1",
    "react-hook-form": "^7.53.0",
    "react-router-dom": "^6.27.0",
    "tailwind-merge": "^2.5.4",
    "tailwindcss-animate": "^1.0.7",
    "zod": "^3.23.8"
  },
  "devDependencies": {
    "@types/node": "^22.7.9",
    "@types/react": "^18.3.12",
    "@types/react-dom": "^18.3.1",
    "@vitejs/plugin-react": "^4.3.3",
    "autoprefixer": "^10.4.20",
    "postcss": "^8.4.47",
    "tailwindcss": "^3.4.14",
    "typescript": "~5.6.2",
    "vite": "^5.4.10"
  }
}
```

---

## E2E 테스트

### 테스트 프레임워크
- **Playwright** v1.58.x

### 테스트 파일 위치
프로젝트 루트의 `e2e/` 디렉토리:
```
e2e/
├── fixtures.ts           # 테스트 공통 설정 (로그인 등)
├── auth.spec.ts          # 인증 테스트
├── dashboard.spec.ts     # 대시보드 테스트
├── cookies.spec.ts       # 쿠키 관리 테스트
├── point-urls.spec.ts    # 포인트 URL 테스트
├── point-logs.spec.ts    # 포인트 로그 테스트
└── sites.spec.ts         # 사이트 관리 테스트
```

### 테스트 실행
```bash
# 모든 테스트 실행
npx playwright test

# UI 모드로 실행
npx playwright test --ui

# 특정 테스트 파일 실행
npx playwright test e2e/auth.spec.ts

# 리포트 확인
npx playwright show-report
```

### 테스트 커버리지
- [x] 로그인/로그아웃
- [x] 대시보드 통계 표시
- [x] 쿠키 CRUD + 유효성 토글
- [x] 포인트 URL CRUD + 영구 플래그 토글
- [x] 포인트 로그 조회/삭제/필터
- [x] 사이트 CRUD

---

## 향후 개선 사항

1. **라우트 보호**: ProtectedRoute/PublicRoute 컴포넌트 추가
2. **코드 스플리팅**: React.lazy를 이용한 동적 import 적용
3. **다크 모드**: 테마 전환 기능 추가
4. **단위 테스트**: Vitest를 이용한 컴포넌트 테스트
5. **에러 바운더리**: React Error Boundary 적용
6. **PWA**: 오프라인 지원 및 설치 가능한 앱

---

## 참고 문서

- [FRONTEND_DESIGN.md](./FRONTEND_DESIGN.md) - 전체 설계문서
- [API_TYPES.md](./API_TYPES.md) - TypeScript 타입 정의
- [COMPONENT_SPEC.md](./COMPONENT_SPEC.md) - 컴포넌트 명세
