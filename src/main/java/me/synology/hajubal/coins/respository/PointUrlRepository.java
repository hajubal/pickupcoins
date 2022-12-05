package me.synology.hajubal.coins.respository;

import me.synology.hajubal.coins.entity.PointUrlData;

import java.util.List;

public interface PointUrlRepository {
    void save(PointUrlData pointUrlData);

    List<PointUrlData> findAll();

    PointUrlData findByUrl(String url);
}
