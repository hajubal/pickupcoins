# Pickup Server

PickupCoins 포인트 수집 서버

## 개요

pickup-server는 PickupCoins 시스템의 핵심 백엔드 서버로, 커뮤니티 사이트 크롤링, 포인트 자동 적립, 스케줄링, Slack 알림 등의 기능을 담당합니다.

## 주요 기능

### 1. 웹 크롤링
- 클리앙, 루리웹 등 커뮤니티 사이트 자동 크롤링
- 네이버 포인트 적립 URL 수집
- Jsoup 기반 HTML 파싱
- 사이트별 커스터마이징 가능한 크롤러

### 2. 포인트 자동 적립
- 수집된 URL로 자동 포인트 적립 시도
- WebClient 기반 비동기 HTTP 통신
- 쿠키 기반 인증
- 적립 결과 자동 저장

### 3. 스케줄링
- 크롤링 스케줄러 (5분 주기)
- 포인트 적립 스케줄러 (5분 주기)
- 일일 리포트 스케줄러 (매일 오전 7시)

### 4. Slack 알림
- 포인트 적립 성공/실패 알림
- 쿠키 무효화 알림
- 일일 수집 통계 리포트

### 5. 쿠키 관리
- 쿠키 유효성 자동 검증
- 무효화된 쿠키 자동 비활성화
- 쿠키 자동 갱신 (Set-Cookie 헤더)

## 기술 스택

### Backend
- Spring Boot 3.2.0
- Spring Data JPA
- Spring WebFlux (WebClient)
- QueryDSL 5.0.0
- Spring Security

### External Libraries
- Jsoup 1.15.3 (웹 크롤링)
- Slack API Client 1.27.1
- P6Spy (SQL 로깅)

### Database
- MySQL 8.0 (프로덕션)
- H2 (테스트)

### Build Tools
- Gradle 8.5

## 프로젝트 구조

```
pickup-server/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── me/synology/hajubal/coins/
│   │   │       ├── code/                     # 공통 코드
│   │   │       │   └── SiteName.java
│   │   │       ├── conf/                     # 설정 클래스
│   │   │       │   ├── JpaConfig.java
│   │   │       │   ├── SecurityConfig.java
│   │   │       │   ├── SlackConfig.java
│   │   │       │   └── WebClientConfig.java
│   │   │       ├── crawler/                  # 크롤링 관련
│   │   │       │   ├── WebCrawler.java       # 크롤러 인터페이스
│   │   │       │   ├── SiteData.java         # 사이트 정보 인터페이스
│   │   │       │   ├── PointUrlSelector.java # URL 선택자 인터페이스
│   │   │       │   ├── PointPostUrlFetcher.java
│   │   │       │   └── impl/
│   │   │       │       ├── clien/            # 클리앙 크롤러
│   │   │       │       └── ruliweb/          # 루리웹 크롤러
│   │   │       ├── exception/                # 예외 클래스
│   │   │       │   ├── SlackConfigException.java
│   │   │       │   └── SlackServiceException.java
│   │   │       ├── schedule/                 # 스케줄러
│   │   │       │   └── Schedulers.java
│   │   │       ├── service/                  # 비즈니스 로직
│   │   │       │   ├── CookieService.java
│   │   │       │   ├── ExchangeService.java
│   │   │       │   ├── PointManageService.java
│   │   │       │   ├── PointUrlService.java
│   │   │       │   ├── SavedPointService.java
│   │   │       │   ├── WebCrawlerService.java
│   │   │       │   ├── NaverSavePointService.java
│   │   │       │   ├── SlackService.java
│   │   │       │   └── ReportService.java
│   │   │       ├── service/dto/              # DTO 클래스
│   │   │       │   └── ExchangeDto.java
│   │   │       ├── DataInitializer.java      # 데이터 초기화
│   │   │       └── PickUpCoinsApplication.java
│   │   └── resources/
│   │       ├── application.yml               # 기본 설정
│   │       ├── application-local.yml         # 로컬 환경
│   │       ├── application-dev.yml           # 개발 환경
│   │       └── application-prod.yml          # 프로덕션 환경
│   └── test/
│       └── java/                             # 단위 테스트
│           └── me/synology/hajubal/coins/
│               └── service/
│                   └── PointManageServiceTest.java
├── build.gradle
└── README.md
```

## 주요 클래스 설명

### Crawler

#### AbstractWebCrawler (pickup-common)
- 웹 크롤러 추상 클래스
- 공통 크롤링 로직 구현
- 템플릿 메서드 패턴 적용

#### ClienWebCrawler
- 클리앙 사이트 전용 크롤러
- CSS 셀렉터: "div.post_article a"

#### RuliwebCrawler
- 루리웹 사이트 전용 크롤러
- CSS 셀렉터: ".board_main_view a"

