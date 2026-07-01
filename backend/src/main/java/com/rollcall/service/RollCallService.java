package com.rollcall.service;

import com.rollcall.entity.RollCallRecord;
import com.rollcall.entity.Student;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface RollCallService {
    boolean uploadRollCallRecord(String studentId, String studentName, String className, String courseName, String location, MultipartFile faceImage) throws Exception;
    Map<String, Integer> getRollCallResult(String className, String courseName);
    Student randomSelectStudent(String className, String courseName);
    List<Student> getStudentList(String className, String courseName);
}
