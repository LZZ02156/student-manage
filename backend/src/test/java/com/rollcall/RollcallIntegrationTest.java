package com.rollcall;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rollcall.dto.CreateSessionRequest;
import com.rollcall.mapper.RollcallSessionMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class RollcallIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RollcallSessionMapper sessionMapper;

    @Test
    @WithMockUser(username = "teacher1", roles = {"TEACHER"})
    public void teacherCanCreateSession() throws Exception {
        CreateSessionRequest req = new CreateSessionRequest();
        req.setTitle("Test Session");
        req.setStartTime(LocalDateTime.now().minusMinutes(1));
        req.setEndTime(LocalDateTime.now().plusMinutes(30));
        String json = objectMapper.writeValueAsString(req);

        mockMvc.perform(post("/rollcall/session")
                .contentType("application/json")
                .content(json))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "student1", roles = {"STUDENT"})
    public void studentCanCheckinToSession() throws Exception {
        // create session directly via mapper
        var s = new com.rollcall.entity.RollcallSession();
        s.setTitle("Session2");
        s.setCreator("teacher1");
        s.setStartTime(LocalDateTime.now().minusMinutes(1));
        s.setEndTime(LocalDateTime.now().plusMinutes(30));
        s.setRequireLocation(false);
        sessionMapper.insert(s);

        var req = new com.rollcall.dto.CheckinRequest();
        req.setLat(null);
        req.setLng(null);
        String json = objectMapper.writeValueAsString(req);

        mockMvc.perform(post("/rollcall/session/" + s.getId() + "/checkin")
                .contentType("application/json")
                .content(json))
                .andExpect(status().isOk());
    }
}
