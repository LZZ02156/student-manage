package com.rollcall.mapper;

import com.rollcall.entity.Student;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface StudentMapper {
    @Select("SELECT id, student_id AS studentId, student_name AS studentName, class_name AS className, course_name AS courseName FROM student WHERE class_name = #{className} AND course_name = #{courseName}")
    List<Student> getStudentByClassAndCourse(@Param("className") String className, @Param("courseName") String courseName);

    @Select("SELECT COUNT(*) FROM student WHERE class_name = #{className} AND course_name = #{courseName}")
    int countStudentByClassAndCourse(@Param("className") String className, @Param("courseName") String courseName);
}
