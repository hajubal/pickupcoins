# PickupCoins Admin Frontend 설계문서

> 최종 업데이트: 2026-01-28

## 개요
PickupCoins 백엔드 Admin API를 소비하는 React 기반 관리자 프론트엔드 애플리케이션

**프로젝트 위치**: `admin/` (모노레포 내 프론트엔드 폴더)

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
| Radix UI | Latest | UI 프리미티브 |
| React Hook Form | 7.53.x | 폼 관리 |
| Zod | 3.23.x | 스키마 검증 |
| Lucide React | 0.453.x | 아이콘 |

## 백엔드 API 정보

### 서버 설정
- **Base URL**: `http://localhost:8080/api/v1`
- **인증**: JWT Bearer Token (Authorization 헤더)
- **Swagger**: `http://localhost:8080/api-docs`
- **CORS**: localhost:3000, localhost:5173 허용

### 토큰 유효기간
| 토큰 유형 | 유효기간 |
|----------|---------|
| Access Token | 15분 |
| Refresh Token | 7일 |
| Remember Me | 15일 |

## 디렉토리 구조

```
admin/
├── public/
├── src/
│   ├── api/                    # API 레이어
│   │   ├── client.ts           # Axios 인스턴스 + 인터셉터
│   │   ├── auth.ts             # 인증 API
│   │   ├── dashboard.ts        # 대시보드 API
│   │   ├── cookies.ts          # 쿠키 API
│   │   ├── point-urls.ts       # 포인트 URL API
│   │   ├── point-logs.ts       # 포인트 로그 API
│   │   ├── sites.ts            # 사이트 API
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
│   └── index.css
│
├── .gitignore
├── Dockerfile
├── nginx.conf
├── package.json
├── postcss.config.js
├── tailwind.config.js
├── tsconfig.json
├── tsconfig.node.json
└── vite.config.ts
```

## 화면 설계

### 1. 로그인 화면 (`/login`)
- 카드 형태 중앙 정렬
- loginId, password 입력 필드
- Remember Me 체크박스
- 로그인 버튼

### 2. 대시보드 (`/dashboard`)
- 오늘 적립 포인트 (전일 대비 비율)
- 이번 주 적립 포인트 (전주 대비 비율)
- 수집된 URL 수 (오늘/이번 주)

### 3. 쿠키 관리 (`/cookies`)
- 쿠키 목록 테이블
- 생성/수정/삭제 기능
- 유효성 토글 (Switch)

### 4. 포인트 URL 관리 (`/point-urls`)
- URL 목록 테이블
- 생성/수정/삭제 기능
- 영구 플래그 토글 (Switch)

### 5. 포인트 로그 (`/point-logs`)
- 날짜 필터 (시작일, 종료일)
- 페이지네이션
- 삭제 기능

### 6. 사이트 관리 (`/sites`)
- 사이트 목록 테이블
- 생성/수정/삭제 기능

## 인증 흐름

1. 로그인 요청 → accessToken, refreshToken 수신
2. accessToken을 Authorization 헤더에 포함
3. 401 에러 시 refreshToken으로 갱신 시도
4. 갱신 실패 시 로그인 페이지로 리다이렉트

## Docker 배포

### Dockerfile
- Node.js 기반 빌드 스테이지
- Nginx로 정적 파일 서빙

### Docker Compose
- `docker-compose.yml`에 admin 서비스 포함
- 포트: 5173 (개발) / 80 (프로덕션)

## 참고 문서

- [API_TYPES.md](./API_TYPES.md) - TypeScript 타입 정의
- [COMPONENT_SPEC.md](./COMPONENT_SPEC.md) - 컴포넌트 명세
- [ADMIN_FRONTEND_IMPLEMENTATION.md](./ADMIN_FRONTEND_IMPLEMENTATION.md) - 구현 완료 문서
