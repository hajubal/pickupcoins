package me.synology.hajubal.coins.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.synology.hajubal.coins.controller.dto.CookieDto;
import me.synology.hajubal.coins.entity.UserCookie;
import me.synology.hajubal.coins.respository.UserCookieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
@Service
public class UserCookieService {

    private final UserCookieRepository userCookieRepository;

    /**
     * 쿠키 업데이트
     *
     * @param userId
     * @param cookieUpdateDto
     */
    @Transactional
    public void updateUserCookie(Long userId, CookieDto.CookieUpdateDto cookieUpdateDto) {
        log.info("cookieUpdateDto: {}", cookieUpdateDto);

        UserCookie userCookie = userCookieRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Not found user"));

        userCookie.setCookie(cookieUpdateDto.getCookie());
        userCookie.setIsValid(cookieUpdateDto.getIsValid());
    }

    /**
     * 신규 사용자 쿠키 추가
     *
     * @param cookieInsertDto
     * @return
     */
    @Transactional
    public Long insertUserCookie(CookieDto.CookieInsertDto cookieInsertDto) {
        log.info("cookieInsertDto: {}", cookieInsertDto);

        UserCookie userCookie = cookieInsertDto.toEntity();

        userCookieRepository.save(userCookie);

        return userCookie.getId();
    }
}
