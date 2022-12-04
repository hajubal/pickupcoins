package me.synology.hajubal.coins.respository;

import me.synology.hajubal.coins.entity.UrlData;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class UrlRepositoryImpl implements UrlRepository {

    Map<Long, UrlData> data = new ConcurrentHashMap<>();

    @Override
    public void save(UrlData urlData) {
        data.put(urlData.getId(), urlData);
    }

    @Override
    public List<UrlData> findAll() {
        return data.values().stream().toList();
    }
}
