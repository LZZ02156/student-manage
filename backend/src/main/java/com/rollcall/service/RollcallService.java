package com.rollcall.service;

import com.rollcall.dto.CheckinRequest;
import com.rollcall.dto.CreateSessionRequest;
import com.rollcall.entity.AttendanceRecord;
import com.rollcall.entity.RollcallSession;
import com.rollcall.mapper.AttendanceRecordMapper;
import com.rollcall.mapper.RollcallSessionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.Duration;
import java.util.List;
import java.util.UUID;

@Service
public class RollcallService {
    @Autowired
    private RollcallSessionMapper sessionMapper;
    @Autowired
    private AttendanceRecordMapper recordMapper;

    @Transactional
    public Long createSession(String creator, CreateSessionRequest req) {
        RollcallSession s = new RollcallSession();
        s.setTitle(req.getTitle());
        s.setCreator(creator);
        s.setStartTime(req.getStartTime());
        s.setEndTime(req.getEndTime());
        s.setRequireLocation(req.getRequireLocation());
        s.setLocationLat(req.getLocationLat());
        s.setLocationLng(req.getLocationLng());
        s.setLocationRadiusM(req.getLocationRadiusM());
        s.setRequireQr(req.getRequireQr());
        if (req.getRequireQr() != null && req.getRequireQr()) {
            s.setQrCode(UUID.randomUUID().toString());
        }
        sessionMapper.insert(s);
        return s.getId();
    }

    public RollcallSession getSession(Long id) {
        return sessionMapper.findById(id);
    }

    public List<RollcallSession> listByCreator(String creator) {
        return sessionMapper.findByCreator(creator);
    }

    public List<AttendanceRecord> getAttendance(Long sessionId) {
        return recordMapper.findBySessionId(sessionId);
    }

    @Transactional
    public String checkin(String username, Long sessionId, CheckinRequest req) {
        RollcallSession s = sessionMapper.findById(sessionId);
        if (s == null) throw new IllegalArgumentException("点名会话不存在");
        LocalDateTime now = LocalDateTime.now();
        if (s.getStartTime() != null && now.isBefore(s.getStartTime())) throw new IllegalArgumentException("未到签到时间");
        if (s.getEndTime() != null && now.isAfter(s.getEndTime())) throw new IllegalArgumentException("签到已结束");

        if (Boolean.TRUE.equals(s.getRequireQr())) {
            if (req.getQrCode() == null || !req.getQrCode().equals(s.getQrCode())) {
                throw new IllegalArgumentException("二维码不匹配");
            }
        }

        if (Boolean.TRUE.equals(s.getRequireLocation())) {
            if (req.getLat() == null || req.getLng() == null) throw new IllegalArgumentException("需要位置签到");
            double dist = distanceMeters(s.getLocationLat(), s.getLocationLng(), req.getLat(), req.getLng());
            if (s.getLocationRadiusM() != null && dist > s.getLocationRadiusM()) {
                throw new IllegalArgumentException("不在签到范围内");
            }
        }

        AttendanceRecord exist = recordMapper.findBySessionAndUser(sessionId, username);
        if (exist != null) return exist.getStatus();

        AttendanceRecord r = new AttendanceRecord();
        r.setSessionId(sessionId);
        r.setUsername(username);
        r.setCheckinTime(now);
        r.setCheckinLat(req.getLat());
        r.setCheckinLng(req.getLng());
        r.setProofUrl(req.getProofUrl());

        // define lateness: more than 5 minutes after start => LATE
        String status = "PRESENT";
        if (s.getStartTime() != null) {
            Duration d = Duration.between(s.getStartTime(), now);
            if (d.toMinutes() > 5) status = "LATE";
        }
        r.setStatus(status);
        recordMapper.insert(r);
        return status;
    }

    private double distanceMeters(Double lat1, Double lon1, Double lat2, Double lon2) {
        if (lat1 == null || lon1 == null || lat2 == null || lon2 == null) return Double.MAX_VALUE;
        double R = 6371000; // meters
        double phi1 = Math.toRadians(lat1);
        double phi2 = Math.toRadians(lat2);
        double dphi = Math.toRadians(lat2 - lat1);
        double dlambda = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dphi/2) * Math.sin(dphi/2) + Math.cos(phi1) * Math.cos(phi2) * Math.sin(dlambda/2) * Math.sin(dlambda/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return R * c;
    }
}
