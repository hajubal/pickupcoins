package me.synology.hajubal.coins.respository;

import me.synology.hajubal.coins.entity.PointUrlData;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class PointUrlRepositoryImpl implements PointUrlRepository {

    Map<Long, PointUrlData> data = new ConcurrentHashMap<>();

    @Override
    public void save(PointUrlData pointUrlData) {
        data.put(pointUrlData.getId(), pointUrlData);
    }

    @Override
    public List<PointUrlData> findAll() {
        return data.values().stream().toList();
    }

    @Override
    public PointUrlData findByUrl(String url) {
        return data.values().stream().filter(pointUrlData -> pointUrlData.getUrl().equals(url)).findFirst().orElseThrow(new IllegalArgumentException("Not found point url data."));
    }
}
