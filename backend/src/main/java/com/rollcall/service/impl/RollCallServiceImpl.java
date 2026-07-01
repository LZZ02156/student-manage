package com.rollcall.service.impl;

import com.rollcall.entity.RollCallRecord;
import com.rollcall.entity.Student;
import com.rollcall.mapper.RollCallMapper;
import com.rollcall.mapper.StudentMapper;
import com.rollcall.service.RollCallService;
import com.rollcall.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
public class RollCallServiceImpl implements RollCallService {
    @Autowired
    private RollCallMapper rollCallMapper;
    @Autowired
    private StudentMapper studentMapper;
    @Value("${file.upload.path}")
    private String uploadPath;

    private static final long MAX_IMAGE_SIZE = 5L * 1024 * 1024; // 5MB

    @Override
    public boolean uploadRollCallRecord(String studentId, String studentName, String className, String courseName, String location, MultipartFile faceImage) throws Exception {
        // validate
        if (faceImage == null || faceImage.isEmpty()) return false;
        String contentType = faceImage.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("只允许上传图片文件");
        }
        if (faceImage.getSize() > MAX_IMAGE_SIZE) {
            throw new IllegalArgumentException("图片大小不能超过5MB");
        }
        String fileName = FileUtil.generateFileName(faceImage.getOriginalFilename());
        FileUtil.saveFile(faceImage, uploadPath, fileName);
        String imageUrl = "/uploads/" + fileName; // nginx mapping
        RollCallRecord record = new RollCallRecord();
        record.setStudentId(studentId);
        record.setStudentName(studentName);
        record.setClassName(className);
        record.setCourseName(courseName);
        record.setLocation(location);
        record.setFaceImageUrl(imageUrl);
        record.setUploadTime(new Date());
        record.setIsValid(1);
        int count = rollCallMapper.insertRollCallRecord(record);
        return count > 0;
    }

    @Override
    public Map<String, Integer> getRollCallResult(String className, String courseName) {
        int totalNum = studentMapper.countStudentByClassAndCourse(className, courseName);
        int actualNum = rollCallMapper.countValidRollCallRecord(className, courseName);
        return Map.of("totalNum", totalNum, "actualNum", actualNum);
    }

    @Override
    public Student randomSelectStudent(String className, String courseName) {
        List<Student> list = studentMapper.getStudentByClassAndCourse(className, courseName);
        if (list == null || list.isEmpty()) return null;
        Random r = new Random();
        return list.get(r.nextInt(list.size()));
    }

    @Override
    public List<Student> getStudentList(String className, String courseName) {
        return studentMapper.getStudentByClassAndCourse(className, courseName);
    }
}
