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

이 프로젝트는 멀티 모듈 구조로 설계되었습니다:

```
pickupcoins/
├── pickup-common/     # 공통 모듈 (엔티티, 리포지토리, 설정)
├── pickup-server/     # 포인트 수집 서버 (크롤링, 적립, 스케줄링)
├── admin-api/         # 관리자 REST API 서버 (JWT 인증)
└── admin-web/         # 관리자 웹 프론트엔드 (React SPA)
```

### 모듈 설명

#### pickup-common
- JPA 엔티티 정의 (Cookie, PointUrl, SavedPoint, Site, SiteUser 등)
- Repository 인터페이스 및 QueryDSL 구현
- 공통 설정 클래스 (Properties, Exception)
- 추상 클래스 (AbstractWebCrawler, BaseCookieService)

#### pickup-server
- 웹 크롤링 서비스 (클리앙, 루리웹)
- 포인트 교환 서비스
- 스케줄러 (크롤링, 포인트 적립, 일일 리포트)
- Slack 알림 서비스

#### admin-api
- REST API 서버 (JWT 기반 인증)
- Swagger/OpenAPI 문서 제공
- 관리자 기능 API (대시보드, 쿠키, 포인트, 사이트, 사용자 관리)

#### admin-web
- React + TypeScript 기반 SPA
- admin-api와 통신하여 관리자 기능 제공
- 모던한 UI/UX (Tailwind CSS)

## 기술 스택

### Backend
- Spring Boot 3.2.0
- Spring Data JPA
- QueryDSL 5.0.0
- Spring Security
- Spring WebFlux (WebClient)

### Frontend
- React 19 + TypeScript
- Vite
- Tailwind CSS
- React Router, React Query, Zustand

### Database
- MySQL 8.0
- H2 (테스트용)

### Build & Deploy
- Gradle 8.5
- Java 17
- Docker & Docker Compose
- Jib (컨테이너 이미지 빌드)

### Monitoring
- Prometheus
- Grafana
- Spring Boot Actuator

### External APIs
- Slack API
- Jsoup (웹 크롤링)

## 시작하기

### 사전 요구사항

- JDK 17 이상
- MySQL 8.0 이상
- Gradle 8.5 이상
- Node.js (Tailwind CSS 빌드용)

### 환경 설정

1. 데이터베이스 설정

```sql
CREATE DATABASE pickupcoins DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'pickupcoins'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON pickupcoins.* TO 'pickupcoins'@'localhost';
FLUSH PRIVILEGES;
```

2. 애플리케이션 설정 파일 생성

`pickup-server/src/main/resources/application-local.yml`
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/pickupcoins
    username: pickupcoins
    password: your_password
```

`admin-api/src/main/resources/application-local.yml`
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/pickupcoins
    username: pickupcoins
    password: your_password
```

### Frontend 개발 서버 실행

```bash
# admin-web 디렉토리에서 실행
cd admin-web

# 의존성 설치 (최초 1회)
npm install

# 개발 서버 실행 (포트 5173)
npm run dev

# 프로덕션 빌드
npm run build
```

### 빌드 및 실행

```bash
# 전체 프로젝트 빌드
./gradlew clean build

# pickup-server 실행
./gradlew :pickup-server:bootRun

# admin-api 실행
./gradlew :admin-api:bootRun
```

### Docker 실행 (로컬 개발 환경)

로컬 개발 환경에서 전체 스택을 Docker로 실행할 수 있습니다.

```bash
# 1. Gradle 프로젝트 빌드
./gradlew clean build -x test

# 2. Docker Compose로 전체 스택 실행
docker-compose -f docker-compose-local.yml up --build -d

# 3. 컨테이너 상태 확인
docker ps
```

#### 컨테이너 정보

| 서비스 | 컨테이너명 | 포트 | 설명 |
|--------|-----------|------|------|
| admin-web | admin-web | 3000 | React 관리자 웹 (Nginx) |
| admin-api | admin-api | 8080 | REST API 서버 |
| pickup-server | pickup-server | 7070 | 포인트 수집 서버 |
| coin-mysql | coin-mysql | 3306 | MySQL 데이터베이스 |

#### 테스트 계정

애플리케이션 시작 시 자동으로 테스트 계정이 생성됩니다:
- **User ID**: `admin`
- **Password**: `test123`

#### 접속 URL

```
# 관리자 웹 페이지
http://localhost:3000

# REST API
http://localhost:8080

# 포인트 수집 서버
http://localhost:7070
```

#### Docker 컨테이너 관리

```bash
# 로그 확인
docker logs -f admin-api
docker logs -f pickup-server
docker logs -f admin-web

# 컨테이너 중지
docker-compose -f docker-compose-local.yml down

# 볼륨 포함 완전 삭제
docker-compose -f docker-compose-local.yml down -v
```

### Docker 실행 (Jib 빌드)

