package com.rollcall.entity;

import java.time.LocalDateTime;

public class AttendanceRecord {
    private Long id;
    private Long sessionId;
    private String username;
    private String status;
    private LocalDateTime checkinTime;
    private Double checkinLat;
    private Double checkinLng;
    private String proofUrl;
    private String note;

    // getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getSessionId() { return sessionId; }
    public void setSessionId(Long sessionId) { this.sessionId = sessionId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCheckinTime() { return checkinTime; }
    public void setCheckinTime(LocalDateTime checkinTime) { this.checkinTime = checkinTime; }
    public Double getCheckinLat() { return checkinLat; }
    public void setCheckinLat(Double checkinLat) { this.checkinLat = checkinLat; }
    public Double getCheckinLng() { return checkinLng; }
    public void setCheckinLng(Double checkinLng) { this.checkinLng = checkinLng; }
    public String getProofUrl() { return proofUrl; }
    public void setProofUrl(String proofUrl) { this.proofUrl = proofUrl; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}
