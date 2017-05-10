package com.yukiww233.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

/**
 * Created by disagree on 2017/5/10.
 */
@Mapper
public interface FavMapper {
    @Insert("insert into fav (uid,taskUid) values (#{uid},#{taskUid})")
    void insertFav(@Param("uid") String uid, @Param("taskUid") String taskUid);

    @Select("select taskUid from fav where uid=#{uid}")
    String[] getTaskUid(@Param("uid") String uid);

    @Select("select * from fav where uid=#{uid} and taskUid=#{taskUid}")
    Map<String, Object> checkFav(@Param("uid") String uid, @Param("taskUid") String taskUid);
}
