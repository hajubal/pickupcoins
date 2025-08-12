package me.synology.hajubal.coins.respository;

import me.synology.hajubal.coins.entity.Cookie;
import me.synology.hajubal.coins.entity.SavedPoint;
import me.synology.hajubal.coins.entity.SiteUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Import(TestJpaConfig.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class SavedPointRepositoryTest {

    @Autowired
    private SavedPointRepository savedPointRepository;

    @Autowired
    private SiteUserRepository siteUserRepository;

    @Autowired
    private CookieRepository cookieRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Transactional
    @BeforeEach
    void setUp() {
        cookieRepository.deleteAll();
        siteUserRepository.deleteAll();
    }

    @Test
    void findBySiteUser() {
        //given
        SiteUser siteUser = createSiteUser();

        createSavedPointWithCreatedDate(siteUser, LocalDateTime.now().minusDays(1));
        createSavedPointWithCreatedDate(siteUser, LocalDateTime.now().minusDays(1));

        //when
        List<SavedPoint> points = savedPointRepository.findBySiteUser(siteUser.getId(), 1);

        //then
        assertThat(points).hasSize(2);
    }

    private void createSavedPointWithCreatedDate(SiteUser siteUser, LocalDateTime createdDate) {
        Cookie cookie = Cookie.builder()
                .siteUser(siteUser)
                .cookie("cookie")
                .siteName("naver")
                .userName("user")
                .build();

        cookieRepository.save(cookie);

        jdbcTemplate.update("insert into SAVED_POINT (AMOUNT, COOKIE_ID, CREATED_DATE) values ( ?, ?, ? )"
                , 200, cookie.getId(), createdDate);
    }

    private SiteUser createSiteUser() {
        SiteUser siteUser = SiteUser.builder()
                .userName("name")
                .password("password")
                .loginId("loginId")
                .build();

        return siteUserRepository.save(siteUser);
    }
}