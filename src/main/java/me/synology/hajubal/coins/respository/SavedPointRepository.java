package me.synology.hajubal.coins.respository;

import me.synology.hajubal.coins.entity.SavedPoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SavedPointRepository extends JpaRepository<SavedPoint, Long> {
}
