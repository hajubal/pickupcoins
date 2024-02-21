package me.synology.hajubal.coins.service;


import lombok.RequiredArgsConstructor;
import me.synology.hajubal.coins.controller.dto.SiteUserDto;
import me.synology.hajubal.coins.entity.SiteUser;
import me.synology.hajubal.coins.respository.SiteUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class SiteUserService {

    private final SiteUserRepository siteUserRepository;

    @Transactional
    public SiteUser updateSiteUser(Long id, SiteUserDto.UpdateDto updateDto) {

        SiteUser siteUser = siteUserRepository.findById(id).orElseThrow();

        siteUser.updateUserName(updateDto.getUserName());
        siteUser.updateSlackWebhookUrl(updateDto.getSlackWebhookUrl());

        return siteUser;
    }

    public SiteUser getSiteUser(String loginId) {
        return siteUserRepository.findByLoginId(loginId).orElseThrow();
    }
}
