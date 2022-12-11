package me.synology.hajubal.coins.respository;

import me.synology.hajubal.coins.entity.PointUrlCallHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PointUrlCallHistoryRepository extends JpaRepository<PointUrlCallHistory, Long> {

    Optional<PointUrlCallHistory> findByPointUrlAndUserName(String pointUrl, String userName);
}
