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

@ExtendWith(SpringExtension.class)
@DataJpaTest
class PointUrlCookieRepositoryTest {

    @Autowired
    private PointUrlCookieRepository pointUrlCookieRepository;

    @DisplayName("point url save 테스트")
    @Test
    void saveTest() {
        PointUrl pointUrl = PointUrl.builder()
                .url("url")
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

        PointUrlCookie savedData = pointUrlCookieRepository.save(pointUrlCookie);

        Assertions.assertThat(pointUrlCookie).isEqualTo(savedData);
    }
}