package me.synology.hajubal.coins.service;

import me.synology.hajubal.coins.controller.dto.CookieUpdateDto;
import me.synology.hajubal.coins.entity.UserCookie;
import me.synology.hajubal.coins.respository.UserCookieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class UserCookieService {

    @Autowired
    private UserCookieRepository userCookieRepository;

    public void updateUserCookie(CookieUpdateDto cookieUpdateDto) {
        UserCookie userCookie = userCookieRepository.findByUserNameAndSiteName(cookieUpdateDto.getUserName(), cookieUpdateDto.getSiteName())
                .orElseThrow(() -> new IllegalArgumentException("Not found user"));

        userCookie.setCookie(cookieUpdateDto.getCookie());
    }
}
