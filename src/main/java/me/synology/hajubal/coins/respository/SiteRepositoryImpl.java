package me.synology.hajubal.coins.respository;

import me.synology.hajubal.coins.entity.SiteData;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class SiteRepositoryImpl implements SiteRepository {

    private Map<Long, SiteData> data = new ConcurrentHashMap<>();

    @Override
    public void save(SiteData siteData) {
        data.put(siteData.getId(), siteData);
    }

    @Override
    public List<SiteData> findAll() {
        return data.values().stream().toList();
    }
}
