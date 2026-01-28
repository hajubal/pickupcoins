# Admin Frontend 및 E2E 테스트 추가

## 작업 일시
2026-01-28 23:40

## 작업 요청
관리자 프론트엔드 구현 및 E2E 테스트 환경 구축

## 수정 파일

### 신규 추가
- `admin/` - React 기반 관리자 프론트엔드 (shadcn/ui, React Router, TanStack Query)
- `e2e/` - Playwright E2E 테스트
- `playwright.config.ts` - Playwright 설정
- `docs/design/` - 설계 문서

### 수정
- `docker-compose.yml` - 개발 환경으로 변경 (볼륨 마운트, hot reload 지원)
- `package.json` - @playwright/test 의존성 추가
- `tsconfig.json` - 설정 업데이트
- `.dockerignore` - Docker 빌드 최적화

### 삭제
- `docker-compose.dev.yml` - docker-compose.yml로 통합
- `docs/ADMIN_FRONTEND_IMPLEMENTATION.md` - docs/design/으로 이동
- `docs/API_TYPES.md` - docs/design/으로 이동
- `docs/COMPONENT_SPEC.md` - docs/design/으로 이동
- `docs/FRONTEND_DESIGN.md` - docs/design/으로 이동

### 버그 수정
- `src/modules/auth/auth.service.spec.ts` - JwtPayload.sub 타입 오류 수정 (BigInt → string)
- `src/modules/point/exchange.service.ts` - 미사용 import 및 파라미터 경고 수정
- `src/modules/point/point.service.ts` - 미사용 파라미터 경고 수정

## 주요 변경사항

### 1. 관리자 프론트엔드 (admin/)
- React 18 + TypeScript + Vite 기반
- shadcn/ui 컴포넌트 라이브러리 사용
- TanStack Query로 서버 상태 관리
- React Router v6로 라우팅 처리
- 주요 페이지: 로그인, 대시보드, 쿠키 관리, 포인트 URL, 포인트 로그, 사이트 관리

### 2. E2E 테스트 (e2e/)
- Playwright 테스트 프레임워크 도입
- 인증, 대시보드, 쿠키, 포인트 URL, 포인트 로그, 사이트 관리 테스트

### 3. Docker Compose 개발 환경
- 볼륨 마운트로 hot reload 지원
- builder 스테이지 사용으로 빠른 재빌드
- admin-web 서비스 추가 (profile: admin)

## 테스트 결과
- [x] 단위 테스트 통과 (88 tests passed)
- [x] ESLint 검사 통과 (no errors, no warnings)

## 특이사항
- JwtPayload.sub가 string 타입으로 변경되어 있어 테스트 코드 수정 필요했음
- 미사용 import/변수에 대한 lint 경고 해결

## 후속 작업
- [ ] E2E 테스트 CI/CD 파이프라인 통합 (GitHub Actions)
- [ ] 관리자 프론트엔드 프로덕션 빌드 및 배포 설정
- [ ] Nginx 프로덕션 설정 (gzip, 캐시 등)
