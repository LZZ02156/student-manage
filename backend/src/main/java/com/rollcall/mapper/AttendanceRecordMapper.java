package com.rollcall.mapper;

import com.rollcall.entity.AttendanceRecord;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AttendanceRecordMapper {
    @Insert("INSERT INTO attendance_record(session_id, username, status, checkin_time, checkin_lat, checkin_lng, proof_url, note) VALUES(#{sessionId},#{username},#{status},#{checkinTime},#{checkinLat},#{checkinLng},#{proofUrl},#{note})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(AttendanceRecord record);

    @Select("SELECT * FROM attendance_record WHERE session_id = #{sessionId} ORDER BY checkin_time")
    List<AttendanceRecord> findBySessionId(Long sessionId);

    @Select("SELECT * FROM attendance_record WHERE session_id = #{sessionId} AND username = #{username}")
    AttendanceRecord findBySessionAndUser(@Param("sessionId") Long sessionId, @Param("username") String username);
}
