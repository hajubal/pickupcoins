# Admin Server

PickupCoins 관리자 웹 애플리케이션

## 개요

admin-server는 PickupCoins 시스템의 관리자 웹 인터페이스를 제공하는 Spring Boot 애플리케이션입니다. Thymeleaf와 Tailwind CSS를 사용하여 사용자 친화적인 대시보드와 관리 기능을 제공합니다.

## 주요 기능

### 1. 대시보드
- 포인트 적립 현황 요약
- 일별/주별 포인트 통계 차트
- 쿠키 유효성 현황
- 포인트 URL 수집 통계

### 2. 사용자 관리
- 사용자 계정 CRUD
- 비밀번호 변경
- Slack 웹훅 URL 설정
- 사용자별 권한 관리

### 3. 쿠키 관리
- 네이버 로그인 쿠키 등록/수정/삭제
- 쿠키 유효성 상태 확인
- 사이트별 쿠키 관리
- 쿠키 자동 갱신 기능

### 4. 포인트 URL 관리
- 수집된 포인트 URL 목록 조회
- URL 상세 정보 확인
- 영구 URL 설정
- URL 삭제

### 5. 포인트 적립 로그
- 사용자별 포인트 적립 내역 조회
- 적립 금액 및 일시 확인
- 페이지네이션 지원

### 6. 사이트 관리
- 크롤링 대상 사이트 등록/수정/삭제
- 사이트 URL 및 도메인 설정

## 기술 스택

### Backend
- Spring Boot 3.2.0
- Spring Security (Form 로그인)
- Spring Data JPA
- QueryDSL 5.0.0
- Thymeleaf (템플릿 엔진)

### Frontend
- Tailwind CSS 3.x
- HTML5
- JavaScript (Vanilla)

### Database
- MySQL 8.0 (프로덕션)
- H2 (테스트)

### Build Tools
- Gradle 8.5
- Node.js & npm (Tailwind CSS 빌드)

## 프로젝트 구조

```
admin-server/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── me/synology/hajubal/coins/
│   │   │       ├── code/              # 공통 코드
│   │   │       │   └── SiteName.java
│   │   │       ├── conf/              # 설정 클래스
│   │   │       │   ├── CommonControllerAdvice.java
│   │   │       │   ├── ExceptionControllerAdvice.java
│   │   │       │   ├── JpaConfig.java
│   │   │       │   └── SecurityConfig.java
│   │   │       ├── controller/        # 웹 컨트롤러
│   │   │       │   ├── MainController.java
│   │   │       │   ├── CookieController.java
│   │   │       │   ├── PointController.java
│   │   │       │   ├── SiteController.java
│   │   │       │   ├── SiteUserController.java
│   │   │       │   └── RestEndpointController.java
│   │   │       ├── controller/dto/    # 컨트롤러 DTO
│   │   │       │   ├── PasswordUpdateDto.java
│   │   │       │   ├── ResponseEntityDto.java
│   │   │       │   ├── SiteUserDto.java
│   │   │       │   └── UserCookieDto.java
│   │   │       ├── service/           # 비즈니스 로직
│   │   │       │   ├── CookieService.java
│   │   │       │   ├── DashboardService.java
│   │   │       │   ├── PointManageService.java
│   │   │       │   ├── PointUrlService.java
│   │   │       │   ├── SavedPointService.java
│   │   │       │   ├── SiteService.java
│   │   │       │   ├── SiteUserService.java
│   │   │       │   └── UserDetailsServiceImpl.java
│   │   │       ├── service/dto/       # 서비스 DTO
│   │   │       │   ├── DashboardDto.java
│   │   │       │   └── ExchangeDto.java
│   │   │       └── PickUpCoinsAdminApplication.java
│   │   └── resources/
│   │       ├── application.yml        # 기본 설정
│   │       ├── application-local.yml  # 로컬 환경 설정
│   │       ├── application-dev.yml    # 개발 환경 설정
│   │       ├── application-prod.yml   # 프로덕션 환경 설정
│   │       ├── static/                # 정적 리소스
│   │       │   ├── css/
│   │       │   ├── js/
│   │       │   └── assets/
│   │       └── templates/             # Thymeleaf 템플릿
│   │           ├── dashboard.html
│   │           ├── login.html
│   │           ├── cookie/
│   │           ├── point/
│   │           ├── site/
│   │           └── siteUser/
│   └── test/
│       ├── java/                      # 단위 테스트
│       └── integTest/                 # 통합 테스트
├── build.gradle
├── package.json                       # npm 설정 (Tailwind CSS)
└── tailwind.config.js                 # Tailwind CSS 설정
```

## 주요 클래스 설명

### Controller

#### MainController
- 대시보드 페이지 렌더링
- 로그인 페이지 제공

#### CookieController
- 쿠키 CRUD 작업 처리
- 쿠키 유효성 검증

#### PointController
- 포인트 URL 목록 조회
- 포인트 적립 로그 조회

#### SiteUserController
- 사용자 계정 관리
- 비밀번호 변경
- Slack 웹훅 설정

### Service

#### DashboardService
- 대시보드 통계 데이터 집계
- 일별/주별 포인트 합산
- 차트 데이터 생성

#### CookieService (extends BaseCookieService)
- 쿠키 CRUD 작업
- 쿠키 유효성 관리
- 사용자별 쿠키 조회

#### SavedPointService
- 포인트 적립 내역 조회
- 사용자별/기간별 통계

### Configuration

#### JpaConfig
- JPA 설정 및 QueryDSL 연동
- Auditing 활성화

#### CommonControllerAdvice
- 공통 모델 속성 설정
- 전체 컨트롤러에 적용되는 공통 처리

#### ExceptionControllerAdvice
- 전역 예외 처리
- 사용자 친화적인 에러 페이지 제공

