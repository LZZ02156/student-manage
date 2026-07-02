package com.rollcall.controller;

import com.rollcall.dto.CheckinRequest;
import com.rollcall.dto.CreateSessionRequest;
import com.rollcall.entity.AttendanceRecord;
import com.rollcall.entity.RollcallSession;
import com.rollcall.service.RollcallService;
import com.rollcall.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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

    @GetMapping("/session/{id}/export")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<byte[]> exportAttendance(@PathVariable Long id, @RequestParam(defaultValue = "csv") String format) {
        if (!"csv".equalsIgnoreCase(format)) {
            byte[] body = "Unsupported format".getBytes(StandardCharsets.UTF_8);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.TEXT_PLAIN).body(body);
        }

        List<AttendanceRecord> list = rollcallService.getAttendance(id);
        StringBuilder sb = new StringBuilder();
        // CSV header
        sb.append("username,status,checkin_time,checkin_lat,checkin_lng,proof_url,note\n");
        for (AttendanceRecord r : list) {
            sb.append(escapeCsv(r.getUsername())).append(",");
            sb.append(escapeCsv(r.getStatus())).append(",");
            sb.append(escapeCsv(r.getCheckinTime() == null ? "" : r.getCheckinTime().toString())).append(",");
            sb.append(escapeCsv(r.getCheckinLat() == null ? "" : r.getCheckinLat().toString())).append(",");
            sb.append(escapeCsv(r.getCheckinLng() == null ? "" : r.getCheckinLng().toString())).append(",");
            sb.append(escapeCsv(r.getProofUrl())).append(",");
            sb.append(escapeCsv(r.getNote())).append("\n");
        }

        byte[] bytes = sb.toString().getBytes(StandardCharsets.UTF_8);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv; charset=UTF-8"));
        // set simple Content-Disposition with filename
        String filename = "attendance_" + id + ".csv";
        String encoded = URLEncoder.encode(filename, StandardCharsets.UTF_8);
        headers.add("Content-Disposition", "attachment; filename=\"" + filename + "\"; filename*=UTF-8''" + encoded);

        return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
    }

    private String escapeCsv(String s) {
        if (s == null) return "";
        boolean needQuotes = s.contains(",") || s.contains("\n") || s.contains("\r") || s.contains("\"");
        String out = s.replace("\"", "\"\"");
        if (needQuotes) return "\"" + out + "\"";
        return out;
    }
}
