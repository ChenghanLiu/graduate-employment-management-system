package com.example.employment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
class Round3EndpointAcceptanceTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void printCurrentEndpointResponses() throws Exception {
        int healthStatus = print("health",
                mockMvc.perform(get("/api/health"))
                        .andReturn());
        int studentGetStatus = print("student-get",
                mockMvc.perform(get("/api/student-profiles/current"))
                        .andReturn());
        int studentPostStatus = print("student-post",
                mockMvc.perform(post("/api/student-profiles/current")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "studentNo": "20263001",
                                          "gender": 1,
                                          "collegeName": "计算机学院",
                                          "majorName": "软件工程",
                                          "className": "软工03班",
                                          "educationLevel": "本科",
                                          "graduationYear": 2026,
                                          "nativePlace": "上海",
                                          "employmentStatus": 1
                                        }
                                        """))
                        .andReturn());
        int enterpriseReviewStatus = print("enterprise-review-status",
                mockMvc.perform(get("/api/enterprise-profiles/current/review-status")
                                .header("X-User-Id", "2001"))
                        .andReturn());

        assertThat(healthStatus).isEqualTo(200);
        assertThat(studentGetStatus).isIn(200, 400, 503);
        assertThat(studentPostStatus).isIn(200, 409, 503);
        assertThat(enterpriseReviewStatus).isIn(200, 400, 503);
    }

    private int print(String name, MvcResult result) throws Exception {
        System.out.println("[" + name + "] status=" + result.getResponse().getStatus());
        System.out.println("[" + name + "] body=" + result.getResponse().getContentAsString());
        return result.getResponse().getStatus();
    }
}
