package com.example.employment;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("local")
class ChineseEncodingApiVerificationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnReadableChineseForDemoPages() throws Exception {
        mockMvc.perform(get("/api/student-profiles/current"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.collegeName").value("计算机学院"))
                .andExpect(jsonPath("$.data.majorName").value("软件工程"));

        mockMvc.perform(get("/api/resumes/current"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.resumeName").value("张晨曦-软件开发岗简历"));

        mockMvc.perform(get("/api/job-posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(content().string(containsString("Java后端开发工程师")))
                .andExpect(content().string(containsString("上海")));

        mockMvc.perform(get("/api/announcements"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(content().string(containsString("2026届毕业生春季双选会参会须知")))
                .andExpect(content().string(containsString("双选会通知")));

        mockMvc.perform(get("/api/job-posts/mine")
                        .header("X-User-Id", "2001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(content().string(containsString("Java后端开发工程师")))
                .andExpect(content().string(containsString("12k-18k·14薪")));

        mockMvc.perform(get("/api/admin/announcements")
                        .header("X-User-Id", "9001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(content().string(containsString("2026届毕业生春季双选会参会须知")));

        mockMvc.perform(get("/api/statistics/overview")
                        .header("X-User-Id", "9001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.totalStudents").value(8));

        mockMvc.perform(get("/api/statistics/employment-status")
                        .header("X-User-Id", "9001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data[0].employmentStatusName").isNotEmpty());
    }
}
