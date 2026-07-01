package com.rollcall.controller;

import com.rollcall.dto.CheckinRequest;
import com.rollcall.dto.CreateSessionRequest;
import com.rollcall.entity.AttendanceRecord;
import com.rollcall.entity.RollcallSession;
import com.rollcall.service.RollcallService;
import com.rollcall.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rollcall")
public class RollcallController {
    @Autowired
    private RollcallService rollcallService;

    @PostMapping("/session")
    @PreAuthorize("hasRole('TEACHER')")
    public Result createSession(@RequestBody CreateSessionRequest req, Authentication auth) {
        String creator = auth.getName();
        Long id = rollcallService.createSession(creator, req);
        return Result.success(id);
    }

    @GetMapping("/session/{id}")
    public Result getSession(@PathVariable Long id) {
        RollcallSession s = rollcallService.getSession(id);
        return Result.success(s);
    }

    @GetMapping("/session/{id}/attendance")
    @PreAuthorize("hasRole('TEACHER')")
    public Result getAttendance(@PathVariable Long id) {
        List<AttendanceRecord> list = rollcallService.getAttendance(id);
        return Result.success(list);
    }

    @PostMapping("/session/{id}/checkin")
    public Result checkin(@PathVariable Long id, @RequestBody CheckinRequest req, Authentication auth) {
        String username = auth.getName();
        String status = rollcallService.checkin(username, id, req);
        return Result.success(status);
    }
}
