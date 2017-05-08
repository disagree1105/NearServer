package com.yukiww233.controller;

import com.yukiww233.bean.Task;
import com.yukiww233.bean.resultBean.TaskModel;
import com.yukiww233.mapper.TaskMapper;
import com.yukiww233.mapper.TokenMapper;
import com.yukiww233.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

import static com.yukiww233.utils.Utils.getResult;
import static com.yukiww233.utils.Utils.getResult2;

/**
 * Created by disagree on 2017/5/2.
 */
@RestController
public class TaskController {
    @Autowired
    TaskMapper taskMapper;

    @Autowired
    TokenMapper tokenMapper;

    @Autowired
    UserMapper userMapper;

    @RequestMapping(value = "/task/feed", produces = "application/json")
    public String taskFeed(@RequestParam(value = "token") String token,
                           @RequestParam(value = "longitude") Double longitude,
                           @RequestParam(value = "latitude") Double latitude,
                           @RequestParam(value = "locationDescription") String locationDescription,
                           @RequestParam(value = "size") int size,
                           @RequestParam(value = "page") int page) {
        if (!checkToken(token)) {
            return getResult(1, 0, "token无效！", null);
        }
        List<Map<String, Object>> list = taskMapper.queryAll();
        List<TaskModel> result = new ArrayList<TaskModel>();
        for (int i = 0; i < list.size(); i++) {

            String id = list.get(i).get("taskUid").toString();
            String publisherUid = list.get(i).get("publisherUid").toString();
            Map<String, Object> map = userMapper.getUserInfo(publisherUid);
            String publisherUsername, publisherAvatarUrl;
            if (map == null) {
                publisherUsername = "";
                publisherAvatarUrl = "";
            } else {
                publisherUsername = map.get("username") == null ?
                        "" : map.get("username").toString();
                publisherAvatarUrl = map.get("avatarUrl") == null ?
                        "" : map.get("avatarUrl").toString();
            }
            long publishTime = Long.valueOf(list.get(i).get("publishTime").toString());
            String taskDescription = list.get(i).get("taskDescription").toString();
            String taskImgUrls = list.get(i).get("imageList") == null ?
                    "" : list.get(i).get("imageList").toString();
            String publishPhoneBrand = list.get(i).get("phoneBrand").toString();
            Double taskLongitude2 = Double.valueOf(list.get(i).get("longitude").toString());
            Double taskLatitude2 = Double.valueOf(list.get(i).get("latitude").toString());
            String locationDesc = list.get(i).get("locationDescription").toString();
            String likeCount = list.get(i).get("likeNumber").toString();
            String favCount = list.get(i).get("favNumber").toString();
            int rewards = Integer.valueOf(list.get(i).get("rewards").toString());
            int taskStatus = Integer.valueOf(list.get(i).get("status").toString());
            String workerUid = tokenMapper.selectUid(token);
            Map<String, Object> workerMapper = userMapper.getUserInfo(workerUid);
            String workerUsername, workerAvatarUrl;
            if (workerMapper == null) {
                workerUsername = "";
                workerAvatarUrl = "";
            } else {
                workerUsername = workerMapper.get("username") == null ?
                        "" : workerMapper.get("username").toString();
                workerAvatarUrl = workerMapper.get("avatarUrl") == null ?
                        "" : workerMapper.get("username").toString();
            }
            long workTime = Long.valueOf(list.get(i).get("acceptTime").toString());
            long finishTime = Long.valueOf(list.get(i).get("finishTime").toString());
            TaskModel task = new TaskModel(id, publisherUid, publisherUsername, publisherAvatarUrl,
                    publishTime, taskDescription, taskImgUrls, publishPhoneBrand, taskLatitude2, taskLongitude2, locationDesc,
                    likeCount, favCount, rewards, taskStatus, workerUid, workerUsername, workerAvatarUrl, workTime, finishTime);
            result.add(task);
        }
        //每页size个，第page页
        if ((page - 1) * size >= result.size()) {
            List<Map<String, Object>> emptyList = new ArrayList<Map<String, Object>>();
            return getResult2(1, 1, "查询成功！", emptyList);
        }
        result = result.subList((page - 1) * size, page * size >= result.size() ? result.size() : page * size);
        return getResult2(1, 1, "查询成功！", result);
    }

    @RequestMapping(value = "/task/create", produces = "application/json")
    public String uploadImage(@RequestParam(value = "token") String token,
                              @RequestParam(value = "taskDescription") String taskDescription,
                              @RequestParam(value = "imageList") String imageList,
                              @RequestParam(value = "longitude") Double longitude,
                              @RequestParam(value = "latitude") Double latitude,
                              @RequestParam(value = "locationDescription") String locationDescription,
                              @RequestParam(value = "phoneBrand") String phoneBrand,
                              @RequestParam(value = "rewards") int rewards) {
        String taskUid = UUID.randomUUID().toString();
        if (!checkToken(token)) {
            return getResult(1, 0, "token无效！", null);
        }
        String publisherUid = tokenMapper.selectUid(token);
        long publishTime = new Date().getTime();
        taskMapper.createTask(taskUid, publisherUid, publishTime, phoneBrand, longitude, latitude,
                locationDescription, taskDescription, imageList, rewards);
        return getResult(1, 1, "发布成功！", null);
    }

    @RequestMapping(value = "/task/accept", produces = "application/json")
    public String uploadImage(@RequestParam(value = "token") String token,
                              @RequestParam(value = "taskUid") String taskUid) {
        if (!checkToken(token)) {
            return getResult(1, 0, "token无效！", null);
        }
        if(taskMapper.getPublisherUid(taskUid)==null || taskMapper.getPublisherUid(taskUid).equals("")){
            return getResult(1, 0, "该任务不存在！", null);
        }
        if(taskMapper.getStatus(taskUid)==1){
            return getResult(1, 0, "该任务已被接取！", null);
        }
        String publisherUid=taskMapper.getPublisherUid(taskUid);
        String acceptUid = tokenMapper.selectUid(token);
        if(publisherUid.equals(acceptUid)){
            return getResult(1, 0, "不能接取自己的任务！", null);
        }
        long acceptTime = new Date().getTime();
        taskMapper.acceptTask(taskUid,acceptUid,acceptTime);
        return getResult(1, 1, "任务接取成功！", null);
    }

    private Boolean checkToken(String token) {
        return tokenMapper.selectUid(token) != null;
    }
}
