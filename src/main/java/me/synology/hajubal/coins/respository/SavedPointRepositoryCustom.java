package me.synology.hajubal.coins.respository;

import me.synology.hajubal.coins.entity.SavedPoint;

import java.util.List;

public interface SavedPointRepositoryCustom {

    List<SavedPoint> findBySiteUser(Long siteUserId, int dayBefore);
}
