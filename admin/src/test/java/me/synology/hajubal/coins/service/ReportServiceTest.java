package me.synology.hajubal.coins.service;

import me.synology.hajubal.coins.entity.Cookie;
import me.synology.hajubal.coins.entity.SiteUser;
import me.synology.hajubal.coins.respository.CookieRepository;
import me.synology.hajubal.coins.respository.PointUrlRepository;
import me.synology.hajubal.coins.respository.SavedPointRepository;
import me.synology.hajubal.coins.respository.SiteUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDateTime;

import static org.mockito.Mockito.verify;

@SpringBootTest
public class ReportServiceTest {

    @Autowired
    private ReportService reportService;

    @Autowired
    private PointUrlRepository pointUrlRepository;

    @Autowired
    private CookieRepository cookieRepository;

    @Autowired
    private SiteUserRepository siteUserRepository;

    @Autowired
    private SavedPointRepository savedPointRepository;

    @MockBean
    private SlackService slackService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setup() {
        //init.sql 에 저장된 데이터 삭제
        cookieRepository.deleteAll();
        pointUrlRepository.deleteAll();
        siteUserRepository.deleteAll();
        savedPointRepository.deleteAll();
    }

    @Test
    void reportTest() {
        //given
        /**
         * FIXME 해당 테스트를 단독으로 실행 했을 때는 TestEntityManager 사용시 @EnableJpaAuditing 작동하지 않아 아래 로직으로
         * create_date를 수동으로 변경할 수 있으나 다른 @SpringBootTest를 사용하는 다른 테스트 들과 같이 실행될 경우에는
         * @EnableJpaAuditing 동작하는지 create_date 컬럼을 jpa를 이용해서는 변경할 수 가 없다.
         */
//        PointUrl pointUrl = PointUrl.builder().url("url").build();
//        ReflectionTestUtils.setField(pointUrl, "createdDate", LocalDateTime.now().minusDays(1));
//        entityManager.persist(pointUrl);
//        entityManager.flush();

        //point url 생성 날짜를 하루 전으로 설정
        jdbcTemplate.update("insert into POINT_URL (ID, NAME, URL, CREATED_DATE) values ( ?, ?, ?, ?)"
                , 1L, "name", "url", LocalDateTime.now().minusDays(1));

        SiteUser siteUser = createSiteUser();

        Cookie validCookie1 = createValidCookie("user1", siteUser);
        Cookie validCookie2 = createValidCookie("user2", siteUser);
        createInValidCookie("user3", siteUser);

        jdbcTemplate.update("insert into SAVED_POINT (AMOUNT, COOKIE_ID, CREATED_DATE) values (?, ?, ? )"
                , 200, validCookie1.getId(), LocalDateTime.now().minusDays(1));

        jdbcTemplate.update("insert into SAVED_POINT (AMOUNT, COOKIE_ID, CREATED_DATE) values (?, ?, ? )"
                , 200, validCookie2.getId(), LocalDateTime.now().minusDays(1));

        //when
        reportService.report();

        ReportService.MessageBuilder builder = ReportService.MessageBuilder.builder()
                .urlCount(1)
                .totalCookieCount(3)
                .logoutCookieCount(1)
                .successCount(2)
                .amount(400)
                .build();

        builder.addCookieAmount(validCookie1.getUserName(), 200);
        builder.addCookieAmount(validCookie2.getUserName(), 200);

        String message = builder.format();

        //then
        verify(slackService).sendMessage(siteUser.getSlackWebhookUrl(), message);
    }

    private SiteUser createSiteUser() {
        SiteUser siteUser = SiteUser.builder().loginId("loginId").userName("name").password("pw").slackWebhookUrl("url").build();
        return siteUserRepository.save(siteUser);
    }

    private Cookie createValidCookie(String cookieName, SiteUser siteUser) {
        Cookie validCookie1 = Cookie.builder().userName(cookieName).siteName("naver").cookie("validCookie").siteUser(siteUser).isValid(Boolean.TRUE).build();
        return cookieRepository.save(validCookie1);
    }

    private Cookie createInValidCookie(String cookieName, SiteUser siteUser) {
        Cookie validCookie1 = Cookie.builder().userName(cookieName).siteName("naver").cookie("invalidCookie").siteUser(siteUser).isValid(Boolean.FALSE).build();
        return cookieRepository.save(validCookie1);
    }
}
