package me.synology.hajubal.coins.service;

import me.synology.hajubal.coins.entity.SiteUser;
import me.synology.hajubal.coins.respository.SiteUserRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class SiteUserDetailsServiceImplTest {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private SiteUserRepository userRepository;

    @Disabled
    @DisplayName("사용자 비밀번호 암호화.")
    @Test
    void generatePassword() {
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        String encodedPassword = encoder.encode("user");

        System.out.println("encodedPassword = " + encodedPassword);
    }

    @DisplayName("사용자 조회 테스트")
    @Test
    void findUser() {
        SiteUser siteUser = createSiteUser();

        //given
        userRepository.save(siteUser);

        //when
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(siteUser.getLoginId());

        //then
        assertThat(userDetails.getUsername()).isEqualTo(siteUser.getLoginId());
    }

    private static SiteUser createSiteUser() {
        return SiteUser.builder().loginId("loginId").password("password").userName("userName").build();
    }

}