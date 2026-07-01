package com.rollcall.controller;

import com.rollcall.entity.Student;
import com.rollcall.service.RollCallService;
import com.rollcall.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/rollCall")
public class RollCallController {
    @Autowired
    private RollCallService rollCallService;

    @GetMapping("/getStudentList")
    public Result getStudentList(@RequestParam String className, @RequestParam String courseName) {
        try {
            List<Student> studentList = rollCallService.getStudentList(className, courseName);
            return Result.success("点名册获取成功", studentList);
        } catch (Exception e) {
            return Result.error("点名册获取失败：" + e.getMessage());
        }
    }

    @PostMapping("/uploadRecord")
    public Result uploadRecord(@RequestParam String studentId,
                               @RequestParam String studentName,
                               @RequestParam String className,
                               @RequestParam String courseName,
                               @RequestParam String location,
                               @RequestParam MultipartFile faceImage) {
        try {
            boolean flag = rollCallService.uploadRollCallRecord(studentId, studentName, className, courseName, location, faceImage);
            if (flag) return Result.success("点名信息上传成功");
            else return Result.error("点名信息上传失败");
        } catch (Exception e) {
            return Result.error("上传异常：" + e.getMessage());
        }
    }

    @GetMapping("/getRollCallResult")
    public Result getRollCallResult(@RequestParam String className, @RequestParam String courseName) {
        try {
            Map<String, Integer> resultMap = rollCallService.getRollCallResult(className, courseName);
            return Result.success("结果获取成功", resultMap);
        } catch (Exception e) {
            return Result.error("结果获取失败：" + e.getMessage());
        }
    }

    @GetMapping("/randomCheck")
    public Result randomCheck(@RequestParam String className, @RequestParam String courseName) {
        try {
            Student student = rollCallService.randomSelectStudent(className, courseName);
            if (student != null) return Result.success("抽查成功", student);
            else return Result.error("无对应学生信息");
        } catch (Exception e) {
            return Result.error("抽查失败：" + e.getMessage());
        }
    }
}
