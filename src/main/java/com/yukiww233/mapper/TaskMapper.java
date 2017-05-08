package com.yukiww233.mapper;

import com.yukiww233.bean.Task;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

/**
 * Created by disagree on 2017/5/2.
 */
@Mapper
public interface TaskMapper {

    @Insert("insert into task (taskUid,publisherUid,publishTime,phoneBrand,longitude,latitude," +
            "locationDescription,taskDescription,imageList,rewards) values (#{taskUid},#{publisherUid}," +
            "#{publishTime},#{phoneBrand},#{longitude},#{latitude},#{locationDescription},#{taskDescription}," +
            "#{imageList},#{rewards})")
    public void createTask(@Param("taskUid") String taskUid,
                           @Param("publisherUid") String publisherUid,
                           @Param("publishTime") long publishTime,
                           @Param("phoneBrand") String phoneBrand,
                           @Param("longitude") Double longitude,
                           @Param("latitude") Double latitude,
                           @Param("locationDescription") String locationDescription,
                           @Param("taskDescription") String taskDescription,
                           @Param("imageList") String imageList,
                           @Param("rewards")int rewards);

    @Select("select * from task")
    List<Map<String,Object>> queryAll();

    @Select("select publisherUid from task where taskUid=#{taskUid}")
    String getPublisherUid(@Param("taskUid") String taskUid);

    @Select("select status from task where taskUid=#{taskUid}")
    int getStatus(@Param("taskUid") String taskUid);

    @Update("update task set status=1,acceptUid=#{acceptUid},acceptTime=#{acceptTime} where taskUid=#{taskUid}")
    void acceptTask(@Param("taskUid") String taskUid,
                    @Param("acceptUid") String acceptUid,
                    @Param("acceptTime") long acceptTime);



}
