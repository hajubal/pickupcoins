package me.synology.hajubal.coins.controller;

import me.synology.hajubal.coins.controller.dto.SiteUserDto;
import me.synology.hajubal.coins.entity.SiteUser;
import me.synology.hajubal.coins.service.SiteUserService;
import me.synology.hajubal.coins.util.QueryStringUtils;
import org.hamcrest.Matchers;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@AutoConfigureMockMvc
//@SpringBootTest
@WebMvcTest(SiteUserController.class)
class SiteUserControllerTest {
    @MockBean
    private SiteUserService siteUserService;
//
//    @MockBean
//    private UserDetailsService userDetailsService;

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
        SiteUserDto.UpdateDto updateDto = createUpdateDto("loginId");
        SiteUser siteUser = createSiteUser("loginId");

        given(siteUserService.getSiteUser(updateDto.getLoginId())).willReturn(siteUser);

        //when
        ResultActions perform = mockMvc.perform(post("/siteUser")
                .contentType("application/x-www-form-urlencoded")
                .with(csrf())
                .content(QueryStringUtils.toQueryString(updateDto)));

        //then
        perform.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("siteUser/editUser"))
                .andExpect(MockMvcResultMatchers.model().attribute("siteUser", Matchers.equalTo(updateDto)));
    }

    @NotNull
    private static SiteUser createSiteUser(String loginId) {
        SiteUser siteUser = SiteUser.builder().userName("name").password("password").slackWebhookUrl("url").loginId(loginId).build();
        ReflectionTestUtils.setField(siteUser, "id", 1L);

        return siteUser;
    }

    @NotNull
    private static SiteUserDto.UpdateDto createUpdateDto(String loginId) {
        SiteUserDto.UpdateDto updateDto = new SiteUserDto.UpdateDto();
        updateDto.setId(1L);
        updateDto.setLoginId(loginId);
        updateDto.setUserName("name");
        updateDto.setSlackWebhookUrl("url");
        return updateDto;
    }
}