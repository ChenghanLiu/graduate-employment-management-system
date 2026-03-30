package com.example.employment;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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
class Round6EmploymentFlowAcceptanceTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldVerifyRound6EmploymentRecordAttachmentAndReviewFlow() throws Exception {
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));

        mockMvc.perform(post("/api/employment-records/current")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "employmentStatus": 3,
                                  "employmentType": "签就业协议",
                                  "companyName": "上海示例云科技有限公司",
                                  "jobTitle": "后端开发工程师",
                                  "workCity": "上海",
                                  "contractStartDate": "2026-07-01",
                                  "salaryAmount": 18500,
                                  "reportSource": "学生填报"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.reviewStatus").value(0));

        mockMvc.perform(get("/api/employment-records/current"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.companyName").value("上海示例云科技有限公司"));

        mockMvc.perform(put("/api/employment-records/current")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "employmentStatus": 2,
                                  "employmentType": "签约待入职",
                                  "companyName": "上海示例云科技有限公司",
                                  "jobTitle": "Java开发工程师",
                                  "workCity": "上海",
                                  "contractStartDate": "2026-08-01",
                                  "salaryAmount": 19500,
                                  "reportSource": "学生填报"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.employmentStatus").value(2));

        mockMvc.perform(post("/api/employment-records/current/attachments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "fileName": "offer.pdf",
                                  "fileUrl": "https://files.example.com/offer.pdf",
                                  "fileType": "pdf",
                                  "remark": "录用证明"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.fileName").value("offer.pdf"));

        mockMvc.perform(get("/api/employment-records/current/attachments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data[0].fileType").value("pdf"));

        mockMvc.perform(delete("/api/employment-records/current/attachments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));

        mockMvc.perform(get("/api/employment-records/review")
                        .header("X-User-Id", "3001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data[0].studentNo").value("20240001"))
                .andExpect(jsonPath("$.data[0].reviewStatus").value(0));

        mockMvc.perform(get("/api/employment-records/1")
                        .header("X-User-Id", "3001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.jobTitle").value("Java开发工程师"));

        mockMvc.perform(put("/api/employment-records/1/review")
                        .header("X-User-Id", "3001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "reviewStatus": 2,
                                  "reviewRemark": "请补充有效证明材料"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.reviewStatus").value(2))
                .andExpect(jsonPath("$.data.reviewRemark").value("请补充有效证明材料"));

        mockMvc.perform(put("/api/employment-records/1/review")
                        .header("X-User-Id", "3001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "reviewStatus": 1,
                                  "reviewRemark": "直接重复审核"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(4090));

        mockMvc.perform(put("/api/employment-records/current")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "employmentStatus": 3,
                                  "employmentType": "正式入职",
                                  "companyName": "上海示例云科技有限公司",
                                  "jobTitle": "后端开发工程师",
                                  "workCity": "上海",
                                  "contractStartDate": "2026-09-01",
                                  "salaryAmount": 20500,
                                  "reportSource": "学生补充"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.reviewStatus").value(0));

        mockMvc.perform(put("/api/employment-records/1/review")
                        .header("X-User-Id", "3001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "reviewStatus": 1,
                                  "reviewRemark": "材料完整"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.reviewStatus").value(1))
                .andExpect(jsonPath("$.data.employmentStatus").value(3));

        mockMvc.perform(get("/api/student-profiles/current"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.employmentStatus").value(3));

        mockMvc.perform(put("/api/employment-records/current")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "employmentStatus": 5,
                                  "employmentType": "创业",
                                  "companyName": "上海示例云科技有限公司",
                                  "jobTitle": "创始人",
                                  "workCity": "上海",
                                  "contractStartDate": "2026-10-01",
                                  "salaryAmount": 30000,
                                  "reportSource": "学生补充"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(4090));
    }
}
