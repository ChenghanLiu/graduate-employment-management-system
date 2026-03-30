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
class Round9AdminAcceptanceTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldSupportRound9AdminCrudAndRbac() throws Exception {
        mockMvc.perform(get("/api/admin/users")
                        .header("X-User-Id", "9001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data[0].passwordStrategy").exists());

        mockMvc.perform(put("/api/admin/users/3002")
                        .header("X-User-Id", "9001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "counselor_demo",
                                  "realName": "辅导员示例账号-已更新",
                                  "phone": "13800009999",
                                  "email": "counselor-updated@example.com",
                                  "status": 1
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.realName").value("辅导员示例账号-已更新"));

        mockMvc.perform(put("/api/admin/users/3002/roles")
                        .header("X-User-Id", "9001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "roleIds": [2, 3]
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.roles.length()").value(2));

        mockMvc.perform(get("/api/admin/roles")
                        .header("X-User-Id", "9001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(5));

        mockMvc.perform(get("/api/admin/enterprises")
                        .header("X-User-Id", "9001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data[0].enterpriseName").value("示例科技有限公司"));

        mockMvc.perform(get("/api/admin/students")
                        .header("X-User-Id", "9001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data[0].studentNo").value("20240001"));

        mockMvc.perform(post("/api/admin/announcements")
                        .header("X-User-Id", "9001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Round9管理公告",
                                  "content": "管理员创建公告",
                                  "type": "系统通知"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.title").value("Round9管理公告"));

        mockMvc.perform(put("/api/admin/announcements/1")
                        .header("X-User-Id", "9001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Round9管理公告-更新",
                                  "content": "管理员更新公告",
                                  "type": "系统通知"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.title").value("Round9管理公告-更新"));

        mockMvc.perform(put("/api/admin/announcements/1/status")
                        .header("X-User-Id", "9001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "status": 0
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.status").value(0));

        mockMvc.perform(delete("/api/admin/announcements/1")
                        .header("X-User-Id", "9001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));

        mockMvc.perform(get("/api/admin/dashboard/overview")
                        .header("X-User-Id", "9001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.totalStudents").value(1));

        mockMvc.perform(get("/api/admin/users")
                        .header("X-User-Id", "1001"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(4030));

        mockMvc.perform(get("/api/admin/users")
                        .header("X-User-Id", "2001"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(4030));

        mockMvc.perform(get("/swagger-ui.html"))
                .andExpect(status().is3xxRedirection());
    }
}
