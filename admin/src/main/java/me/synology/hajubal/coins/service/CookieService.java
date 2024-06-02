package me.synology.hajubal.coins.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.synology.hajubal.coins.controller.dto.UserCookieDto;
import me.synology.hajubal.coins.entity.Cookie;
import me.synology.hajubal.coins.entity.SiteUser;
import me.synology.hajubal.coins.respository.CookieRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 사이트 사용자의 쿠키를 정보를 관리하는 서비스
 */
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
@Service
public class CookieService {

    private final CookieRepository cookieRepository;

    private final SiteUserService siteUserService;


    /**
     * 쿠키 업데이트
     *
     * @param cookieId
     * @param updateDto
     */
    @Transactional
    public void updateCookie(Long cookieId, UserCookieDto.UpdateDto updateDto) {
        log.info("cookieUpdateDto: {}", updateDto);

        Cookie cookie = cookieRepository.findById(cookieId)
                .orElseThrow(() -> new IllegalArgumentException("Not found cookie."));

        cookie.updateCookie(updateDto.getCookie());

        if(updateDto.getIsValid()) {
            cookie.valid();
        } else {
            cookie.invalid();
        }

        cookie.updateSiteName(updateDto.getSiteName());
        cookie.updateUserName(updateDto.getUserName());
    }

    @Transactional
    public void updateCookie(Long cookieId, String cookieStr) {
        log.info("userId: {}, cookie: {}", cookieId, cookieStr);

        Cookie cookie = cookieRepository.findById(cookieId)
                .orElseThrow(() -> new IllegalArgumentException("Not found cookie."));

        cookie.updateCookie(cookieStr);
    }

    /**
     * 신규 사용자 쿠키 추가
     *
     * @param insertDto
     * @return
     */
    @Transactional
    public Long insertCookie(UserCookieDto.InsertDto insertDto, String loginId) {
        log.info("cookieInsertDto: {}", insertDto);

        SiteUser siteUser = siteUserService.getSiteUser(loginId);

        Cookie cookie = cookieRepository.save(insertDto.toEntity(siteUser));

        return cookie.getId();
    }

    @Transactional
    public void deleteCookie(Long cookieId) {
        cookieRepository.deleteById(cookieId);
    }

    public List<Cookie> getAll() {
        return cookieRepository.findAll();
    }

    public List<Cookie> getAll(Long siteUserId) {
        return cookieRepository.findAllBySiteUser_Id(siteUserId);
    }

    public Cookie getCookie(Long cookieId) {
        return cookieRepository.findById(cookieId).orElseThrow(() -> new IllegalArgumentException("Not found cookie."));
    }

}
