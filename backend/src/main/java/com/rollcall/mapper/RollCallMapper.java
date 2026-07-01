package com.rollcall.mapper;

import com.rollcall.entity.RollCallRecord;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface RollCallMapper {
    @Insert("INSERT INTO roll_call_record(student_id, student_name, class_name, course_name, face_image_url, location, upload_time, is_valid) VALUES(#{studentId}, #{studentName}, #{className}, #{courseName}, #{faceImageUrl}, #{location}, #{uploadTime}, #{isValid})")
    int insertRollCallRecord(RollCallRecord record);

    @Select("SELECT COUNT(*) FROM roll_call_record WHERE class_name = #{className} AND course_name = #{courseName} AND is_valid = 1")
    int countValidRollCallRecord(@Param("className") String className, @Param("courseName") String courseName);
}
