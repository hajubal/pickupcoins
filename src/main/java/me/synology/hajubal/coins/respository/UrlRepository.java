package me.synology.hajubal.coins.respository;

import me.synology.hajubal.coins.entity.UrlData;

import java.util.List;

public interface UrlRepository {
    void save(UrlData urlData);

    List<UrlData> findAll();
}
