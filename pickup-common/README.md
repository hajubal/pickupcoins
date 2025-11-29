# Pickup Common

PickupCoins 공통 모듈

## 개요

pickup-common은 PickupCoins 시스템의 공통 모듈로, admin-server와 pickup-server에서 공유하는 엔티티, 리포지토리, 설정, 추상 클래스 등을 제공합니다.

## 주요 기능

### 1. JPA 엔티티
- 도메인 모델 정의
- 데이터베이스 테이블 매핑
- 연관 관계 설정
- 인덱스 정의

### 2. Repository
- Spring Data JPA 인터페이스
- QueryDSL 커스텀 구현
- 성능 최적화 쿼리

### 3. 공통 설정
- Configuration Properties 클래스
- 외부 설정 관리
- 공통 애플리케이션 설정

### 4. 예외 클래스
- 커스텀 예외 정의
- 일관된 예외 처리

### 5. 추상 클래스
- 크롤러 공통 로직
- 서비스 공통 로직
- 템플릿 메서드 패턴

## 프로젝트 구조

```
pickup-common/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── me/synology/hajubal/coins/
│   │   │       ├── config/               # 설정 클래스
│   │   │       │   ├── CrawlerProperties.java
│   │   │       │   ├── NaverPointProperties.java
│   │   │       │   └── ScheduleProperties.java
│   │   │       ├── crawler/              # 크롤러 공통
│   │   │       │   ├── AbstractWebCrawler.java
│   │   │       │   ├── WebCrawler.java
│   │   │       │   └── SiteData.java
│   │   │       ├── entity/               # JPA 엔티티
│   │   │       │   ├── BaseDataEntity.java
│   │   │       │   ├── Cookie.java
│   │   │       │   ├── PointUrl.java
│   │   │       │   ├── PointUrlCookie.java
│   │   │       │   ├── PointUrlCallLog.java
│   │   │       │   ├── SavedPoint.java
│   │   │       │   ├── Site.java
│   │   │       │   ├── SiteUser.java
│   │   │       │   └── type/
│   │   │       │       └── POINT_URL_TYPE.java
│   │   │       ├── exception/            # 예외 클래스
│   │   │       │   ├── CookieNotFoundException.java
│   │   │       │   ├── InvalidCookieException.java
│   │   │       │   └── PointExchangeException.java
│   │   │       ├── respository/          # 리포지토리
│   │   │       │   ├── CookieRepository.java
│   │   │       │   ├── PointUrlRepository.java
│   │   │       │   ├── PointUrlCookieRepository.java
│   │   │       │   ├── PointUrlCallLogRepository.java
│   │   │       │   ├── SavedPointRepository.java
│   │   │       │   ├── SavedPointRepositoryCustom.java
│   │   │       │   ├── SavedPointRepositoryImpl.java
│   │   │       │   ├── SiteRepository.java
│   │   │       │   └── SiteUserRepository.java
│   │   │       └── service/              # 공통 서비스
│   │   │           └── BaseCookieService.java
│   │   └── resources/
│   │       └── application-common.yml    # 공통 설정
│   └── test/
│       └── java/
├── build.gradle
└── README.md
```

## 엔티티 설명

### BaseDataEntity
모든 엔티티의 기본 클래스로 공통 필드를 제공합니다.

```java
public abstract class BaseDataEntity {
    @CreatedDate
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime modifiedDate;
}
```

### SiteUser
사용자 계정 정보를 저장합니다.

**주요 필드**:
- `loginId`: 로그인 ID (unique)
- `userName`: 사용자 이름
- `password`: BCrypt 암호화된 비밀번호
- `slackWebhookUrl`: Slack 웹훅 URL
- `active`: 계정 활성화 여부

### Cookie
네이버 로그인 쿠키 정보를 저장합니다.

**주요 필드**:
- `userName`: 네이버 사용자명
- `siteName`: 사이트 이름
- `cookie`: 쿠키 문자열
- `isValid`: 쿠키 유효성 여부
- `siteUser`: 소유 사용자 (ManyToOne)

**인덱스**:
- `idx_cookie_site_valid`: (siteName, isValid)
- `idx_cookie_site_user`: (site_user_id)

### Site
크롤링 대상 사이트 정보를 저장합니다.

**주요 필드**:
- `name`: 사이트 이름
- `domain`: 도메인 URL
- `url`: 크롤링 대상 게시판 URL

### PointUrl
수집된 포인트 적립 URL을 저장합니다.

**주요 필드**:
- `name`: 사이트 이름
- `url`: 포인트 적립 URL
- `pointUrlType`: URL 타입 (ENUM)
- `permanent`: 영구 URL 여부

**인덱스**:
- `idx_point_url_name_permanent`: (name, permanent)
- `idx_point_url_created`: (created_date)

### SavedPoint
적립된 포인트 내역을 저장합니다.

**주요 필드**:
- `cookie`: 사용한 쿠키 (ManyToOne)
- `amount`: 적립 금액
- `responseBody`: 응답 본문 (MEDIUMTEXT)

