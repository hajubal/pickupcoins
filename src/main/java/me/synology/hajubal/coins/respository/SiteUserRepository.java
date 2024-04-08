package me.synology.hajubal.coins.respository;

import me.synology.hajubal.coins.entity.SiteUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SiteUserRepository extends JpaRepository<SiteUser, Long> {

    Optional<SiteUser> findByLoginId(String loginId);

    List<SiteUser> findAllByActiveIsTrue();
}
