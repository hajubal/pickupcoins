package me.synology.hajubal.coins.respository;

import me.synology.hajubal.coins.entity.PointUrl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PointUrlRepository extends JpaRepository<PointUrl, Long> {
    List<PointUrl> findByUrl(String url);
}
