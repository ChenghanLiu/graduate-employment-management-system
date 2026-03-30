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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class Round5JobFlowAcceptanceTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldVerifyRound5JobPostAndApplicationFlow() throws Exception {
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));

        mockMvc.perform(post("/api/resumes/current")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "resumeName": "2026届校招简历",
                                  "jobIntention": "Java后端开发工程师",
                                  "expectedCity": "上海",
                                  "expectedSalary": "18k-22k",
                                  "selfEvaluation": "具备就业系统开发与联调经验"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));

        mockMvc.perform(post("/api/job-posts")
                        .header("X-User-Id", "2001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "jobTitle": "Java后端开发工程师",
                                  "jobCategory": "研发",
                                  "workCity": "上海",
                                  "educationRequirement": "本科",
                                  "salaryMin": 15000,
                                  "salaryMax": 22000,
                                  "salaryRemark": "14薪",
                                  "hiringCount": 3,
                                  "jobDescription": "负责学生就业系统后端接口开发",
                                  "deadline": "2026-12-31"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.postStatus").value(0));

        mockMvc.perform(get("/api/job-posts/mine")
                        .header("X-User-Id", "2001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data[0].jobTitle").value("Java后端开发工程师"));

        mockMvc.perform(put("/api/job-posts/1/status")
                        .header("X-User-Id", "2001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "postStatus": 1
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.postStatus").value(1));

        mockMvc.perform(get("/api/job-posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data[0].jobTitle").value("Java后端开发工程师"));

        mockMvc.perform(get("/api/job-posts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.enterpriseName").value("示例科技有限公司"));

        mockMvc.perform(post("/api/job-applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "jobPostId": 1,
                                  "remark": "期待进一步沟通"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.applicationStatus").value(0));

        mockMvc.perform(get("/api/job-applications/mine"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data[0].jobTitle").value("Java后端开发工程师"));

        mockMvc.perform(get("/api/job-applications/received")
                        .header("X-User-Id", "2001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data[0].studentNo").value("20240001"));

        mockMvc.perform(put("/api/job-applications/1/status")
                        .header("X-User-Id", "2001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "applicationStatus": 1,
                                  "remark": "已查看简历"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.applicationStatus").value(1));

        mockMvc.perform(post("/api/job-applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "jobPostId": 1
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(4090));
    }
}