**인덱스**:
- `idx_saved_point_created`: (created_date)

### PointUrlCookie
사용자가 호출한 URL 기록을 저장합니다 (중복 호출 방지).

**주요 필드**:
- `pointUrl`: 호출한 URL (ManyToOne)
- `cookie`: 사용한 쿠키 (ManyToOne)

### PointUrlCallLog
포인트 URL 호출 로그를 저장합니다.

**주요 필드**:
- `userName`: 사용자명
- `siteName`: 사이트명
- `cookie`: 쿠키 문자열
- `pointUrl`: 호출 URL
- `responseStatusCode`: HTTP 상태 코드
- `responseHeader`: 응답 헤더
- `responseBody`: 응답 본문

## 리포지토리 설명

### CookieRepository
```java
public interface CookieRepository extends JpaRepository<Cookie, Long> {
    List<Cookie> findBySiteNameIgnoreCaseAndIsValid(String siteName, Boolean isValid);
    List<Cookie> findBySiteNameAndIsValid(String siteName, Boolean isValid);

    @EntityGraph(attributePaths = {"siteUser"})
    List<Cookie> findAllBySiteUser_Id(Long siteUserId);
}
```

### PointUrlRepository
```java
public interface PointUrlRepository extends JpaRepository<PointUrl, Long> {
    List<PointUrl> findByUrl(String url);

    @Query("복잡한 쿼리...")
    List<PointUrl> findByNotCalledUrl(
        @Param("siteName") String siteName,
        @Param("userName") String userName
    );

    List<PointUrl> findByCreatedDateBetween(
        LocalDateTime startTime,
        LocalDateTime endTime
    );
}
```

### SavedPointRepository
Spring Data JPA 인터페이스와 QueryDSL 커스텀 구현을 조합합니다.

```java
public interface SavedPointRepository extends
    JpaRepository<SavedPoint, Long>,
    SavedPointRepositoryCustom {
    List<SavedPoint> findAllByCreatedDateBetween(
        LocalDateTime with,
        LocalDateTime with1
    );
}
```

### SavedPointRepositoryImpl (QueryDSL)
N+1 쿼리 문제를 해결한 최적화된 쿼리를 제공합니다.

```java
public List<SavedPoint> findBySiteUser(Long siteUserId, int dayBefore) {
    return jpaQueryFactory.selectFrom(savedPoint)
        .leftJoin(savedPoint.cookie, cookie1).fetchJoin()
        .leftJoin(cookie1.siteUser, siteUser).fetchJoin()
        .where(...)
        .fetch();
}
```

## 설정 클래스

### CrawlerProperties
크롤링 관련 설정을 관리합니다.

```java
@ConfigurationProperties(prefix = "crawler")
public class CrawlerProperties {
    private int timeout = 10000;  // HTTP 연결 타임아웃
}
```

### NaverPointProperties
네이버 포인트 관련 설정을 관리합니다.

```java
@ConfigurationProperties(prefix = "naver.point")
public class NaverPointProperties {
    private String saveKeyword = "적립";
    private String invalidCookieKeyword = "로그인이 필요";
    private String amountPattern = "\\s\\d+원이 적립 됩니다.";
    private String userAgent = "Mozilla/5.0...";
}
```

### ScheduleProperties
스케줄러 설정을 관리합니다.

```java
@ConfigurationProperties(prefix = "schedule")
public class ScheduleProperties {
    private long crawlerFixedDelay = 300000;     // 5분
    private long pointFixedDelay = 300000;       // 5분
    private String dailyReportCron = "0 0 7 * * *";  // 오전 7시
}
```

## 추상 클래스

### AbstractWebCrawler
모든 웹 크롤러의 기본 클래스로 공통 크롤링 로직을 제공합니다.

**템플릿 메서드**:
- `crawling()`: 전체 크롤링 프로세스 실행

**추상 메서드** (하위 클래스에서 구현):
- `getSiteData()`: 사이트 정보 반환
- `getArticleSelector()`: CSS 셀렉터 반환
- `fetchPostUrls()`: 게시글 URL 목록 조회

**주요 로직**:
1. 사이트 정보 조회
2. 게시글 URL 목록 수집
3. 게시글 본문에서 포인트 URL 추출
4. 네이버 포인트 URL 필터링
5. PointUrl 엔티티 생성

**코드 중복 감소 효과**: 90줄 -> 37줄 (59% 감소)

### BaseCookieService
쿠키 관련 공통 서비스 로직을 제공합니다.

**제공 메서드**:
- `getCookie(Long cookieId)`: ID로 쿠키 조회
- `findAllBySiteUserId(Long siteUserId)`: 사용자별 쿠키 목록

**템플릿 메서드**:
- `onCookieInvalidated(Cookie cookie)`: 쿠키 무효화 시 호출 (오버라이드 가능)

## 예외 클래스

### CookieNotFoundException
쿠키를 찾을 수 없을 때 발생합니다.

