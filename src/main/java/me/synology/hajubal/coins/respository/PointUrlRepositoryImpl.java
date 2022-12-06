package me.synology.hajubal.coins.respository;

import me.synology.hajubal.coins.entity.PointUrlData;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class PointUrlRepositoryImpl implements PointUrlRepository {

    Map<Long, PointUrlData> data = new ConcurrentHashMap<>();

    @Override
    public void save(PointUrlData pointUrlData) {
        Optional<Long> max = data.keySet().stream().max(Long::compareTo);

        Long id = 0l;

        if(max.isPresent()) {
            id = max.get() + 1l;
        }

        pointUrlData.setId(id);

        data.put(id, pointUrlData);
    }

    @Override
    public List<PointUrlData> findAll() {
        return data.values().stream().toList();
    }

    @Override
    public Optional<PointUrlData> findByUrl(String url) {
        return data.values().stream().filter(pointUrlData -> pointUrlData.getUrl().equals(url)).findFirst();
    }
}
