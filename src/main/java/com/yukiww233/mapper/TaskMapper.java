package com.yukiww233.mapper;

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

    @Select("select * from task where status!= 2")
    List<Map<String,Object>> queryAll();


    @Select("select publisherUid from task where taskUid=#{taskUid}")
    String getPublisherUid(@Param("taskUid") String taskUid);

    @Select("select acceptUid from task where taskUid=#{taskUid}")
    String getAcceptUid(@Param("taskUid") String taskUid);

    @Select("select status from task where taskUid=#{taskUid}")
    int getStatus(@Param("taskUid") String taskUid);

    @Update("update task set status=2,finishTime=#{finishTime} where taskUid=#{taskUid}")
    void finishTask(@Param("taskUid") String taskUid,
                    @Param("finishTime") long finishTime);

    @Update("update task set status=1,acceptUid=#{acceptUid},acceptTime=#{acceptTime} where taskUid=#{taskUid}")
    void acceptTask(@Param("taskUid") String taskUid,
                    @Param("acceptUid") String acceptUid,
                    @Param("acceptTime") long acceptTime);

    @Select("select * from task where publisherUid=#{publisherUid}")
    List<Map<String, Object>> getPublishTask(@Param("publisherUid") String publisherUid);

    @Select("select * from task where acceptUid=#{acceptUid}")
    List<Map<String, Object>> getAcceptTask(@Param("acceptUid") String acceptUid);

    @Select("select * from task where taskUid=#{taskUid}")
    Map<String, Object> getTask(@Param("taskUid") String taskUid);

    @Select("select count(*) from task where publisherUid=#{publisherUid}")
    int getPublishNumber(@Param("publisherUid") String publisherUid);

    @Select("select count(*) from task where acceptUid=#{acceptUid}")
    int getAcceptNumber(@Param("acceptUid") String acceptUid);

    @Update("update task set likeNumber=likeNumber+1 where taskUid=#{taskUid}")
    void addLike(@Param("taskUid") String taskUid);

    @Update("update task set favNumber=favNumber+1 where taskUid=#{taskUid}")
    void addFav(@Param("taskUid") String taskUid);

    @Select("select rewards from task where taskUid=#{taskUid}")
    int getRewards(@Param("taskUid") String taskUid);

}
