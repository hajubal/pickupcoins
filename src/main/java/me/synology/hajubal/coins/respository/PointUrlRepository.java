package me.synology.hajubal.coins.respository;

import me.synology.hajubal.coins.entity.PointUrlData;

import java.util.List;
import java.util.Optional;

public interface PointUrlRepository {
    void save(PointUrlData pointUrlData);

    List<PointUrlData> findAll();

    Optional<PointUrlData> findByUrl(String url);
}
