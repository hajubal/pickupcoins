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
import org.springframework.test.util.ReflectionTestUtils;

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

    @Test
    void reportTest() {
        //init.sql 에 저장된 데이터 삭제
        cookieRepository.deleteAll();
        pointUrlRepository.deleteAll();
        siteUserRepository.deleteAll();
        entityManager.flush();

        //given
        PointUrl pointUrl = PointUrl.builder().url("url").build();
        //point url 생성 날짜를 하루 전으로 설정
        ReflectionTestUtils.setField(pointUrl, "createdDate", LocalDateTime.now().minusDays(1));
        entityManager.persist(pointUrl);
        entityManager.flush();

        SiteUser siteUser = SiteUser.builder().loginId("loginId").userName("name").password("pw").slackWebhookUrl("url").build();
        siteUserRepository.save(siteUser);

        Cookie validCookie = Cookie.builder().userName("user1").siteName("naver").cookie("validCookie").siteUser(siteUser).isValid(Boolean.TRUE).build();
        cookieRepository.save(validCookie);

        Cookie invalidCookie = Cookie.builder().userName("user2").siteName("naver").cookie("invalidCookie").siteUser(siteUser).isValid(Boolean.FALSE).build();
        cookieRepository.save(invalidCookie);

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
