# 로깅 가이드라인

## 로그 레벨 사용 기준

### ERROR
- 시스템 오류, 예외 발생 시
- 즉각적인 조치가 필요한 상황
- 예: 외부 API 호출 실패, 데이터베이스 연결 오류

```java
log.error("Failed to exchange point. url: {}, user: {}", url, userName, exception);
```

### WARN
- 잠재적 문제 상황
- 시스템은 정상 동작하지만 주의가 필요한 경우
- 예: 쿠키 무효화, 예상치 못한 응답

```java
log.warn("Cookie is invalid. user: {}", userName);
log.warn("Failed to extract amount from response body: {}", body);
```

### INFO
- 중요한 비즈니스 이벤트
- 시스템 상태 변경
- 예: 포인트 적립 성공, 크롤링 완료

```java
log.info("Point saved. user: {}, amount: {}원", userName, amount);
log.info("Crawling completed. site: {}, posts: {}, points: {}", siteName, postCount, pointCount);
```

### DEBUG
- 상세한 디버깅 정보
- 개발/테스트 환경에서 주로 사용
- 프로덕션에서는 비활성화 권장

```java
log.debug("Calling point URL. url: {}, user: {}", url, userName);
log.debug("Found point URL: {}", href);
```

## 로그 메시지 포맷 규칙

### 1. 일관된 구조 사용
```java
// 패턴: [동작]. [주요 속성들]
log.info("Point exchange completed. user: {}, url: {}", userName, url);
log.info("Crawling completed. site: {}, posts: {}, points: {}", siteName, postCount, pointCount);
```

### 2. 변수 치환 사용 (문자열 연결 지양)
```java
// Good
log.error("Failed to process. id: {}", id, exception);

// Bad
log.error("Failed to process. id: " + id);
```

### 3. 예외는 마지막 파라미터로
```java
log.error("Failed to exchange point. url: {}, user: {}", url, userName, exception);
```

### 4. 민감 정보 로깅 금지
- 비밀번호, 쿠키 값, 개인정보는 로깅하지 않음
- 필요시 마스킹 처리

```java
// Bad
log.info("Cookie value: {}", cookie);

// Good
log.info("Cookie updated. user: {}", userName);
```

## 로그 레벨별 운영 환경 설정

### application.yml (프로덕션)
```yaml
logging:
  level:
    root: INFO
    me.synology.hajubal.coins: INFO
    org.springframework: WARN
    org.hibernate: WARN
```

### application-dev.yml (개발)
```yaml
logging:
  level:
    root: DEBUG
    me.synology.hajubal.coins: DEBUG
    org.springframework: INFO
    org.hibernate: DEBUG
    org.hibernate.SQL: DEBUG
    org.hibernate.type.descriptor.sql.BasicBinder: TRACE
```

## 구조화된 로깅

### 주요 이벤트 로깅 체크리스트
- [ ] 시작/완료 로그 쌍으로 기록
- [ ] 실행 시간 측정이 필요한 경우 StopWatch 사용
- [ ] 중요 비즈니스 메트릭 포함 (포인트 금액, URL 개수 등)
- [ ] 오류 발생 시 충분한 컨텍스트 정보 포함

### 예시
```java
@Transactional
public void exchange(PointUrl url, ExchangeDto exchangeDto) {
    log.debug("Calling point URL. url: {}, user: {}", url.getUrl(), exchangeDto.userName());

    try {
        // 비즈니스 로직
        log.info("Point exchange completed. user: {}, url: {}", exchangeDto.userName(), url.getUrl());
    } catch (Exception e) {
        log.error("Failed to exchange point. url: {}, user: {}", url.getUrl(), exchangeDto.userName(), e);
        throw new PointExchangeException("Failed to exchange point for url: " + url.getUrl(), e);
    }
}
```

## 성능 고려사항

### 1. 로그 레벨 체크
```java
// 복잡한 연산이 필요한 경우
if (log.isDebugEnabled()) {
    log.debug("Complex calculation result: {}", complexCalculation());
}
```

### 2. 불필요한 객체 생성 피하기
```java
// Good - 변수 치환 사용
log.info("Processing. id: {}, name: {}", id, name);

// Bad - 문자열 연결
log.info("Processing. id: " + id + ", name: " + name);
```

## 프로젝트별 로깅 예시

### 크롤링 (WebCrawler)
```java
log.info("Starting crawling. site: {}", siteName);
log.debug("Fetching post URLs from: {}", siteUrl);
log.debug("Found point URL: {}", href);
log.info("Crawling completed. site: {}, posts: {}, points: {}", siteName, postCount, pointCount);
log.error("Failed to crawl URL: {}", siteUrl, exception);
```

### 포인트 적립 (ExchangeService)
```java
log.debug("Calling point URL. url: {}, user: {}", url, userName);
log.info("Point saved. user: {}, amount: {}원", userName, amount);
log.warn("Cookie is invalid. user: {}", userName);
log.error("Failed to exchange point. url: {}, user: {}", url, userName, exception);
```

### Slack 알림 (SlackService)
```java
log.info("Sending Slack message. user: {}", userName);
log.warn("Slack webhook URL is empty. user: {}", userName);
log.error("Failed to send Slack message. user: {}", userName, exception);
```

### 스케줄러 (Schedulers)
```java
log.info("Starting web crawler scheduler");
log.info("Starting naver point save scheduler");
log.info("Sending daily report. date: {}", LocalDate.now());
log.error("Scheduler execution failed", exception);
```

### 쿠키 관리 (CookieService)
```java
log.info("Cookie updated. user: {}, site: {}", userName, siteName);
log.warn("Cookie invalidated. user: {}, site: {}", userName, siteName);
log.error("Failed to update cookie. id: {}", cookieId, exception);
```
