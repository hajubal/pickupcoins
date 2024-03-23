package me.synology.hajubal.coins.service;

import me.synology.hajubal.coins.entity.Cookie;
import me.synology.hajubal.coins.entity.PointUrl;
import me.synology.hajubal.coins.entity.SiteUser;
import me.synology.hajubal.coins.respository.CookieRepository;
import me.synology.hajubal.coins.respository.PointUrlRepository;
import me.synology.hajubal.coins.respository.SiteUserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.auditing.AuditingHandler;
import org.springframework.data.auditing.config.AuditingBeanDefinitionRegistrarSupport;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.mockito.Mockito.verify;

@DataJpaTest
@Import(ReportService.class)
public class ReportServiceTest {

    @Autowired
    private ReportService reportService;

    @Autowired
    private PointUrlRepository pointUrlRepository;

    @Autowired
    private CookieRepository cookieRepository;

    @Autowired
    private SiteUserRepository siteUserRepository;

    @MockBean
    private SlackService slackService;

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Transactional
    @Test
    void reportTest() {
        //init.sql 에 저장된 데이터 삭제
        cookieRepository.deleteAll();
        pointUrlRepository.deleteAll();
        siteUserRepository.deleteAll();
        entityManager.flush();

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

        SiteUser siteUser = SiteUser.builder().loginId("loginId").userName("name").password("pw").slackWebhookUrl("url").build();
        siteUserRepository.save(siteUser);

        Cookie validCookie = Cookie.builder().userName("user1").siteName("naver").cookie("validCookie").siteUser(siteUser).isValid(Boolean.TRUE).build();
        cookieRepository.save(validCookie);

        Cookie invalidCookie = Cookie.builder().userName("user2").siteName("naver").cookie("invalidCookie").siteUser(siteUser).isValid(Boolean.FALSE).build();
        cookieRepository.save(invalidCookie);

        pointUrlRepository.findAll().stream().forEach(pointUrl1 -> System.out.println("pointUrl1 = " + pointUrl1));

        //when
        reportService.report();

        String message = ReportService.MessageBuilder.builder()
                .urlCount(1)
                .totalCookieCount(2)
                .logoutCookieCount(1)
                .successCount(0)
                .build().format();

        //then
        verify(slackService).sendMessage(siteUser.getSlackWebhookUrl(), message);
    }

}
