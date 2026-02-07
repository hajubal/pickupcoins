# PickupCoins

네이버 포인트 자동 수집 시스템

## 프로젝트 개요

PickupCoins는 클리앙, 루리웹 등의 커뮤니티 사이트에서 네이버 포인트 적립 URL을 자동으로 크롤링하고, 등록된 사용자 계정으로 포인트를 자동 적립하는 시스템입니다.

### 주요 기능

- 커뮤니티 사이트 크롤링을 통한 네이버 포인트 URL 수집
- 등록된 쿠키를 이용한 자동 포인트 적립
- 포인트 적립 현황 대시보드 제공
- Slack 웹훅을 통한 실시간 알림
- 사용자별 포인트 적립 통계

## 아키텍처

```
pickupcoins/
├── src/
│   ├── common/           # 공통 모듈 (데코레이터, DTO, 필터, 인터셉터)
│   ├── config/           # 환경 설정
│   ├── health/           # 헬스 체크 엔드포인트
│   └── modules/
│       ├── admin/        # 관리자 API (쿠키, 대시보드, 포인트URL, 사이트)
│       ├── auth/         # JWT 인증
│       ├── crawler/      # 웹 크롤러 (클리앙, 루리웹)
│       ├── notification/ # Slack 알림
│       ├── point/        # 포인트 교환
│       ├── prisma/       # Prisma ORM 서비스
│       ├── scheduler/    # 스케줄러
│       └── seed/         # 초기 데이터 시딩
├── admin/                # React 관리자 프론트엔드
├── e2e/                  # Playwright E2E 테스트
├── prisma/               # Prisma 스키마 및 마이그레이션
└── docs/design/          # 설계 문서
```

### 모듈 설명

#### common
- 공통 데코레이터 (CurrentUser)
- 공통 DTO (페이지네이션)
- HTTP 예외 필터
- 로깅/응답 변환 인터셉터

#### modules/admin
- Cookie: 사용자 쿠키 관리 API
- Dashboard: 대시보드 통계 API
- PointUrl: 포인트 URL 관리 API
- SavedPoint: 적립 포인트 기록 API
- Site: 크롤링 사이트 관리 API

#### modules/auth
- JWT 기반 인증
- Passport.js 통합
- 로그인/토큰 갱신 API

#### modules/crawler
- 추상 크롤러 베이스 클래스
- 클리앙 크롤러
- 루리웹 크롤러

#### modules/point
- 네이버 포인트 교환 서비스

#### modules/scheduler
- 크롤링 스케줄러 (5분 주기)
- 포인트 적립 스케줄러 (5분 주기)
- 일일 리포트 스케줄러 (매일 오전 7시)

#### modules/seed
- 애플리케이션 시작 시 초기 데이터 생성
- 테스트 계정 자동 생성 (admin/test123)

## 기술 스택

### Backend
- NestJS 10.x
- TypeScript 5.x
- Prisma ORM 5.x
- Passport.js + JWT

### Database
- SQLite (파일 기반, 서버 불필요)

### Build & Deploy
- Node.js 20.x
- npm
- Docker & Docker Compose

### Testing
- Jest (단위 테스트)
- Playwright (E2E 테스트)

### External APIs
- Slack API (@slack/web-api)
- Cheerio (웹 크롤링)
- Axios (HTTP 클라이언트)

## 시작하기

### 사전 요구사항

- Node.js 20.x 이상
- npm 10.x 이상

### 환경 설정

1. 환경 변수 설정

```bash
# .env.example 파일을 .env로 복사
cp .env.example .env
```

`.env` 파일 수정:
```env
# Database (SQLite 파일 경로)
DATABASE_URL="file:./prisma/dev.db"

# JWT Configuration
JWT_SECRET="your-super-secret-jwt-key-minimum-256-bits-for-hmac-sha256"

# Server
PORT=8080
NODE_ENV=development

# CORS
CORS_ORIGINS="http://localhost:5173,http://localhost:3000"
```

2. 데이터베이스 (SQLite)

별도 DB 서버 없이 파일로 동작합니다. `.env`의 `DATABASE_URL`이 가리키는 경로에 스키마를 적용하려면:

```bash
npm run prisma:push
```

### 설치 및 실행