#### PointPostUrlFetcher
- 포인트 적립 게시글 URL 추출
- PointUrlSelector를 사용한 필터링

### Configuration

#### JpaConfig
- JPA 설정 및 QueryDSL 연동
- Auditing 활성화

#### SecurityConfig
- Spring Security 기본 설정
- 모든 엔드포인트 허용 (개발 편의성)

#### SlackConfig
- Slack 클라이언트 빈 설정
- 환경별 설정 관리

#### WebClientConfig
- WebClient 빌드 설정
- HTTP 통신 클라이언트 구성

### Exception

#### SlackConfigException
- Slack 설정 오류 시 발생
- 설정 검증 실패 처리

#### SlackServiceException
- Slack 메시지 전송 실패 시 발생
- 런타임 예외 래핑

### Service

#### WebCrawlerService
- 모든 등록된 크롤러 실행
- 크롤링 결과 통합 관리

#### ExchangeService
- 포인트 URL 호출 및 적립 처리
- WebClient 기반 HTTP 통신
- 응답 분석 및 후속 처리 (쿠키 무효화, 포인트 저장)

#### PointManageService
- 포인트 적립 후처리
- 쿠키 갱신
- 포인트 금액 추출 및 저장

#### NaverSavePointService
- 네이버 포인트 적립 오케스트레이션
- 사용자별 미호출 URL 조회
- 적립 결과 집계

#### SlackService
- Slack 웹훅 메시지 전송
- 알림 템플릿 관리

#### ReportService
- 일일 수집 통계 생성
- Slack 리포트 전송

### Scheduler

#### Schedulers
- webCrawlerScheduler: 크롤링 실행 (5분 주기)
- naverPointSaveScheduler: 포인트 적립 실행 (5분 주기)
- dailyReport: 일일 리포트 전송 (매일 오전 7시)

## 환경 설정

### application.yml

```yaml
spring:
  application:
    name: pickup-server
  profiles:
    active: local

server:
  port: 8080

logging:
  level:
    root: INFO
    me.synology.hajubal.coins: DEBUG
```

### application-common.yml (pickup-common)

```yaml
crawler:
  timeout: 10000

schedule:
  crawler-fixed-delay: 300000
  point-fixed-delay: 300000
  daily-report-cron: "0 0 7 * * *"

naver:
  point:
    save-keyword: "적립"
    invalid-cookie-keyword: "로그인이 필요"
    amount-pattern: "\\s\\d+원이 적립 됩니다."
    user-agent: "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36"
```

## 크롤링 동작 원리

### 1. 크롤링 단계

```
Schedulers.webCrawlerScheduler()
    ↓
WebCrawlerService.crawling()
    ↓
각 WebCrawler.crawling()
    ↓
fetchPostUrls(siteUrl) - 게시글 URL 목록 수집
    ↓
extractPointUrls(postUrls) - 포인트 URL 추출
    ↓
PointUrl 엔티티 저장
```

### 2. 포인트 적립 단계

```
Schedulers.naverPointSaveScheduler()
    ↓
NaverSavePointService.savePoint()
    ↓
사용자별 미호출 URL 조회
    ↓
ExchangeService.exchange(url, cookie)
    ↓
WebClient로 HTTP GET 요청
    ↓
응답 분석:
  - "적립" 포함 → PointManageService.savePointPostProcess()
  - "로그인이 필요" 포함 → CookieService.invalid()
  - 그 외 → 로그만 기록
    ↓
SlackService로 결과 알림
```

### 3. 일일 리포트 단계

```
Schedulers.dailyReport()
    ↓
ReportService.report()
    ↓
어제 수집 통계 집계
    ↓
사용자별 Slack 리포트 전송
```

## 스케줄러 설정

### 크롤링 주기 변경

```yaml
schedule:
  crawler-fixed-delay: 600000  # 10분 (밀리초)
```

### 포인트 적립 주기 변경

```yaml
schedule:
  point-fixed-delay: 600000  # 10분 (밀리초)
```

### 일일 리포트 시간 변경

```yaml
schedule:
  daily-report-cron: "0 0 9 * * *"  # 매일 오전 9시
```

## 크롤러 추가 방법

### 1. 사이트 데이터 클래스 생성

```java
@Component
public class NewSiteData implements SiteData {
    @Override
    public String getSiteName() {
        return "새로운사이트";
    }

    @Override
    public String getDomain() {
        return "https://newsite.com";
    }

    @Override
    public String getBoardUrl() {
        return "https://newsite.com/board";
    }
}
```

### 2. URL 선택자 구현

```java
@Component
public class NewSitePointUrlSelector implements PointUrlSelector {
    @Override
    public String titleCssQuery() {
        return "div.list a.title";
    }

    @Override
    public boolean titleSelector(Element element) {
        return element.text().contains("포인트") ||
               element.text().contains("적립");
    }

    @Override
    public String linkExtractor(Element element) {
        return element.attr("href");
    }
}
```

