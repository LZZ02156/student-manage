package com.rollcall;

import com.rollcall.entity.AttendanceRecord;
import com.rollcall.entity.RollcallSession;
import com.rollcall.mapper.AttendanceRecordMapper;
import com.rollcall.mapper.RollcallSessionMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class RollcallExportTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RollcallSessionMapper sessionMapper;

    @Autowired
    private AttendanceRecordMapper recordMapper;

    @Test
    @WithMockUser(username = "teacher1", roles = {"TEACHER"})
    public void teacherCanExportCsv() throws Exception {
        RollcallSession s = new RollcallSession();
        s.setTitle("ExportSession");
        s.setCreator("teacher1");
        s.setStartTime(LocalDateTime.now().minusMinutes(10));
        s.setEndTime(LocalDateTime.now().plusMinutes(10));
        sessionMapper.insert(s);

        AttendanceRecord r1 = new AttendanceRecord();
        r1.setSessionId(s.getId());
        r1.setUsername("student_a");
        r1.setStatus("PRESENT");
        r1.setCheckinTime(LocalDateTime.now());
        recordMapper.insert(r1);

        AttendanceRecord r2 = new AttendanceRecord();
        r2.setSessionId(s.getId());
        r2.setUsername("student_b,withcomma");
        r2.setStatus("LATE");
        r2.setCheckinTime(LocalDateTime.now());
        r2.setNote("note with \"quote\"");
        recordMapper.insert(r2);

        var mvcRes = mockMvc.perform(get("/rollcall/session/" + s.getId() + "/export?format=csv"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/csv;charset=UTF-8"))
                .andReturn();

        String body = mvcRes.getResponse().getContentAsString();
        assertThat(body).contains("username,status,checkin_time");
        assertThat(body).contains("student_a");
        // ensure comma and quote are escaped in CSV
        assertThat(body).contains("\"student_b,withcomma\"");
        assertThat(body).contains("\"note with \"\"quote\"\"\"");
    }
}