### Security

#### SecurityConfig
- Spring Security 설정
- Form 로그인 구성
- CSRF 보호
- XSS Protection
- Content Security Policy (CSP)
- 세션 관리 (최대 1개 세션)

#### UserDetailsServiceImpl
- Spring Security UserDetailsService 구현
- 데이터베이스 기반 사용자 인증

## 환경 설정

### application.yml

```yaml
spring:
  application:
    name: admin-server
  profiles:
    active: local

server:
  port: 8081

logging:
  level:
    root: INFO
    me.synology.hajubal.coins: DEBUG
```

### application-local.yml (예시)

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/pickupcoins
    username: pickupcoins
    password: your_password
    driver-class-name: com.mysql.cj.jdbc.Driver

  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        format_sql: true
```

## Tailwind CSS 설정

### package.json

```json
{
  "scripts": {
    "build-css": "tailwindcss -i ./src/main/resources/static/css/input.css -o ./src/main/resources/static/css/output.css --watch",
    "build": "tailwindcss -i ./src/main/resources/static/css/input.css -o ./src/main/resources/static/css/output.css --minify"
  },
  "devDependencies": {
    "tailwindcss": "^3.x"
  }
}
```

### 개발 워크플로우

```bash
# 의존성 설치
npm install

# 개발 모드 (watch)
npm run build-css

# 프로덕션 빌드
npm run build
```

## 보안 설정

### 인증/인가
- Form 로그인 방식
- 로그인 페이지: `/login`
- 로그인 성공 후 리다이렉트: `/dashboard`

### 접근 권한
- 모든 페이지 인증 필요 (로그인 페이지 제외)
- 정적 리소스 (CSS, JS, 이미지) 공개
- Actuator health/info 엔드포인트만 공개

### 보안 헤더
- X-Content-Type-Options: nosniff
- X-Frame-Options: SAMEORIGIN
- X-XSS-Protection: 1; mode=block
- Content-Security-Policy: default-src 'self'; script-src 'self' 'unsafe-inline'; style-src 'self' 'unsafe-inline'

### 세션 관리
- 세션 생성 정책: IF_REQUIRED
- 최대 동시 세션: 1개
- 중복 로그인 방지: false (기존 세션 종료)

## 실행 방법

### 개발 환경

```bash
# Tailwind CSS 빌드 (별도 터미널)
cd admin-server
npm run build-css

# 애플리케이션 실행
cd ..
./gradlew :admin-server:bootRun --args='--spring.profiles.active=local'
```

### 프로덕션 환경

```bash
# Tailwind CSS 프로덕션 빌드
cd admin-server
npm run build

# 애플리케이션 빌드
cd ..
./gradlew :admin-server:build

# JAR 실행
java -jar admin-server/build/libs/admin-server-2.0.0.jar --spring.profiles.active=prod
```

## 테스트

### 단위 테스트

```bash
./gradlew :admin-server:test
```

### 통합 테스트

```bash
./gradlew :admin-server:integrationTest
```

테스트는 H2 인메모리 데이터베이스를 사용합니다.

## API 엔드포인트

### Web Pages

| Method | Path | Description |
|--------|------|-------------|
| GET | `/` | 메인 페이지 (대시보드로 리다이렉트) |
| GET | `/dashboard` | 대시보드 |
| GET | `/login` | 로그인 페이지 |
| POST | `/login` | 로그인 처리 |
| GET | `/logout` | 로그아웃 |
| GET | `/cookie` | 쿠키 목록 |
| GET | `/cookie/new` | 쿠키 등록 폼 |
| POST | `/cookie` | 쿠키 저장 |
| GET | `/cookie/{id}` | 쿠키 수정 폼 |
| PUT | `/cookie/{id}` | 쿠키 수정 |
| DELETE | `/cookie/{id}` | 쿠키 삭제 |
| GET | `/pointUrl` | 포인트 URL 목록 |
| GET | `/savePointLog` | 포인트 적립 로그 |
| GET | `/site` | 사이트 목록 |
| GET | `/siteUser` | 사용자 목록 |

### REST API

| Method | Path | Description |
|--------|------|-------------|
| GET | `/actuator/health` | 헬스 체크 |
| GET | `/actuator/info` | 애플리케이션 정보 |

## 모니터링

### Actuator Endpoints

```
http://localhost:8081/actuator/health
http://localhost:8081/actuator/info
```

### 로그 위치

개발 환경: 콘솔 출력
프로덕션 환경: 설정에 따라 파일 또는 외부 로깅 시스템

## 트러블슈팅

### Tailwind CSS가 적용되지 않는 경우

```bash
# CSS 재빌드
npm run build

# 캐시 클리어 후 재시작
./gradlew clean :admin-server:bootRun
```

### 로그인 실패

- 데이터베이스 연결 확인
- 사용자 계정 존재 여부 확인
- 비밀번호 암호화 방식 확인 (BCrypt)

### 세션 타임아웃

기본 세션 타임아웃은 30분입니다. application.yml에서 변경 가능:

```yaml
server:
  servlet:
    session:
      timeout: 30m
```

## 의존성

주요 의존성은 `build.gradle` 참조:
- pickup-common (공통 모듈)
- Spring Boot Starter Web
- Spring Boot Starter Security
- Spring Boot Starter Data JPA
- Spring Boot Starter Thymeleaf
- Thymeleaf Spring Security Extras
- QueryDSL
- MySQL Connector
- H2 Database (테스트)

## 참고 문서

- [Spring Boot Reference](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Spring Security Reference](https://docs.spring.io/spring-security/reference/)
- [Thymeleaf Documentation](https://www.thymeleaf.org/documentation.html)
- [Tailwind CSS Documentation](https://tailwindcss.com/docs)
