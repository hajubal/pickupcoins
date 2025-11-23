package me.synology.hajubal.coins.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.synology.hajubal.coins.entity.Cookie;
import me.synology.hajubal.coins.entity.SiteUser;
import me.synology.hajubal.coins.respository.CookieRepository;
import me.synology.hajubal.coins.respository.SiteUserRepository;
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

    private final SiteUserRepository siteUserRepository;

    /**
     * 쿠키 업데이트
     *
     * @param cookieId
     * @param cookie
     * @param userName
     * @param siteName
     * @param isValid
     */
    @Transactional
    public void updateCookie(Long cookieId, String cookie, String userName, String siteName, Boolean isValid) {
        log.info("cookieId: {}, cookie: {}, userName: {}, siteName: {}, isValid: {}",
                cookieId, cookie, userName, siteName, isValid);

        Cookie cookieEntity = cookieRepository.findById(cookieId)
                .orElseThrow(() -> new IllegalArgumentException("Not found cookie."));

        cookieEntity.updateCookie(cookie);

        if(isValid) {
            cookieEntity.valid();
        } else {
            cookieEntity.invalid();
        }

        cookieEntity.updateSiteName(siteName);
        cookieEntity.updateUserName(userName);
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
     * @param siteUser
     * @param cookie
     * @param userName
     * @param siteName
     * @param isValid
     * @return
     */
    @Transactional
    public Long insertCookie(SiteUser siteUser, String cookie, String userName, String siteName, Boolean isValid) {
        log.info("siteUser: {}, cookie: {}, userName: {}, siteName: {}, isValid: {}",
                siteUser.getId(), cookie, userName, siteName, isValid);

        Cookie cookieEntity = Cookie.builder()
                .siteUser(siteUser)
                .cookie(cookie)
                .userName(userName)
                .siteName(siteName)
                .isValid(isValid)
                .build();

        Cookie saved = cookieRepository.save(cookieEntity);

        return saved.getId();
    }

    /**
     * 로그인 ID로 SiteUser를 조회하고 쿠키 추가
     *
     * @param loginId
     * @param cookie
     * @param userName
     * @param siteName
     * @param isValid
     * @return
     */
    @Transactional
    public Long insertCookie(String loginId, String cookie, String userName, String siteName, Boolean isValid) {
        SiteUser siteUser = siteUserRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("Not found site user."));

        return insertCookie(siteUser, cookie, userName, siteName, isValid);
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
