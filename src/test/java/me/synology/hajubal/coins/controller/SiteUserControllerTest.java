package me.synology.hajubal.coins.controller;

import me.synology.hajubal.coins.controller.dto.SiteUserDto;
import me.synology.hajubal.coins.entity.SiteUser;
import me.synology.hajubal.coins.service.SiteUserService;
import me.synology.hajubal.coins.util.QueryStringUtils;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@WebMvcTest(SiteUserController.class)
class SiteUserControllerTest {
    @MockBean
    private SiteUserService siteUserService;


    @Autowired
    private MockMvc mockMvc;

    @Test
    void unauthorizedTest() throws Exception {
        this.mockMvc.perform(get("/"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username="loginId")
    void updateUser() throws Exception {
        //given
        SiteUserDto.UpdateDto updateDto = createUpdateDto();
        SiteUser siteUser = createSiteUser();

        given(siteUserService.getSiteUser(updateDto.getLoginId())).willReturn(siteUser);

        //when
        ResultActions perform = mockMvc.perform(post("/siteUser")
                .contentType("application/x-www-form-urlencoded")
                .with(csrf())
                .content(QueryStringUtils.toQueryString(updateDto)));

        //then
        perform.andExpect(status().isOk())
                .andExpect(view().name("siteUser/editUser"))
                .andExpect(model().attribute("siteUser", equalTo(updateDto)));
    }

    @NotNull
    private static SiteUser createSiteUser() {
        SiteUser siteUser = SiteUser.builder().userName("name").password("password").slackWebhookUrl("url").loginId("loginId").build();
        ReflectionTestUtils.setField(siteUser, "id", 1L);

        return siteUser;
    }

    @NotNull
    private static SiteUserDto.UpdateDto createUpdateDto() {
        SiteUserDto.UpdateDto updateDto = new SiteUserDto.UpdateDto();
        updateDto.setId(1L);
        updateDto.setLoginId("loginId");
        updateDto.setUserName("name");
        updateDto.setSlackWebhookUrl("url");
        return updateDto;
    }
}