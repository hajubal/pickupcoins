package me.synology.hajubal.coins.respository;

import me.synology.hajubal.coins.entity.PointUrl;
import me.synology.hajubal.coins.entity.PointUrlUserCookie;
import me.synology.hajubal.coins.entity.UserCookie;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class PointUrlUserCookieRepositoryTest {

    @Autowired
    private PointUrlUserCookieRepository pointUrlUserCookieRepository;

    @Transactional
    @Test
    void saveTest() {
        PointUrl pointUrl = PointUrl.builder().url("url").name("name").build();
        UserCookie userCookie = UserCookie.builder().cookie("cookie").userName("name").siteName("site").isValid(Boolean.TRUE).build();

        PointUrlUserCookie pointUrlUserCookie = PointUrlUserCookie.builder().pointUrl(pointUrl).userCookie(userCookie).build();

        PointUrlUserCookie savedData = pointUrlUserCookieRepository.save(pointUrlUserCookie);

        Assertions.assertThat(pointUrlUserCookie).isEqualTo(savedData);
    }
}