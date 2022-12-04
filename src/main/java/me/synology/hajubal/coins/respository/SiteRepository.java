package me.synology.hajubal.coins.respository;

import me.synology.hajubal.coins.entity.SiteData;

import java.util.List;

public interface SiteRepository {

    void save(SiteData siteData);

    List<SiteData> findAll();
}
