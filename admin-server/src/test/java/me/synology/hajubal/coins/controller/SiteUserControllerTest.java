package me.synology.hajubal.coins.controller;

import me.synology.hajubal.coins.controller.dto.SiteUserDto;
import me.synology.hajubal.coins.entity.SiteUser;
import me.synology.hajubal.coins.respository.SiteUserRepository;
import me.synology.hajubal.coins.service.SiteUserService;
import me.synology.hajubal.coins.util.QueryStringUtils;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@AutoConfigureMockMvc
@SpringBootTest
class SiteUserControllerTest {

    @Autowired
    private SiteUserService siteUserService;

    @Autowired
    private SiteUserRepository siteUserRepository;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        siteUserRepository.deleteAll();
    }

    @Test
    void unauthorizedTest() throws Exception {
        this.mockMvc.perform(get("/"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username="loginId")
    void updateUser() throws Exception {
        //given
        SiteUser siteUser = siteUserRepository.save(createSiteUser("loginId"));

        SiteUserDto.UpdateDto updateDto = createUpdateDto(siteUser.getLoginId(), siteUser.getId());

        //when
        ResultActions perform = mockMvc.perform(post("/siteUser")
                .contentType("application/x-www-form-urlencoded")
                .with(csrf())
                .content(QueryStringUtils.toQueryString(updateDto)));

        //then
        perform.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("siteUser/editUser"))
                .andExpect(MockMvcResultMatchers.model().attribute("siteUser", Matchers.hasProperty("userName", Matchers.is(updateDto.getUserName()))));
    }

    private static SiteUser createSiteUser(String loginId) {
        return SiteUser.builder().userName("name").password("password").slackWebhookUrl("url").loginId(loginId).build();
    }

    private static SiteUserDto.UpdateDto createUpdateDto(String loginId, Long id) {
        SiteUserDto.UpdateDto updateDto = new SiteUserDto.UpdateDto();
        updateDto.setId(id);
        updateDto.setLoginId(loginId);
        updateDto.setUserName("updatedName");
        updateDto.setSlackWebhookUrl("url");
        return updateDto;
    }
}