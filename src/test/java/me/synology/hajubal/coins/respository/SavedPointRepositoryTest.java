package me.synology.hajubal.coins.respository;

import me.synology.hajubal.coins.entity.PointUrlUserCookie;
import me.synology.hajubal.coins.entity.SavedPoint;
import me.synology.hajubal.coins.entity.UserCookie;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
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

    @Test
    void deleteTest() {
        UserCookie userCookie = UserCookie.builder().userName("test").siteName("test").isValid(Boolean.TRUE).build();
        userCookie.setSavedPoint(List.of(SavedPoint.builder().userCookie(userCookie).build()));
        userCookie.setPointUrlUserCookie(List.of(PointUrlUserCookie.builder().userCookie(userCookie).build()));

        userCookieRepository.save(userCookie);

        //?? 이게 왜 주소값이 다르지???
        assertThat(userCookie).isEqualTo(userCookieRepository.findById(userCookie.getId()).get());

//        assertThat(userCookie.getSavedPoint());



    }
}