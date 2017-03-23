package com.yukiww233.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/*Created by disagree on 2017/3/14.
 */
@Mapper
public interface PasswordMapper {
    @Insert("insert into password (uid,password) values (#{uid},#{password})")
    void register(@Param("uid") String uid, @Param("password") String password);

    @Select("select password from password where uid = #{uid}")
    String findPassword(@Param("uid") String uid);

}
