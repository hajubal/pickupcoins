package me.synology.hajubal.coins.respository;

import me.synology.hajubal.coins.entity.PointUrlCookie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PointUrlUserCookieRepository extends JpaRepository<PointUrlCookie, Long> {
}
