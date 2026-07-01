package com.rollcall.entity;

import lombok.Data;
import java.util.Date;

@Data
public class RollCallRecord {
    private Integer id;
    private String studentId;
    private String studentName;
    private String className;
    private String courseName;
    private String faceImageUrl;
    private String location;
    private Date uploadTime;
    private Integer isValid;
}
