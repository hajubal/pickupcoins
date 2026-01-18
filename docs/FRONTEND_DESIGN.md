# PickupCoins Admin Frontend 설계문서

## 개요
PickupCoins 백엔드 Admin API를 소비하는 React 기반 관리자 프론트엔드 애플리케이션

**프로젝트 위치**: `C:\Users\hajub\pickupcoins-admin`

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
| React Hook Form + Zod | Latest | 폼 관리/검증 |
| Recharts | Latest | 차트 (대시보드) |
| Lucide React | Latest | 아이콘 |
| date-fns | Latest | 날짜 처리 |

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
pickupcoins-admin/
├── public/
├── src/
│   ├── api/                    # API 레이어
│   ├── components/
│   │   ├── ui/                 # shadcn/ui 컴포넌트
│   │   ├── forms/              # 폼 컴포넌트
│   │   ├── data-table/         # 테이블 컴포넌트
│   │   └── common/             # 공통 컴포넌트
│   ├── features/               # 기능별 모듈
│   │   ├── auth/
│   │   ├── dashboard/
│   │   ├── cookies/
│   │   ├── point-urls/
│   │   ├── point-logs/
│   │   └── sites/
│   ├── layouts/
│   ├── routes/
│   ├── stores/
│   ├── types/
│   ├── lib/
│   ├── App.tsx
│   ├── main.tsx
│   └── index.css
├── .env
├── package.json
├── tailwind.config.js
├── tsconfig.json
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
- 유효성 토글

### 4. 포인트 URL 관리 (`/point-urls`)
- URL 목록 테이블
- 생성/수정/삭제 기능
- 영구 플래그 토글

### 5. 포인트 로그 (`/point-logs`)
- 날짜 필터
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
