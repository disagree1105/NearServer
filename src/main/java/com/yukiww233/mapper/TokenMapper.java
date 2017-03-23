package com.yukiww233.mapper;

import org.apache.ibatis.annotations.*;

/**
 * Created by disagree on 2017/3/20.
 */
@Mapper
public interface TokenMapper {
    @Insert("insert into token (uid,token) values (#{uid},#{token})")
    void insertToken(@Param("uid") String uid, @Param("token") String password);

    @Update("update token set token = #{token} where uid = #{uid}")
    void updateToken(@Param("uid") String uid, @Param("token") String token);

    @Select("select token from token where uid = #{uid}")
    String selectToken(@Param("uid") String uid);

    @Select("select uid from token where token = #{token}")
    String selectUid(@Param("token") String token);

    @Delete("delete from token where token = #{token}")
    void deleteToken(@Param("token") String token);
}
