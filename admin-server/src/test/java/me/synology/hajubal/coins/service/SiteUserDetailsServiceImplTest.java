package me.synology.hajubal.coins.service;

import me.synology.hajubal.coins.entity.SiteUser;
import me.synology.hajubal.coins.respository.SiteUserRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

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
    }

    @DisplayName("사용자 조회 테스트")
    @Test
    void findUser() {
        SiteUser siteUser = SiteUser.builder()
                .loginId("loginId"+System.currentTimeMillis())
                .password("password")
                .userName("userName")
                .build();

        //given
        userRepository.save(siteUser);

        //when
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(siteUser.getLoginId());

        //then
        assertThat(userDetails.getUsername()).isEqualTo(siteUser.getLoginId());
    }

}