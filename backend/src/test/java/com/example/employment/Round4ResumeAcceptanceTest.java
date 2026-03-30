package com.example.employment;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class Round4ResumeAcceptanceTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldVerifyResumeRound4Endpoints() throws Exception {
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));

        mockMvc.perform(get("/api/resumes/current"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(4040));

        mockMvc.perform(post("/api/resumes/current")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "resumeName": "2026届校招简历",
                                  "jobIntention": "后端开发工程师",
                                  "expectedCity": "上海",
                                  "expectedSalary": "15k-20k",
                                  "selfEvaluation": "具备 Spring Boot 与 MyBatis-Plus 项目经验"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.resumeName").value("2026届校招简历"));

        mockMvc.perform(get("/api/resumes/current"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.jobIntention").value("后端开发工程师"));

        mockMvc.perform(put("/api/resumes/current")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "resumeName": "2026届校招简历-更新版",
                                  "jobIntention": "Java后端开发工程师",
                                  "expectedCity": "杭州",
                                  "expectedSalary": "18k-22k",
                                  "selfEvaluation": "具备 Java 后端、数据库设计和接口联调经验"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.expectedCity").value("杭州"));

        mockMvc.perform(post("/api/resumes/current/educations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "schoolName": "XX大学",
                                  "majorName": "软件工程",
                                  "educationLevel": "本科",
                                  "startDate": "2022-09-01",
                                  "endDate": "2026-06-30",
                                  "description": "主修软件工程核心课程",
                                  "sortOrder": 1
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));

        mockMvc.perform(get("/api/resumes/current/educations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data[0].schoolName").value("XX大学"));

        mockMvc.perform(post("/api/resumes/current/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "projectName": "就业管理系统",
                                  "roleName": "后端开发",
                                  "startDate": "2025-11-01",
                                  "endDate": "2026-02-28",
                                  "description": "负责学生简历模块接口开发",
                                  "sortOrder": 1
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));

        mockMvc.perform(get("/api/resumes/current/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data[0].projectName").value("就业管理系统"));

        mockMvc.perform(post("/api/resumes/current/skills")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "skillName": "Spring Boot",
                                  "skillLevel": 3,
                                  "description": "可独立完成 CRUD 与权限接口开发",
                                  "sortOrder": 1
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));

        mockMvc.perform(get("/api/resumes/current/skills"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data[0].skillName").value("Spring Boot"));
    }
}
