package com.example.employment.modules.student;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.employment.config.SecurityConfig;
import com.example.employment.config.WebMvcConfig;
import com.example.employment.exception.GlobalExceptionHandler;
import com.example.employment.security.CurrentIdentityResolver;
import com.example.employment.security.RequestIdentityInterceptor;
import com.example.employment.security.RoleAuthorizationInterceptor;
import com.example.employment.security.RoleGuard;
import com.example.employment.modules.student.controller.StudentProfileController;
import com.example.employment.modules.student.dto.StudentProfileCreateRequest;
import com.example.employment.modules.student.dto.StudentProfileUpdateRequest;
import com.example.employment.modules.student.service.StudentProfileService;
import com.example.employment.modules.student.vo.StudentProfileDetailVO;
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

@WebMvcTest(StudentProfileController.class)
@Import({
        SecurityConfig.class,
        WebMvcConfig.class,
        GlobalExceptionHandler.class,
        CurrentIdentityResolver.class,
        RequestIdentityInterceptor.class,
        RoleAuthorizationInterceptor.class,
        RoleGuard.class,
        StudentProfileControllerTest.TestConfig.class
})
class StudentProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldGetCurrentStudentProfile() throws Exception {
        mockMvc.perform(get("/api/student-profiles/current")
                        .header("X-User-Id", "1001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.studentNo").value("20240001"))
                .andExpect(jsonPath("$.data.employmentStatusName").value("求职中"));
    }

    @Test
    void shouldRejectInvalidStudentProfileCreateRequest() throws Exception {
        mockMvc.perform(post("/api/student-profiles/current")
                        .header("X-User-Id", "1001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "studentNo": "",
                                  "collegeName": "",
                                  "majorName": "",
                                  "educationLevel": "",
                                  "graduationYear": 1999,
                                  "employmentStatus": 9
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(4001));
    }

    @TestConfiguration
    static class TestConfig {

        @Bean
        StudentProfileService studentProfileService() {
            return new StudentProfileService() {
                @Override
                public StudentProfileDetailVO getCurrentProfile(Long userId) {
                    return StudentProfileDetailVO.builder()
                            .id(1L)
                            .userId(userId)
                            .studentNo("20240001")
                            .collegeName("计算机学院")
                            .majorName("软件工程")
                            .educationLevel("本科")
                            .graduationYear(2026)
                            .employmentStatus(1)
                            .employmentStatusName("求职中")
                            .build();
                }

                @Override
                public StudentProfileDetailVO createProfile(Long userId, StudentProfileCreateRequest request) {
                    return getCurrentProfile(userId);
                }

                @Override
                public StudentProfileDetailVO updateProfile(Long userId, StudentProfileUpdateRequest request) {
                    return getCurrentProfile(userId);
                }

                @Override
                public List<StudentProfileDetailVO> listAdminProfiles() {
                    return List.of(getCurrentProfile(1001L));
                }

                @Override
                public StudentProfileDetailVO getAdminProfile(Long profileId) {
                    return getCurrentProfile(1001L);
                }

                @Override
                public StudentProfileDetailVO updateAdminProfile(Long profileId, StudentProfileUpdateRequest request) {
                    return getCurrentProfile(1001L);
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
