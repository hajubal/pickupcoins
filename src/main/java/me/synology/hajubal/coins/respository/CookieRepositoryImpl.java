package me.synology.hajubal.coins.respository;

import me.synology.hajubal.coins.entity.CookieData;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class CookieRepositoryImpl implements CookieRepository {

    Map<Long, CookieData> data = new ConcurrentHashMap<>();

    @Override
    public Optional<CookieData> findById(Long id) {
        return Optional.of(data.get(id));
    }

    @Override
    public List<CookieData> findAll() {
        return data.values().stream().toList();
    }

    @Override
    public void save(CookieData cookieData) {
        data.put(cookieData.getId(), cookieData);
    }
}
