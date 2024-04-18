package me.synology.hajubal.coins.service;


import lombok.RequiredArgsConstructor;
import me.synology.hajubal.coins.controller.dto.SiteUserDto;
import me.synology.hajubal.coins.entity.SiteUser;
import me.synology.hajubal.coins.respository.SiteUserRepository;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class SiteUserService {

    private final SiteUserRepository siteUserRepository;

    /**
     * 사이트 사용자 정보 변경
     *
     * @param id 사이트 사용자 아이디
     * @param updateDto 변결될 사용자 정보
     * @return 변경된 사용자 정보
     */
    @Transactional
    public SiteUser updateSiteUser(Long id, SiteUserDto.UpdateDto updateDto) {

        SiteUser siteUser = siteUserRepository.findById(id).orElseThrow();

        siteUser.updateUserName(updateDto.getUserName());
        siteUser.updateSlackWebhookUrl(updateDto.getSlackWebhookUrl());
        if(updateDto.getActive()) {
            siteUser.activate();
        } else {
            siteUser.inActivate();
        }

        return siteUser;
    }

    /**
     * 사이트 사용자 조회
     *
     * @param loginId 사이트 사용자 로그인 아이디
     * @return 사이트 사용자
     */
    public SiteUser getSiteUser(String loginId) {
        return siteUserRepository.findByLoginId(loginId).orElseThrow();
    }

    /**
     * 사이트 사용자 비밀번호 변경
     *
     * @param id 사이트 사용자 아이디
     * @param newPassword 신규 비밀번호
     */
    @Transactional
    public void updatePassword(Long id, String newPassword) {
        PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

        String encoded = passwordEncoder.encode(newPassword);

        SiteUser siteUser = siteUserRepository.findById(id).orElseThrow();

        siteUser.updatePassword(encoded);
    }

    /**
     * 사용자 비활성화
     *
     * @param loginId
     */
    @Transactional
    public void inActivate(String loginId) {
        SiteUser siteUser = siteUserRepository.findByLoginId(loginId).orElseThrow();

        siteUser.inActivate();
    }
}
