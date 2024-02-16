package me.synology.hajubal.coins.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
class SiteUserDetailsServiceImplTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void generatePassword() {
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        String encodedPassword = encoder.encode("gkwngjs12!@");

        System.out.println("encodedPassword = " + encodedPassword);
    }

    @Test
    void unauthorizedTest() throws Exception {
        this.mockMvc.perform(get("/"))
                .andExpect(status().isUnauthorized());
    }

    //FIXME test fail
//    @WithUserDetails("user@example.com")
    @WithMockUser
    @Test
    void loginTest() throws Exception {
        this.mockMvc.perform(get("/"))
                .andExpect(status().isOk());
    }

}