package com.example.employment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
class MojibakeChainVerificationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldKeepChineseReadableAcrossSeedDataAndJsonEndpoints() throws Exception {
        Map<String, Object> enterpriseRow = jdbcTemplate.queryForMap("""
                SELECT enterprise_name, industry_name, contact_name, address, introduction
                FROM enterprise_profile
                WHERE user_id = 2001
                """);

        assertThat(enterpriseRow.get("enterprise_name")).isEqualTo("星联智造科技有限公司");
        assertThat(enterpriseRow.get("industry_name")).isEqualTo("智能制造 / 工业软件");
        assertThat(enterpriseRow.get("contact_name")).isEqualTo("王宁");
        assertThat(enterpriseRow.get("address")).isEqualTo("上海市浦东新区张江高科技园区科苑路88号");
        assertThat(enterpriseRow.get("introduction")).isEqualTo("聚焦工业互联网平台、数字工厂排产系统与智能产线协同软件，连续多年开展校招训练营。");

        mockMvc.perform(get("/api/enterprise-profiles/current")
                        .header("X-User-Id", "2001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.enterpriseName").value("星联智造科技有限公司"))
                .andExpect(jsonPath("$.data.industryName").value("智能制造 / 工业软件"))
                .andExpect(jsonPath("$.data.contactName").value("王宁"))
                .andExpect(jsonPath("$.data.address").value("上海市浦东新区张江高科技园区科苑路88号"))
                .andExpect(jsonPath("$.data.introduction").value("聚焦工业互联网平台、数字工厂排产系统与智能产线协同软件，连续多年开展校招训练营。"));

        mockMvc.perform(get("/api/student-profiles/current")
                        .header("X-User-Id", "1001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.collegeName").value("计算机学院"))
                .andExpect(jsonPath("$.data.majorName").value("软件工程"))
                .andExpect(jsonPath("$.data.className").value("软工2201班"));

        mockMvc.perform(get("/api/job-posts")
                        .header("X-User-Id", "1001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[*].enterpriseName", hasItem("星联智造科技有限公司")))
                .andExpect(jsonPath("$.data[*].jobTitle", hasItem("Java后端开发工程师")));

        mockMvc.perform(get("/api/announcements"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[*].title", hasItem("关于开展就业协议书集中审核的通知")));

        mockMvc.perform(get("/api/admin/enterprises")
                        .header("X-User-Id", "9001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[*].enterpriseName", hasItem("星联智造科技有限公司")));

        mockMvc.perform(get("/api/admin/announcements")
                        .header("X-User-Id", "9001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[*].title", hasItem("关于开展就业协议书集中审核的通知")));

        mockMvc.perform(get("/api/employment-records/review")
                        .header("X-User-Id", "3001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].companyName").value("星联智造科技有限公司"))
                .andExpect(jsonPath("$.data[0].jobTitle").value("Java后端开发工程师"));
    }
}
