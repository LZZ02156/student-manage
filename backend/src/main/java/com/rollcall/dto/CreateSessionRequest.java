package com.rollcall.dto;

import java.time.LocalDateTime;

public class CreateSessionRequest {
    private String title;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Boolean requireLocation = false;
    private Double locationLat;
    private Double locationLng;
    private Integer locationRadiusM;
    private Boolean requireQr = false;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
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
}
