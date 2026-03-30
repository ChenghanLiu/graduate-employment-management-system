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
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class Round8RbacAcceptanceTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldEnforceRound8LightweightRbac() throws Exception {
        mockMvc.perform(get("/api/student-profiles/current")
                        .header("X-User-Id", "1001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));

        mockMvc.perform(put("/api/resumes/current")
                        .header("X-User-Id", "1001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "resumeName": "RBAC学生简历-更新",
                                  "jobIntention": "Java后端开发工程师",
                                  "expectedCity": "上海",
                                  "expectedSalary": "20k-25k",
                                  "selfEvaluation": "用于RBAC验证"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.resumeName").value("RBAC学生简历-更新"));

        mockMvc.perform(get("/api/enterprise-profiles/current")
                        .header("X-User-Id", "2001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));

        mockMvc.perform(post("/api/job-posts")
                        .header("X-User-Id", "2001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "jobTitle": "RBAC岗位",
                                  "jobCategory": "研发",
                                  "workCity": "上海",
                                  "educationRequirement": "本科",
                                  "salaryMin": 15000,
                                  "salaryMax": 22000,
                                  "salaryRemark": "15薪",
                                  "hiringCount": 1,
                                  "jobDescription": "RBAC验证岗位",
                                  "deadline": "2026-12-31"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));

        mockMvc.perform(get("/api/job-applications/received")
                        .header("X-User-Id", "2001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));

        mockMvc.perform(get("/api/employment-records/review")
                        .header("X-User-Id", "3001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));

        mockMvc.perform(get("/api/statistics/overview")
                        .header("X-User-Id", "3001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));

        mockMvc.perform(post("/api/announcements")
                        .header("X-User-Id", "3001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "RBAC公告",
                                  "content": "教师侧管理公告",
                                  "type": "就业通知"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));

        mockMvc.perform(get("/api/employment-records/review")
                        .header("X-User-Id", "1001"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(4030));

        mockMvc.perform(get("/api/statistics/overview")
                        .header("X-User-Id", "1001"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(4030));

        mockMvc.perform(post("/api/resumes/current")
                        .header("X-User-Id", "2001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "resumeName": "企业非法简历",
                                  "jobIntention": "无效",
                                  "expectedCity": "上海"
                                }
                                """))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(4030));

        mockMvc.perform(post("/api/announcements")
                        .header("X-User-Id", "2001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "企业非法公告",
                                  "content": "不应允许",
                                  "type": "就业通知"
                                }
                                """))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(4030));
    }
}
