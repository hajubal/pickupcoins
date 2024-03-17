package me.synology.hajubal.coins.respository;

import me.synology.hajubal.coins.entity.PointUrl;
import me.synology.hajubal.coins.entity.PointUrlCookie;
import me.synology.hajubal.coins.entity.Cookie;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class PointUrlCookieRepositoryTest {

    @Autowired
    private PointUrlUserCookieRepository pointUrlUserCookieRepository;

    @DisplayName("point url save 테스트")
    @Test
    void saveTest() {
        PointUrl pointUrl = PointUrl.builder()
                .url("url")
                .name("name")
                .build();

        Cookie cookie = Cookie.builder()
                .cookie("cookie")
                .userName("name")
                .siteName("site")
                .isValid(Boolean.TRUE)
                .build();

        PointUrlCookie pointUrlCookie = PointUrlCookie.builder()
                .pointUrl(pointUrl)
                .cookie(cookie)
                .build();

        PointUrlCookie savedData = pointUrlUserCookieRepository.save(pointUrlCookie);

        Assertions.assertThat(pointUrlCookie).isEqualTo(savedData);
    }
}