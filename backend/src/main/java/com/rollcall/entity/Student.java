package com.rollcall.entity;

import lombok.Data;

@Data
public class Student {
    private Integer id;
    private String studentId;
    private String studentName;
    private String className;
    private String courseName;
}
