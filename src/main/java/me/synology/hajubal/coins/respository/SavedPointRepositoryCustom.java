package me.synology.hajubal.coins.respository;

import me.synology.hajubal.coins.entity.SavedPoint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SavedPointRepositoryCustom {

    List<SavedPoint> findBySiteUser(Long siteUserId, int dayBefore);

    Page<SavedPoint> findAllBySiteUser(Long siteUserId, Pageable pageable);
}
