package me.synology.hajubal.coins.respository;

import me.synology.hajubal.coins.entity.CookieData;

import java.util.List;
import java.util.Optional;

public interface CookieRepository {
    Optional<CookieData> findById(Long id);

    List<CookieData> findAll();

    void save(CookieData cookieData);
}
