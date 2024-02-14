package me.synology.hajubal.coins.respository;

import me.synology.hajubal.coins.entity.UserCookie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserCookieRepository extends JpaRepository<UserCookie, Long> {

    boolean existsByUserName(String userName);

    List<UserCookie> findBySiteNameAndIsValid(String siteName, Boolean isValid);

    Optional<UserCookie> findByUserNameAndSiteName(String userName, String siteName);

}
