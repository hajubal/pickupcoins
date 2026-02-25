# AGENTS.md

## Cursor Cloud specific instructions

### 프로젝트 개요
PickupCoins: 네이버 포인트 자동 수집 시스템. NestJS 백엔드 (포트 8080) + React/Vite 프론트엔드 (포트 5173). SQLite 파일 DB 사용 (외부 DB 서버 불필요).

### Node.js 버전
프로젝트는 Node.js 20.x를 요구합니다. VM에는 nvm이 설치되어 있으며, `nvm use 20`으로 전환 가능합니다.

### 빌드 관련 주의사항
- `tsconfig.json`에 `incremental: true`가 설정되어 있어, `tsconfig.build.tsbuildinfo` 캐시 파일이 오래되면 빌드가 출력 없이 성공하는 것처럼 보일 수 있습니다. 빌드 문제 발생 시 `rm -f tsconfig.build.tsbuildinfo`를 실행한 후 재빌드하세요.
- `nest-cli.json`의 `deleteOutDir: true`와 incremental 빌드가 충돌할 수 있어 위 조치가 필요합니다.

### 주요 명령어
README.md의 "개발 가이드" 섹션 참조. 요약:
- 백엔드 lint: `npm run lint`
- 백엔드 테스트: `npm run test`
- 백엔드 빌드: `npm run build`
- 백엔드 개발 서버: `npm run start:dev` (포트 8080)
- 프론트엔드 개발 서버: `cd admin && npm run dev` (포트 5173)
- Prisma DB 동기화: `npm run prisma:push`

### 프론트엔드 린트
`admin/` 디렉토리에 `eslint.config.js`가 없어서 프론트엔드 `npm run lint`는 실패합니다 (리포지토리 기존 이슈).

### 테스트 계정
시드 데이터로 `admin` / `admin123` 계정이 자동 생성됩니다 (백엔드 시작 시).

### 환경 변수
`.env.example`을 `.env`로 복사하면 기본 개발 환경에서 바로 사용 가능합니다. SQLite DB는 `prisma/dev.db`에 자동 생성됩니다.
