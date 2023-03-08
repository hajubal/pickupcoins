package me.synology.hajubal.coins.respository;

import me.synology.hajubal.coins.entity.PointUrlCallLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PointUrlCallLogRepository extends JpaRepository<PointUrlCallLog, Long> {
    List<PointUrlCallLog> findByUserNameIn(String... userName);


}
