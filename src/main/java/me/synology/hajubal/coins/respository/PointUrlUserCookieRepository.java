package me.synology.hajubal.coins.respository;

import me.synology.hajubal.coins.entity.PointUrl;
import me.synology.hajubal.coins.entity.PointUrlUserCookie;
import me.synology.hajubal.coins.entity.UserCookie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PointUrlUserCookieRepository extends JpaRepository<PointUrlUserCookie, Long> {

    Optional<PointUrlUserCookie> findByPointUrl_UrlAndUserCookieUserName(String pointUrl, String userName);

    Optional<PointUrlUserCookie> findByPointUrlAndUserCookie(PointUrl pointUrl, UserCookie userCookie);
}
