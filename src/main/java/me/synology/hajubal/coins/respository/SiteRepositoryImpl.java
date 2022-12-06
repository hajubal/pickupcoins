package me.synology.hajubal.coins.respository;

import me.synology.hajubal.coins.entity.SiteData;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class SiteRepositoryImpl implements SiteRepository {

    private Map<Long, SiteData> data = new ConcurrentHashMap<>();

    @Override
    public void save(SiteData siteData) {
        Optional<Long> max = data.keySet().stream().max(Long::compareTo);

        Long id = 0l;

        if(max.isPresent()) {
            id = max.get() + 1l;
        }

        siteData.setId(id);

        data.put(id, siteData);
    }

    @Override
    public List<SiteData> findAll() {
        return data.values().stream().toList();
    }
}
