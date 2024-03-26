package me.synology.hajubal.coins.respository;

import me.synology.hajubal.coins.entity.SavedPoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SavedPointRepository extends JpaRepository<SavedPoint, Long> {
    List<SavedPoint> findAllByCreatedDateBetween(LocalDateTime with, LocalDateTime with1);
}
