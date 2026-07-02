package com.rollcall.entity;

import java.time.LocalDateTime;

public class RollcallSession {
    private Long id;
    private String title;
    private String creator;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Boolean requireLocation;
    private Double locationLat;
    private Double locationLng;
    private Integer locationRadiusM;
    private Boolean requireQr;
    private String qrCode;
    private LocalDateTime createdAt;

    // getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getCreator() { return creator; }
    public void setCreator(String creator) { this.creator = creator; }
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    public Boolean getRequireLocation() { return requireLocation; }
    public void setRequireLocation(Boolean requireLocation) { this.requireLocation = requireLocation; }
    public Double getLocationLat() { return locationLat; }
    public void setLocationLat(Double locationLat) { this.locationLat = locationLat; }
    public Double getLocationLng() { return locationLng; }
    public void setLocationLng(Double locationLng) { this.locationLng = locationLng; }
    public Integer getLocationRadiusM() { return locationRadiusM; }
    public void setLocationRadiusM(Integer locationRadiusM) { this.locationRadiusM = locationRadiusM; }
    public Boolean getRequireQr() { return requireQr; }
    public void setRequireQr(Boolean requireQr) { this.requireQr = requireQr; }
    public String getQrCode() { return qrCode; }
    public void setQrCode(String qrCode) { this.qrCode = qrCode; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