```java
public class CookieNotFoundException extends RuntimeException {
    public CookieNotFoundException(Long cookieId) {
        super("Cookie not found: " + cookieId);
    }
}
```

### InvalidCookieException
쿠키가 유효하지 않을 때 발생합니다.

```java
public class InvalidCookieException extends RuntimeException {
    public InvalidCookieException(String userName) {
        super("Invalid cookie for user: " + userName);
    }

    public InvalidCookieException(String message, Throwable cause) {
        super(message, cause);
    }
}
```

### PointExchangeException
포인트 교환 중 오류 발생 시 사용됩니다.

```java
public class PointExchangeException extends RuntimeException {
    public PointExchangeException(String message, Throwable cause) {
        super(message, cause);
    }
}
```

## 공통 설정

### application-common.yml

```yaml
crawler:
  timeout: 10000
  retry-count: 3

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

## 성능 최적화

### 1. 데이터베이스 인덱스
성능 향상을 위해 자주 조회되는 컬럼에 인덱스를 추가했습니다.

- Cookie: (siteName, isValid), (site_user_id)
- SavedPoint: (created_date)
- PointUrl: (name, permanent), (created_date)

### 2. N+1 쿼리 해결
fetchJoin과 @EntityGraph를 사용하여 N+1 쿼리 문제를 해결했습니다.

**SavedPointRepositoryImpl**:
```java
.leftJoin(savedPoint.cookie, cookie1).fetchJoin()
.leftJoin(cookie1.siteUser, siteUser).fetchJoin()
```

**CookieRepository**:
```java
@EntityGraph(attributePaths = {"siteUser"})
List<Cookie> findAllBySiteUser_Id(Long siteUserId);
```

### 3. QueryDSL 쿼리 최적화
복잡한 쿼리는 QueryDSL로 구현하여 타입 안정성과 가독성을 확보했습니다.

## 사용 방법

### pickup-common을 다른 모듈에서 사용

**build.gradle**:
```gradle
dependencies {
    implementation project(':pickup-common')
}
```

### 엔티티 사용 예시

```java
// 쿠키 생성
Cookie cookie = Cookie.builder()
    .siteUser(siteUser)
    .userName("user@example.com")
    .siteName("CLIEN")
    .cookie("JSESSIONID=...")
    .isValid(true)
    .build();

// 포인트 URL 생성
PointUrl pointUrl = PointUrl.builder()
    .url("https://naver.com/point/123")
    .permanent(false)
    .build();

// 포인트 적립 기록
SavedPoint savedPoint = SavedPoint.builder()
    .cookie(cookie)
    .amount(10)
    .responseBody("10원이 적립 됩니다.")
    .build();
```

### 리포지토리 사용 예시

```java
@Service
public class MyService {
    private final CookieRepository cookieRepository;
    private final SavedPointRepository savedPointRepository;

    // 유효한 쿠키 조회
    List<Cookie> validCookies =
        cookieRepository.findBySiteNameAndIsValid("CLIEN", true);

    // 사용자별 포인트 조회
    List<SavedPoint> points =
        savedPointRepository.findBySiteUser(userId, 7);  // 최근 7일
}
```

### 추상 클래스 확장 예시

```java
@Component
public class NewSiteCrawler extends AbstractWebCrawler {
    @Override
    protected SiteData getSiteData() {
        return new NewSiteData();
    }

    @Override
    protected String getArticleSelector() {
        return "div.content a";
    }

    @Override
    protected Set<String> fetchPostUrls(String siteUrl) throws IOException {
        // 사이트별 구현
    }
}
```

## 빌드

```bash
# 공통 모듈만 빌드
./gradlew :pickup-common:build

# 테스트 제외 빌드
./gradlew :pickup-common:build -x test
```

## 의존성

주요 의존성은 `build.gradle` 참조:
- Spring Boot Starter
- Spring Boot Starter Data JPA
- Spring Boot Configuration Processor
- QueryDSL
- Jsoup
- Lombok
- MySQL Connector
- H2 Database (테스트)

## 참고 사항

### 1. 공통 모듈 변경 시 주의사항
pickup-common을 수정하면 admin-server와 pickup-server 모두에 영향을 줍니다. 변경 후 반드시 전체 빌드 및 테스트를 수행하세요.

```bash
./gradlew clean build
```

### 2. 엔티티 변경 시
엔티티 구조를 변경하면 데이터베이스 마이그레이션이 필요할 수 있습니다. 프로덕션 환경에서는 `ddl-auto: validate`를 사용하고 수동 마이그레이션을 권장합니다.

### 3. 인덱스 추가 시
인덱스는 조회 성능을 향상시키지만 쓰기 성능에는 영향을 줄 수 있습니다. 실제 데이터 패턴을 분석한 후 추가하세요.

## 참고 문서

- [Spring Data JPA Reference](https://docs.spring.io/spring-data/jpa/reference/)
- [QueryDSL Documentation](http://querydsl.com/static/querydsl/latest/reference/html/)
- [Hibernate ORM Documentation](https://hibernate.org/orm/documentation/)
