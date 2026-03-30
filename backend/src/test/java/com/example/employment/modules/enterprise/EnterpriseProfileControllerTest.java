package com.example.employment.modules.enterprise;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.employment.config.SecurityConfig;
import com.example.employment.config.WebMvcConfig;
import com.example.employment.exception.GlobalExceptionHandler;
import com.example.employment.security.CurrentIdentityResolver;
import com.example.employment.security.RequestIdentityInterceptor;
import com.example.employment.security.RoleAuthorizationInterceptor;
import com.example.employment.security.RoleGuard;
import com.example.employment.modules.enterprise.controller.EnterpriseProfileController;
import com.example.employment.modules.enterprise.dto.EnterpriseProfileCreateRequest;
import com.example.employment.modules.enterprise.dto.EnterpriseProfileReviewRequest;
import com.example.employment.modules.enterprise.dto.EnterpriseProfileUpdateRequest;
import com.example.employment.modules.enterprise.service.EnterpriseProfileService;
import com.example.employment.modules.enterprise.vo.EnterpriseProfileDetailVO;
import com.example.employment.modules.enterprise.vo.EnterpriseReviewStatusVO;
import com.example.employment.web.CurrentUserResolver;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(EnterpriseProfileController.class)
@Import({
        SecurityConfig.class,
        WebMvcConfig.class,
        GlobalExceptionHandler.class,
        CurrentIdentityResolver.class,
        RequestIdentityInterceptor.class,
        RoleAuthorizationInterceptor.class,
        RoleGuard.class,
        EnterpriseProfileControllerTest.TestConfig.class
})
class EnterpriseProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldGetEnterpriseReviewStatus() throws Exception {
        mockMvc.perform(get("/api/enterprise-profiles/current/review-status")
                        .header("X-User-Id", "2001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.reviewStatus").value(0))
                .andExpect(jsonPath("$.data.reviewStatusName").value("待审核"));
    }

    @Test
    void shouldReviewEnterpriseProfile() throws Exception {
        mockMvc.perform(put("/api/admin/enterprise-profiles/1/review")
                        .header("X-User-Id", "3001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "reviewStatus": 1,
                                  "reviewRemark": "资料完整"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.reviewStatusName").value("审核通过"));
    }

    @TestConfiguration
    static class TestConfig {

        @Bean
        EnterpriseProfileService enterpriseProfileService() {
            return new EnterpriseProfileService() {
                @Override
                public EnterpriseProfileDetailVO getCurrentProfile(Long userId) {
                    return EnterpriseProfileDetailVO.builder()
                            .id(1L)
                            .userId(userId)
                            .enterpriseName("示例科技有限公司")
                            .creditCode("91310000MA1EXAMPLE")
                            .contactName("张三")
                            .contactPhone("13800000000")
                            .reviewStatus(0)
                            .reviewStatusName("待审核")
                            .build();
                }

                @Override
                public EnterpriseProfileDetailVO createProfile(Long userId, EnterpriseProfileCreateRequest request) {
                    return getCurrentProfile(userId);
                }

                @Override
                public EnterpriseProfileDetailVO updateProfile(Long userId, EnterpriseProfileUpdateRequest request) {
                    return getCurrentProfile(userId);
                }

                @Override
                public EnterpriseReviewStatusVO getCurrentReviewStatus(Long userId) {
                    return new EnterpriseReviewStatusVO(0, "待审核", null);
                }

                @Override
                public EnterpriseProfileDetailVO reviewProfile(Long profileId, EnterpriseProfileReviewRequest request) {
                    return EnterpriseProfileDetailVO.builder()
                            .id(profileId)
                            .userId(2001L)
                            .enterpriseName("示例科技有限公司")
                            .creditCode("91310000MA1EXAMPLE")
                            .contactName("张三")
                            .contactPhone("13800000000")
                            .reviewStatus(1)
                            .reviewStatusName("审核通过")
                            .build();
                }

                @Override
                public List<EnterpriseProfileDetailVO> listAdminProfiles() {
                    return List.of(getCurrentProfile(2001L));
                }

                @Override
                public EnterpriseProfileDetailVO getAdminProfile(Long profileId) {
                    return getCurrentProfile(2001L);
                }

                @Override
                public EnterpriseProfileDetailVO updateAdminProfile(Long profileId, EnterpriseProfileUpdateRequest request) {
                    return getCurrentProfile(2001L);
                }

                @Override
                public void disableAdminProfile(Long profileId) {
                }
            };
        }

        @Bean
        CurrentUserResolver currentUserResolver() {
            return new CurrentUserResolver();
        }
    }
}
