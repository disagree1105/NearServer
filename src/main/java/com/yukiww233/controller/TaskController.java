package com.yukiww233.controller;

import com.yukiww233.bean.resultBean.TaskModel;
import com.yukiww233.mapper.*;
import com.yukiww233.utils.Utils;
import io.rong.models.CodeSuccessResult;
import io.rong.util.GsonUtil;
import io.rong.util.HostType;
import io.rong.util.HttpUtil;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.HttpURLConnection;
import java.util.*;

import static com.yukiww233.utils.Constant.appKey;
import static com.yukiww233.utils.Constant.appSecret;
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

    @Autowired
    FavMapper favMapper;

    @Autowired
    WalletMapper walletMapper;

    @RequestMapping(value = "/task/feed", produces = "application/json")
    public String taskFeed(@RequestParam(value = "token") String token,
                           @RequestParam(value = "longitude") final Double currLongitude,
                           @RequestParam(value = "latitude") final Double currLatitude,
                           @RequestParam(value = "locationDescription") String locationDescription,
                           @RequestParam(value = "size") int size,
                           @RequestParam(value = "page") int page) {
        if (!checkToken(token)) {
            return getResult(1, 0, "token无效！", null);
        }
        List<Map<String, Object>> list = taskMapper.queryAll();
        List<TaskModel> result = new ArrayList<TaskModel>();
        getList(token, list, result);
        Collections.sort(result, new Comparator<TaskModel>() {

            public int compare(TaskModel t1, TaskModel t2) {

                //o1，o2是list中的Map，可以在其内取得值，按其排序，此例为升序，s1和s2是排序字段值
                Double s1 = Math.pow(currLatitude - t1.getTaskLatitude(), 2) + Math.pow(currLongitude - t1.getTaskLongitude(), 2);
                Double s2 = Math.pow(currLatitude - t2.getTaskLatitude(), 2) + Math.pow(currLongitude - t2.getTaskLongitude(), 2);

                if (s1 > s2) {
                    return 1;
                } else {
                    return -1;
                }
            }
        });
        int index = 0;
        for (int i = 0; i < result.size(); i++) {
            Double distance = Utils.getDistanceOfMeter(currLatitude, currLongitude, result.get(i).getTaskLatitude(), result.get(i).getTaskLongitude());
            if (distance > 3000.0) {
                index = i;
                break;
            }
        }
        List<TaskModel> newList = result.subList(0, index);
        List<TaskModel> oldList = result.subList(index, result.size());
        Collections.sort(newList, new Comparator<TaskModel>() {

            public int compare(TaskModel t1, TaskModel t2) {

                //o1，o2是list中的Map，可以在其内取得值，按其排序，此例为降序，s1和s2是排序字段值
                long s1 = t1.getPublishTime();
                long s2 = t2.getPublishTime();

                if (s1 < s2) {
                    return 1;
                } else {
                    return -1;
                }
            }
        });
        newList.addAll(oldList);
        //每页size个，第page页
        if ((page - 1) * size >= newList.size()) {
            List<Map<String, Object>> emptyList = new ArrayList<Map<String, Object>>();
            return getResult2(1, 1, "查询成功！", emptyList);
        }
        newList = newList.subList((page - 1) * size, page * size >= newList.size() ? newList.size() : page * size);
        return getResult2(1, 1, "查询成功！", newList);
    }


    @RequestMapping(value = "/task/create", produces = "application/json")
    public String createTask(@RequestParam(value = "token") String token,
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
        Double balance = walletMapper.getBalance(publisherUid);
        if (balance < rewards) {
            return getResult(1, 0, "您的余额不足，无法发布任务！", null);
        }
        long publishTime = new Date().getTime();
        taskMapper.createTask(taskUid, publisherUid, publishTime, phoneBrand, longitude, latitude,
                locationDescription, taskDescription, imageList, rewards);
        walletMapper.minusBalance(publisherUid, rewards);
        return getResult(1, 1, "发布成功！", null);
    }

    public String acceptTask(String token, String taskUid) {
        return "success";
    }

    @RequestMapping(value = "/task/accept", produces = "application/json")
    public String acceptTask2(@RequestParam(value = "token") String token,
                              @RequestParam(value = "taskUid") String taskUid) {
        if (!checkToken(token)) {
            return getResult(1, 0, "token无效！", null);
        }
        if (taskMapper.getPublisherUid(taskUid) == null || taskMapper.getPublisherUid(taskUid).equals("")) {
            return getResult(1, 0, "该任务不存在！", null);
        }
        if (taskMapper.getStatus(taskUid) == 1) {
            return getResult(1, 0, "该任务已被接取！", null);
        }
        String publisherUid = taskMapper.getPublisherUid(taskUid);
        String username = userMapper.getUsername(publisherUid);
        String acceptUid = tokenMapper.selectUid(token);
        if (publisherUid.equals(acceptUid)) {
            return getResult(1, 0, "不能接取自己的任务！", null);
        }
        long acceptTime = new Date().getTime();
        taskMapper.acceptTask(taskUid, acceptUid, acceptTime);
        try {
            HttpURLConnection conn = HttpUtil.CreatePostHttpConnection(HostType.API, appKey, appSecret, "/push.json", "application/json");
            HttpUtil.setBodyParameter("{\"platform\":[\"android\"]," +
                    "\"audience\":{\"tag\":[],\"userid\":[" + username + "]," +
                    "\"is_to_all\":false},\"notification\":{\"alert\":\"你发布的任务已被接取\"," +
                    "\"android\":{\"alert\":\"你发布的任务已被接取\"}}}", conn);
            CodeSuccessResult codeSuccessResult = (CodeSuccessResult) GsonUtil.fromJson(HttpUtil.returnResult(conn), CodeSuccessResult.class);
            System.out.print(codeSuccessResult.getErrorMessage());
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        } finally {

        }

        return getResult(1, 1, "任务接取成功！", null);
    }

    @RequestMapping(value = "/task/finish", produces = "application/json")
    public String finishTask(@RequestParam(value = "token") String token,
                             @RequestParam(value = "taskUid") String taskUid) {
        if (!checkToken(token)) {
            return getResult(1, 0, "token无效！", null);
        }
        if (taskMapper.getPublisherUid(taskUid) == null || taskMapper.getPublisherUid(taskUid).equals("")) {
            return getResult(1, 0, "该任务不存在！", null);
        }
        if (taskMapper.getStatus(taskUid) == 0) {
            return getResult(1, 0, "该任务还没有被接取，无法确认完成！", null);
        }
        if (taskMapper.getStatus(taskUid) == 2) {
            return getResult(1, 0, "该任务已被完成！", null);
        }
        String publisherUid = taskMapper.getPublisherUid(taskUid);
        String finishUid = tokenMapper.selectUid(token);
        if (!publisherUid.equals(finishUid)) {
            return getResult(1, 0, "这不是您发布的任务，无法确认完成！", null);
        }

        if (walletMapper.getBalance(finishUid) == null) {
            return getResult(1, 0, "该用户没有钱包！", null);
        }
        Double balance = walletMapper.getBalance(finishUid);
        if (balance < taskMapper.getRewards(taskUid)) {
            return getResult(1, 0, "您的余额不足，无法确认完成！", null);
        }
        long finishTime = new Date().getTime();
        taskMapper.finishTask(taskUid, finishTime);
        String acceptUid = taskMapper.getAcceptUid(taskUid);
        int rewards = taskMapper.getRewards(taskUid);
        walletMapper.addBalance(acceptUid, rewards);
        String username = userMapper.getUsername(acceptUid);
        try {
            HttpURLConnection conn = HttpUtil.CreatePostHttpConnection(HostType.API, appKey, appSecret, "/push.json", "application/json");
            HttpUtil.setBodyParameter("{\"platform\":[\"android\"],\"audience\":{\"tag\":[],\"userid\":[" + username + "],\"is_to_all\":false},\"notification\":{\"alert\":\"你接取的任务已被发布者确认完成！\",\"android\":{\"alert\":\"你接取的任务已被发布者确认完成！\"}}}", conn);
            CodeSuccessResult codeSuccessResult = (CodeSuccessResult) GsonUtil.fromJson(HttpUtil.returnResult(conn), CodeSuccessResult.class);
            System.out.print(codeSuccessResult.getErrorMessage());
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        } finally {

        }

        return getResult(1, 1, "任务确认完成成功！", null);
    }

    @RequestMapping(value = "/publishTask/get", produces = "application/json")
    public String getPublishTask(@RequestParam(value = "token") String token) {
        if (!checkToken(token)) {
            return getResult(1, 0, "token无效！", null);
        }
        String uid = tokenMapper.selectUid(token);
        List<Map<String, Object>> list = taskMapper.getPublishTask(uid);
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
            String workerUid = list.get(i).get("acceptUid").toString();
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
        return getResult2(1, 1, "查询成功！", result);
    }

    @RequestMapping(value = "/acceptTask/get", produces = "application/json")
    public String getAcceptTask(@RequestParam(value = "token") String token) {
        if (!checkToken(token)) {
            return getResult(1, 0, "token无效！", null);
        }
        String uid = tokenMapper.selectUid(token);
        List<Map<String, Object>> list = taskMapper.getAcceptTask(uid);
        List<TaskModel> result = new ArrayList<TaskModel>();
        getList(token, list, result);
        return getResult2(1, 1, "查询成功！", result);
    }

    private void getList(@RequestParam(value = "token") String token, List<Map<String, Object>> list, List<TaskModel> result) {
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
    }

    @RequestMapping(value = "/taskNumber/get", produces = "application/json")
    public String getTaskNumber(@RequestParam(value = "token") String token) {
        if (!checkToken(token)) {
            return getResult(1, 0, "token无效！", null);
        }
        String uid = tokenMapper.selectUid(token);
        Map<String, Integer> map = new HashedMap();
        map.put("publishCount", taskMapper.getPublishNumber(uid));
        map.put("favCount", favMapper.favCount(uid));
        map.put("acceptCount", taskMapper.getAcceptNumber(uid));
        return getResult2(1, 1, "查询成功！", map);
    }

    @RequestMapping(value = "/task/fav", produces = "application/json")
    public String favTask(@RequestParam(value = "token") String token,
                          @RequestParam(value = "taskUid") String taskUid) {
        if (!checkToken(token)) {
            return getResult(1, 0, "token无效！", null);
        }
        String uid = tokenMapper.selectUid(token);
        if (favMapper.checkFav(uid, taskUid) != null) {
            return getResult(1, 0, "你已经收藏过了！", null);
        }
        favMapper.insertFav(uid, taskUid);
        taskMapper.addFav(taskUid);
        return getResult2(1, 1, "收藏成功！", null);
    }

    @RequestMapping(value = "/task/like", produces = "application/json")
    public String likeTask(@RequestParam(value = "token") String token,
                           @RequestParam(value = "taskUid") String taskUid) {
        if (!checkToken(token)) {
            return getResult(1, 0, "token无效！", null);
        }
        taskMapper.addLike(taskUid);
        return getResult2(1, 1, "点赞成功！", null);
    }

    @RequestMapping(value = "/favTask/get", produces = "application/json")
    public String getFavTask(@RequestParam(value = "token") String token) {
        if (!checkToken(token)) {
            return getResult(1, 0, "token无效！", null);
        }
        String uid = tokenMapper.selectUid(token);
        String[] taskUids = favMapper.getTaskUid(uid);
        List<TaskModel> list = new ArrayList<TaskModel>();
        for (String taskUid : taskUids) {
            Map<String, Object> taskmap = taskMapper.getTask(taskUid);
            String id = taskmap.get("taskUid").toString();
            String publisherUid = taskmap.get("publisherUid").toString();
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
            long publishTime = Long.valueOf(taskmap.get("publishTime").toString());
            String taskDescription = taskmap.get("taskDescription").toString();
            String taskImgUrls = taskmap.get("imageList") == null ?
                    "" : taskmap.get("imageList").toString();
            String publishPhoneBrand = taskmap.get("phoneBrand").toString();
            Double taskLongitude2 = Double.valueOf(taskmap.get("longitude").toString());
            Double taskLatitude2 = Double.valueOf(taskmap.get("latitude").toString());
            String locationDesc = taskmap.get("locationDescription").toString();
            String likeCount = taskmap.get("likeNumber").toString();
            String favCount = taskmap.get("favNumber").toString();
            int rewards = Integer.valueOf(taskmap.get("rewards").toString());
            int taskStatus = Integer.valueOf(taskmap.get("status").toString());
            String workerUid = taskmap.get("acceptUid").toString();
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
            long workTime = Long.valueOf(taskmap.get("acceptTime").toString());
            long finishTime = Long.valueOf(taskmap.get("finishTime").toString());
            TaskModel task = new TaskModel(id, publisherUid, publisherUsername, publisherAvatarUrl,
                    publishTime, taskDescription, taskImgUrls, publishPhoneBrand, taskLatitude2, taskLongitude2, locationDesc,
                    likeCount, favCount, rewards, taskStatus, workerUid, workerUsername, workerAvatarUrl, workTime, finishTime);
            list.add(task);
        }
        return getResult2(1, 1, "查询成功！", list);
    }


    private Boolean checkToken(String token) {
        return tokenMapper.selectUid(token) != null;
    }
}
