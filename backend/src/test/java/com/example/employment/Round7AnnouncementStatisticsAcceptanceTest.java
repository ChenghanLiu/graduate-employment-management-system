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
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
class Round7AnnouncementStatisticsAcceptanceTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldVerifyRound7AnnouncementAndStatisticsFlow() throws Exception {
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));

        mockMvc.perform(post("/api/announcements")
                        .header("X-User-Id", "3001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "春招双选会安排",
                                  "content": "本周五下午两点在体育馆举行春招双选会。",
                                  "type": "就业通知"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.status").value(0))
                .andExpect(jsonPath("$.data.title").value("春招双选会安排"));

        mockMvc.perform(put("/api/announcements/1/status")
                        .header("X-User-Id", "3001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "status": 1
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.status").value(1))
                .andExpect(jsonPath("$.data.publishTime").exists());

        mockMvc.perform(get("/api/announcements"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data[0].title").value("春招双选会安排"));

        mockMvc.perform(get("/api/announcements/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.content").value("本周五下午两点在体育馆举行春招双选会。"));

        mockMvc.perform(put("/api/student-profiles/current")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "studentNo": "20240001",
                                  "gender": 1,
                                  "collegeName": "计算机学院",
                                  "majorName": "软件工程",
                                  "className": "软工01班",
                                  "educationLevel": "本科",
                                  "graduationYear": 2026,
                                  "nativePlace": "上海",
                                  "employmentStatus": 3
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.employmentStatus").value(3));

        mockMvc.perform(post("/api/resumes/current")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "resumeName": "2026届校招简历",
                                  "jobIntention": "Java后端开发工程师",
                                  "expectedCity": "上海",
                                  "expectedSalary": "18k-22k",
                                  "selfEvaluation": "具备就业系统开发经验"
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
                                  "hiringCount": 2,
                                  "jobDescription": "负责就业系统接口开发",
                                  "deadline": "2026-12-31"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));

        mockMvc.perform(put("/api/job-posts/1/status")
                        .header("X-User-Id", "2001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "postStatus": 1
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));

        mockMvc.perform(post("/api/job-applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "jobPostId": 1,
                                  "remark": "期待面试机会"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));

        mockMvc.perform(put("/api/job-applications/1/status")
                        .header("X-User-Id", "2001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "applicationStatus": 3,
                                  "remark": "录用"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.applicationStatus").value(3));

        mockMvc.perform(get("/api/statistics/overview")
                        .header("X-User-Id", "3001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.totalStudents").value(1))
                .andExpect(jsonPath("$.data.employedStudents").value(1))
                .andExpect(jsonPath("$.data.unemployedStudents").value(0))
                .andExpect(jsonPath("$.data.employmentRate").value(1.0000));

        mockMvc.perform(get("/api/statistics/employment-status")
                        .header("X-User-Id", "3001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data[3].employmentStatus").value(3))
                .andExpect(jsonPath("$.data[3].studentCount").value(1));

        mockMvc.perform(get("/api/statistics/job")
                        .header("X-User-Id", "3001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.totalJobs").value(1))
                .andExpect(jsonPath("$.data.totalApplications").value(1))
                .andExpect(jsonPath("$.data.hiredCount").value(1));
    }
}
