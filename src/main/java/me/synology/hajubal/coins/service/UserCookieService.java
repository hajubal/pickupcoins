package me.synology.hajubal.coins.service;

import lombok.extern.slf4j.Slf4j;
import me.synology.hajubal.coins.controller.dto.CookieUpdateDto;
import me.synology.hajubal.coins.entity.UserCookie;
import me.synology.hajubal.coins.respository.UserCookieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional
@Service
public class UserCookieService {

    @Autowired
    private UserCookieRepository userCookieRepository;

    public void updateUserCookie(CookieUpdateDto cookieUpdateDto) {
        log.info("cookieUpdateDto: {}", cookieUpdateDto);

        UserCookie userCookie = userCookieRepository.findByUserNameAndSiteName(cookieUpdateDto.getUserName(), cookieUpdateDto.getSiteName())
                .orElseThrow(() -> new IllegalArgumentException("Not found user"));

        userCookie.setCookie(cookieUpdateDto.getCookie());
    }
}