```bash
# 의존성 설치
npm install

# Prisma 클라이언트 생성
npm run prisma:generate

# 데이터베이스 스키마 동기화 (SQLite 파일 생성/갱신)
npm run prisma:push

# 개발 서버 실행 (또는 한 번에: npm run dev)
npm run start:dev

# 프로덕션 빌드
npm run build

# 프로덕션 서버 실행
npm run start:prod
```

### Docker 실행 (개발 환경)

```bash
# Docker Compose로 개발 환경 실행
docker-compose up --build -d

# 관리자 프론트엔드 포함 실행
docker-compose --profile admin up --build -d

# 컨테이너 상태 확인
docker ps
```

#### 컨테이너 정보

| 서비스 | 컨테이너명 | 포트 | 설명 |
|--------|-----------|------|------|
| app | pickupcoins-api | 8080 | NestJS API 서버 (Hot Reload, SQLite DB 파일은 prisma 볼륨에 저장) |
| admin-web | pickupcoins-admin | 5173 | React 관리자 웹 (Vite Dev Server, --profile admin 필요) |

#### 테스트 계정

애플리케이션 시작 시 자동으로 테스트 계정이 생성됩니다:
- **User ID**: `admin`
- **Password**: `test123`

#### 접속 URL

```
# REST API
http://localhost:8080

# API 문서 (Swagger)
http://localhost:8080/api

# 관리자 웹 페이지 (개발)
http://localhost:5173
```

#### Docker 컨테이너 관리

```bash
# 로그 확인
docker logs -f pickupcoins-api

# 컨테이너 중지
docker-compose down

# 볼륨 포함 완전 삭제
docker-compose down -v
```

## 사용 방법

### 1. 관리자 페이지 접속

```
# React 프론트엔드 (개발)
http://localhost:5173

# REST API 서버
http://localhost:8080
```

### 2. 사용자 등록

- 사용자 계정 생성
- Slack 웹훅 URL 설정 (선택)

### 3. 쿠키 등록

- 네이버 로그인 후 쿠키 정보 복사
- 관리자 페이지에서 쿠키 등록
- 사이트별로 여러 쿠키 등록 가능

### 4. 자동 수집 시작

스케줄러가 자동으로 다음 작업을 수행합니다:
- 5분마다 커뮤니티 사이트 크롤링
- 발견된 포인트 URL로 자동 적립
- 매일 오전 7시 일일 리포트 전송 (Slack)

### 5. 대시보드 확인

- 포인트 적립 현황
- 일별/주별 통계
- 최근 적립 내역

## 주요 설정

### 환경 변수

| 변수명 | 설명 | 기본값 |
|--------|------|--------|
| DATABASE_URL | SQLite DB 파일 경로 (예: file:./prisma/dev.db) | - |
| JWT_SECRET | JWT 서명 키 | - |
| PORT | 서버 포트 | 8080 |
| CORS_ORIGINS | 허용 CORS 오리진 | - |
| CRAWLER_TIMEOUT | 크롤링 타임아웃 (ms) | 10000 |
| SCHEDULE_CRAWLER_FIXED_DELAY | 크롤링 주기 (ms) | 300000 |
| SCHEDULE_POINT_FIXED_DELAY | 포인트 적립 주기 (ms) | 300000 |
| SCHEDULE_DAILY_REPORT_CRON | 일일 리포트 크론 | 0 0 7 * * * |

### 네이버 포인트 설정

| 변수명 | 설명 |
|--------|------|
| NAVER_SAVE_KEYWORD | 적립 성공 키워드 |
| NAVER_INVALID_COOKIE_KEYWORD | 쿠키 만료 키워드 |
| NAVER_AMOUNT_PATTERN | 금액 추출 정규식 |

## API 문서

Swagger UI를 통해 API 문서를 확인할 수 있습니다:

```
http://localhost:8080/api
```

### 주요 엔드포인트

| Method | Path | 설명 |
|--------|------|------|
| POST | /auth/login | 로그인 |
| GET | /admin/dashboard | 대시보드 통계 |
| GET | /admin/cookies | 쿠키 목록 |
| POST | /admin/cookies | 쿠키 등록 |
| GET | /admin/point-urls | 포인트 URL 목록 |
| GET | /admin/saved-points | 적립 포인트 목록 |
| GET | /health | 헬스 체크 |

