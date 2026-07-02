package com.rollcall;

import com.rollcall.entity.User;
import com.rollcall.mapper.UserMapper;
import com.rollcall.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class JwtAuthenticationE2ETest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserMapper userMapper;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Test
    public void teacherWithJwtCanAccessProtectedEndpoint() throws Exception {
        // insert a teacher user directly into H2 test DB
        User u = new User();
        u.setUsername("jwt_teacher");
        u.setPassword(passwordEncoder.encode("secret"));
        u.setRole("ROLE_TEACHER");
        userMapper.insertUser(u);

        String token = JwtUtil.generateToken("jwt_teacher");

        mockMvc.perform(post("/auth/resetPassword")
                .header("Authorization", "Bearer " + token)
                .param("username", "someuser")
                .param("newPassword", "newpass"))
                .andExpect(status().isOk());
    }

    @Test
    public void studentWithJwtCannotAccessProtectedEndpoint() throws Exception {
        User u = new User();
        u.setUsername("jwt_student");
        u.setPassword(passwordEncoder.encode("secret"));
        u.setRole("ROLE_STUDENT");
        userMapper.insertUser(u);

        String token = JwtUtil.generateToken("jwt_student");

        mockMvc.perform(post("/auth/resetPassword")
                .header("Authorization", "Bearer " + token)
                .param("username", "someuser")
                .param("newPassword", "newpass"))
                .andExpect(status().isForbidden());
    }
}
