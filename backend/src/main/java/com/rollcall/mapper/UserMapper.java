package com.rollcall.mapper;

import com.rollcall.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserMapper {
    @Select("SELECT id, username, password, role FROM users WHERE username = #{username}")
    User findByUsername(@Param("username") String username);

    @Insert("INSERT INTO users(username, password, role) VALUES(#{username}, #{password}, #{role})")
    int insertUser(User user);

    @Update("UPDATE users SET password = #{password} WHERE username = #{username}")
    int updatePassword(@Param("username") String username, @Param("password") String password);
}
