package com.example.employment;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class AuthFlowAcceptanceTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldSupportLightweightAuthLoginRegisterAndMe() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "student01",
                                  "password": "Student@123456"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.userId").value(1001))
                .andExpect(jsonPath("$.data.roleCode").value("STUDENT"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "enterprise01",
                                  "password": "Enterprise@123456"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.userId").value(2001))
                .andExpect(jsonPath("$.data.roleCode").value("ENTERPRISE"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "teacher1",
                                  "password": "Teacher@123456"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.userId").value(3001))
                .andExpect(jsonPath("$.data.roleCode").value("EMPLOYMENT_TEACHER"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "admin",
                                  "password": "Admin@123456"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.userId").value(9001))
                .andExpect(jsonPath("$.data.roleCode").value("ADMIN"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "student01",
                                  "password": "wrong"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("用户名或密码错误"));

        mockMvc.perform(post("/api/auth/register/student")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "new_student",
                                  "password": "Student@123456",
                                  "realName": "测试学生",
                                  "phone": "13812345678",
                                  "email": "new_student@test.cn",
                                  "studentNo": "20269999",
                                  "collegeName": "计算机学院",
                                  "majorName": "软件工程",
                                  "className": "软工2601班",
                                  "educationLevel": "本科",
                                  "graduationYear": 2026
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));

        mockMvc.perform(post("/api/auth/register/enterprise")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "new_enterprise",
                                  "password": "Enterprise@123456",
                                  "enterpriseName": "测试企业科技有限公司",
                                  "contactName": "王测试",
                                  "contactPhone": "13887654321",
                                  "contactEmail": "hr@test-enterprise.cn",
                                  "creditCode": "91310000TESTAUTH001"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "new_enterprise",
                                  "password": "Enterprise@123456"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.roleCode").value("ENTERPRISE"));

        mockMvc.perform(get("/api/auth/me")
                        .header("X-User-Id", "9001")
                        .header("X-User-Role", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.username").value("admin"))
                .andExpect(jsonPath("$.data.roleCode").value("ADMIN"));
    }
}
