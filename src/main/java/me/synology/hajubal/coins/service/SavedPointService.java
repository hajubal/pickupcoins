package me.synology.hajubal.coins.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.synology.hajubal.coins.entity.PointUrl;
import me.synology.hajubal.coins.entity.SavedPoint;
import me.synology.hajubal.coins.respository.PointUrlRepository;
import me.synology.hajubal.coins.respository.SavedPointRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class SavedPointService {

    private final SavedPointRepository savedPointRepository;

    /**
     * 몇일 전부터 오늘 까지 수집한 point
     *
     * @param dayBefore 몇일 전
     * @return 수집한 point
     */
    public List<SavedPoint> findSavedPoint(int dayBefore) {
        return savedPointRepository.findAllByCreatedDateBetween(
                LocalDateTime.now().minusDays(dayBefore).with(LocalTime.MIN),
                LocalDateTime.now().with(LocalTime.MIN));
    }

}
