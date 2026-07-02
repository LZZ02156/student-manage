package com.rollcall.mapper;

import com.rollcall.entity.RollcallSession;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface RollcallSessionMapper {
    @Insert("INSERT INTO rollcall_session(title, creator, start_time, end_time, require_location, location_lat, location_lng, location_radius_m, require_qr, qr_code) VALUES(#{title},#{creator},#{startTime},#{endTime},#{requireLocation},#{locationLat},#{locationLng},#{locationRadiusM},#{requireQr},#{qrCode})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(RollcallSession session);

    @Select("SELECT * FROM rollcall_session WHERE id = #{id}")
    RollcallSession findById(Long id);

    @Select("SELECT * FROM rollcall_session WHERE creator = #{creator} ORDER BY start_time DESC")
    List<RollcallSession> findByCreator(String creator);

    @Select("SELECT * FROM rollcall_session ORDER BY start_time DESC")
    List<RollcallSession> findAll();
}
