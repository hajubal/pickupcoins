package me.synology.hajubal.coins.respository;

import me.synology.hajubal.coins.entity.Cookie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CookieRepository extends JpaRepository<Cookie, Long> {

    List<Cookie> findBySiteNameIgnoreCaseAndIsValid(String siteName, Boolean isValid);

}
