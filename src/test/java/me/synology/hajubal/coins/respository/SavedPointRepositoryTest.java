package me.synology.hajubal.coins.respository;

import me.synology.hajubal.coins.entity.PointUrlUserCookie;
import me.synology.hajubal.coins.entity.SavedPoint;
import me.synology.hajubal.coins.entity.UserCookie;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class SavedPointRepositoryTest {

    @Autowired
    private SavedPointRepository savedPointRepository;

    @Autowired
    private UserCookieRepository userCookieRepository;

    @Test
    void allTest() {

        List<SavedPoint> all = savedPointRepository.findAll();

        all.forEach(System.out::println);
    }

    @Test
    void userCookieTest() {
        List<UserCookie> all = userCookieRepository.findAll();

        all.forEach(System.out::println);
    }

}