```bash
# Docker 이미지 빌드 (Jib)
./gradlew jibDockerBuild

# Docker Compose 실행
docker-compose up -d
```

## 사용 방법

### 1. 관리자 페이지 접속

```
# React 프론트엔드 (개발 서버)
http://localhost:5173

# REST API 서버
http://localhost:8082
```

### 2. 사용자 등록

- 사용자 계정 생성
- Slack 웹훅 URL 설정 (선택)

### 3. 쿠키 등록

- 네이버 로그인 후 쿠키 정보 복사
- 관리자 페이지에서 쿠키 등록
- 사이트별로 여러 쿠키 등록 가능

### 4. 자동 수집 시작

pickup-server가 자동으로 다음 작업을 수행합니다:
- 5분마다 커뮤니티 사이트 크롤링
- 발견된 포인트 URL로 자동 적립
- 매일 오전 7시 일일 리포트 전송 (Slack)

### 5. 대시보드 확인

- 포인트 적립 현황
- 일별/주별 통계
- 최근 적립 내역

## 주요 설정

### 크롤링 설정

`pickup-common/src/main/resources/application-common.yml`

```yaml
crawler:
  timeout: 10000  # HTTP 연결 타임아웃 (ms)

schedule:
  crawler-fixed-delay: 300000  # 크롤링 주기 (5분)
  point-fixed-delay: 300000    # 포인트 적립 주기 (5분)
  daily-report-cron: "0 0 7 * * *"  # 일일 리포트 시간 (매일 오전 7시)
```

### 네이버 포인트 설정

```yaml
naver:
  point:
    save-keyword: "적립"
    invalid-cookie-keyword: "로그인이 필요"
    amount-pattern: "\\s\\d+원이 적립 됩니다."
    user-agent: "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36"
```

## 모니터링

### Actuator 엔드포인트

pickup-server와 admin-api 모두 Spring Boot Actuator를 통해 모니터링 가능합니다.

```
# Health Check
http://localhost:8080/actuator/health
http://localhost:8082/actuator/health

# Metrics (Prometheus)
http://localhost:8080/actuator/prometheus
http://localhost:8082/actuator/prometheus
```

### Prometheus & Grafana

별도 모니터링 스택: [Prometheus, Grafana Repository](https://github.com/hajubal/monitoring)

### 로그 확인

로그 레벨 및 포맷은 `LOGGING_GUIDELINES.md` 참조

## 개발 가이드

### 코드 스타일

- Google Java Format 적용
- Spotless 플러그인으로 자동 포맷팅
- Import 정리 및 미사용 import 자동 제거

```bash
# 코드 포맷 확인
./gradlew spotlessCheck

# 코드 포맷 적용
./gradlew spotlessApply
```

Spotless 설정 (build.gradle):
```gradle
spotless {
    java {
        googleJavaFormat()
        importOrder()
        removeUnusedImports()
    }
}
```

### 테스트

```bash
# 단위 테스트 실행
./gradlew test

# 통합 테스트 실행
./gradlew test

# 모든 테스트 실행
./gradlew build
```

### 로깅 가이드라인

로그 작성 규칙은 `LOGGING_GUIDELINES.md` 참조

주요 로깅 규칙:
- ERROR: 시스템 오류, 즉시 조치 필요
- WARN: 잠재적 문제, 모니터링 필요
- INFO: 주요 비즈니스 이벤트, 상태 변경
- DEBUG: 상세 디버깅 정보 (개발 환경에서만 활성화)

로그 레벨별 환경 설정:
- 개발: DEBUG 레벨까지 모두 출력
- 프로덕션: INFO 레벨 이상만 출력

## 성능 최적화

### 데이터베이스 인덱스

주요 테이블에 다음 인덱스가 적용되어 있습니다:
- Cookie: (siteName, isValid), (site_user_id)
- SavedPoint: (created_date)
- PointUrl: (name, permanent), (created_date)

### N+1 쿼리 해결

- SavedPoint 조회 시 fetchJoin 사용
- CookieRepository에 @EntityGraph 적용

### 캐싱

- WebClient 싱글톤 패턴 적용

## 보안

### Spring Security 설정

- Form 로그인 기반 인증
- CSRF 보호 활성화
- XSS Protection 헤더
- Content Security Policy (CSP) 적용
- 세션 관리 (최대 1개 세션, 중복 로그인 방지)

### 민감 정보 관리

- 쿠키 값은 암호화되지 않으므로 데이터베이스 접근 제어 필요
- 프로덕션 환경에서는 환경 변수를 통한 설정 권장

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
- Java Version: 17
- Spring Boot Version: 3.2.0

## 릴리즈 노트

### v2.0.0 (2025)
- 멀티 모듈 리팩토링 (pickup-common 분리)
- 성능 최적화 (N+1 쿼리 해결, 인덱스 추가)
- Security 설정 강화 (XSS, CSP, 세션 관리)
- 코드 품질 개선 (추상 클래스, 중복 제거)
- 단위 테스트 추가

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