### 3. 크롤러 클래스 생성

```java
@Component
public class NewSiteCrawler extends AbstractWebCrawler {
    private final PointPostUrlFetcher pointPostUrlFetcher;
    private final NewSiteData newSiteData;

    public NewSiteCrawler(
            PointPostUrlFetcher pointPostUrlFetcher,
            SiteRepository siteRepository,
            CrawlerProperties crawlerProperties,
            NewSiteData newSiteData) {
        super(siteRepository, crawlerProperties);
        this.pointPostUrlFetcher = pointPostUrlFetcher;
        this.newSiteData = newSiteData;
    }

    @Override
    protected SiteData getSiteData() {
        return newSiteData;
    }

    @Override
    protected String getArticleSelector() {
        return "div.content a";  // 게시글 본문의 링크 셀렉터
    }

    @Override
    protected Set<String> fetchPostUrls(String siteUrl) throws IOException {
        return pointPostUrlFetcher.fetchPostUrls(siteUrl);
    }
}
```

### 4. 데이터베이스에 사이트 정보 등록

admin-server를 통해 새로운 사이트 정보를 등록합니다.

## 실행 방법

### 개발 환경

```bash
./gradlew :pickup-server:bootRun --args='--spring.profiles.active=local'
```

### 프로덕션 환경

```bash
# 빌드
./gradlew :pickup-server:build

# JAR 실행
java -jar pickup-server/build/libs/pickup-server-2.0.0.jar --spring.profiles.active=prod
```

## 테스트

### 단위 테스트

```bash
./gradlew :pickup-server:test
```

테스트는 H2 인메모리 데이터베이스를 사용합니다.

## API 엔드포인트

### Actuator

| Method | Path | Description |
|--------|------|-------------|
| GET | `/actuator/health` | 헬스 체크 |
| GET | `/actuator/info` | 애플리케이션 정보 |
| GET | `/actuator/prometheus` | Prometheus 메트릭 |

## 모니터링

### 로그 레벨

개발 환경:
```yaml
logging:
  level:
    me.synology.hajubal.coins: DEBUG
    org.hibernate.SQL: DEBUG
```

프로덕션 환경:
```yaml
logging:
  level:
    me.synology.hajubal.coins: INFO
    org.hibernate: WARN
```

### 주요 로그 메시지

- `Crawling completed. site: {}, posts: {}, points: {}` - 크롤링 완료
- `Point exchange completed. user: {}, url: {}` - 포인트 적립 성공
- `Cookie is invalid. user: {}` - 쿠키 무효화
- `Failed to exchange point. url: {}, user: {}` - 포인트 적립 실패

## 성능 최적화

### WebClient 싱글톤

ExchangeService에서 WebClient를 싱글톤으로 관리하여 메모리 누수 방지:

```java
public ExchangeService(
        ...
        WebClient.Builder webClientBuilder) {
    this.webClient = webClientBuilder.build();
}
```

### 동기 처리

포인트 적립은 트랜잭션 경계를 명확히 하기 위해 block()으로 동기 처리:

```java
ResponseEntity<String> response = webClient.get()
    .uri(URI.create(url.getUrl()))
    ...
    .block(Duration.ofSeconds(30));
```

## 트러블슈팅

### 크롤링 실패

**증상**: `Failed to crawl URL` 로그 발생

**원인**:
- 사이트 구조 변경
- 네트워크 타임아웃
- CSS 셀렉터 오류

**해결**:
1. 사이트 HTML 구조 확인
2. CSS 셀렉터 업데이트
3. 타임아웃 설정 증가

### 포인트 적립 실패

**증상**: `Failed to extract amount` 로그 발생

**원인**:
- 응답 본문 형식 변경
- 정규식 패턴 불일치

**해결**:
1. 네이버 포인트 페이지 응답 확인
2. amount-pattern 정규식 업데이트

### Slack 알림 실패

**증상**: 알림이 전송되지 않음

**원인**:
- 잘못된 웹훅 URL
- 네트워크 오류

**해결**:
1. 웹훅 URL 유효성 확인
2. Slack API 상태 확인
3. 로그에서 오류 메시지 확인

## 의존성

주요 의존성은 `build.gradle` 참조:
- pickup-common (공통 모듈)
- Spring Boot Starter Web
- Spring Boot Starter WebFlux
- Spring Boot Starter Data JPA
- Spring Boot Starter Security
- Jsoup
- Slack API Client
- QueryDSL
- MySQL Connector
- H2 Database (테스트)

## 참고 문서

- [Spring Boot Reference](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Spring WebFlux Documentation](https://docs.spring.io/spring-framework/reference/web/webflux.html)
- [Jsoup Documentation](https://jsoup.org/)
- [Slack API Documentation](https://api.slack.com/)