## 개발 가이드

### 코드 스타일

- ESLint + Prettier 적용
- TypeScript strict 모드

```bash
# 코드 포맷 확인
npm run lint

# 코드 포맷 적용
npm run format
```

### 테스트

```bash
# 단위 테스트 실행
npm run test

# 테스트 커버리지 확인
npm run test:cov

# NestJS E2E 테스트 실행
npm run test:e2e

# Playwright E2E 테스트 실행 (관리자 프론트엔드, SQLite 사용으로 DB 서버 불필요)
npx playwright test

# Playwright 테스트 UI 모드
npx playwright test --ui

# Playwright 테스트 리포트 확인
npx playwright show-report
```

### Prisma 명령어

```bash
# 스키마 변경 후 클라이언트 생성
npm run prisma:generate

# 데이터베이스에 스키마 푸시 (개발용)
npm run prisma:push

# 마이그레이션 생성 및 적용
npm run prisma:migrate

# Prisma Studio (GUI)
npm run prisma:studio
```

## 데이터베이스 스키마

### 주요 테이블

| 테이블명 | 설명 |
|----------|------|
| site | 크롤링 대상 사이트 |
| site_user | 관리자 사용자 |
| cookie | 네이버 쿠키 정보 |
| point_url | 크롤링된 포인트 URL |
| point_url_cookie | URL-쿠키 연결 (M:N) |
| point_url_call_log | URL 호출 로그 |
| saved_point | 적립 포인트 기록 |

### 인덱스

- Cookie: (siteName, isValid), (siteUserId)
- PointUrl: (name, permanent), (createdDate)
- SavedPoint: (createdDate)

## 트러블슈팅

### 자주 발생하는 문제

1. **쿠키 무효화**
   - 네이버 로그인 세션이 만료되면 자동으로 쿠키 무효 처리
   - Slack 알림을 통해 재등록 필요 알림

2. **크롤링 실패**
   - 사이트 구조 변경 시 크롤러 수정 필요
   - 로그 확인: `Failed to crawl URL`

3. **포인트 적립 실패**
   - 응답 본문에서 금액 추출 실패 시 0원으로 저장
   - 로그 확인: `Failed to extract amount`

## 버전 정보

- Current Version: 2.0.0
- Node.js Version: 20.x
- NestJS Version: 10.x

## 릴리즈 노트

### v2.0.0 (2026-01)
- **기술 스택 전환**: Java/Spring Boot → Node.js/NestJS
- **ORM 변경**: JPA/QueryDSL → Prisma
- **빌드 시스템**: Gradle → npm
- TypeScript 기반 개발 환경 구성
- Swagger API 문서 자동 생성
- 단위 테스트 추가 (Jest)
- **관리자 프론트엔드**: React + TypeScript + Vite 기반
  - shadcn/ui 컴포넌트 라이브러리
  - TanStack Query/Table 적용
  - JWT 인증 및 자동 토큰 갱신
- **E2E 테스트**: Playwright 테스트 프레임워크 도입
- **초기 데이터 시딩**: 애플리케이션 시작 시 테스트 계정 자동 생성

### v1.2.0
- UI를 Tailwind CSS 기반으로 변경
- 모던한 디자인 시스템 적용
- 반응형 레이아웃 구현

### v1.1.3
- dashboard 포인트 증감율 버그 수정
- Point log 페이지 url log -> point log 내용 변경

### v1.1.2
- Prometheus 설정 추가
- 커스텀 로그인 페이지 추가

### v1.1.1
- Report 내용: 쿠키별로 금액 세분화
- 화면에 어플리케이션 버전 표시

### v1.1.0
- 대시보드 소수점 자리수 수정
- Report 시간 변경 9시 -> 7시
- 사이트 사용자들 기준으로 Report 발송
- SpringBoot version 3.0 -> 3.2
- QueryDSL 적용

### v1.0.12
- Admin 디자인 적용
- Dashboard 적용

## 라이선스

이 프로젝트는 개인 학습 및 사용 목적으로 제작되었습니다.

## 기여

버그 리포트나 기능 제안은 GitHub Issues를 통해 제출해주세요.

## 문의

프로젝트 관련 문의사항이 있으시면 이슈를 생성해주세요.
