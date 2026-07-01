package com.rollcall;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class PermissionIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "teacher", roles = {"TEACHER"})
    public void teacherCanResetPassword() throws Exception {
        mockMvc.perform(post("/auth/resetPassword")
                .param("username", "someuser")
                .param("newPassword", "newpass"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "student", roles = {"STUDENT"})
    public void studentCannotResetPassword() throws Exception {
        mockMvc.perform(post("/auth/resetPassword")
                .param("username", "someuser")
                .param("newPassword", "newpass"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "teacher", roles = {"TEACHER"})
    public void teacherCanChangeRole() throws Exception {
        mockMvc.perform(post("/auth/changeRole")
                .param("username", "someuser")
                .param("role", "ROLE_STUDENT"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "student", roles = {"STUDENT"})
    public void studentCannotChangeRole() throws Exception {
        mockMvc.perform(post("/auth/changeRole")
                .param("username", "someuser")
                .param("role", "ROLE_STUDENT"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "teacher", roles = {"TEACHER"})
    public void teacherCanDeleteUser() throws Exception {
        mockMvc.perform(post("/auth/deleteUser")
                .param("username", "someuser"))
                .andExpect(status().isMethodNotAllowed());
    }
}
