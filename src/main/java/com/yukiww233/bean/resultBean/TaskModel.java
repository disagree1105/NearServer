package com.yukiww233.bean.resultBean;

/**
 * Created by disagree on 2017/5/3.
 */
public class TaskModel {
    private String _id;
    private String publisherUid;
    private String publisherUsername;
    private String publisherAvatarUrl;
    private long publishTime;
    private String taskDescription;
    private String taskImgUrls;
    private String publishPhoneBrand;
    private double taskLatitude;
    private double taskLongitude;
    private String locationDesc;
    private String likeCount;
    private String favCount;
    private int rewards;
    private int taskStatus;
    private String workerUid;
    private String workerUsername;
    private String workerAvatarUrl;
    private long workTime;
    private long finishTime;

    public TaskModel(String _id, String publisherUid, String publisherUsername, String publisherAvatarUrl, long publishTime, String taskDescription, String taskImgUrls, String publishPhoneBrand, double taskLatitude, double taskLongitude, String locationDesc, String likeCount, String favCount, int rewards, int taskStatus, String workerUid, String workerUsername, String workerAvatarUrl, long workTime, long finishTime) {
        this._id = _id;
        this.publisherUid = publisherUid;
        this.publisherUsername = publisherUsername;
        this.publisherAvatarUrl = publisherAvatarUrl;
        this.publishTime = publishTime;
        this.taskDescription = taskDescription;
        this.taskImgUrls = taskImgUrls;
        this.publishPhoneBrand = publishPhoneBrand;
        this.taskLatitude = taskLatitude;
        this.taskLongitude = taskLongitude;
        this.locationDesc = locationDesc;
        this.likeCount = likeCount;
        this.favCount = favCount;
        this.rewards = rewards;
        this.taskStatus = taskStatus;
        this.workerUid = workerUid;
        this.workerUsername = workerUsername;
        this.workerAvatarUrl = workerAvatarUrl;
        this.workTime = workTime;
        this.finishTime = finishTime;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getPublisherUid() {
        return publisherUid;
    }

    public void setPublisherUid(String publisherUid) {
        this.publisherUid = publisherUid;
    }

    public String getPublisherUsername() {
        return publisherUsername;
    }

    public void setPublisherUsername(String publisherUsername) {
        this.publisherUsername = publisherUsername;
    }

    public String getPublisherAvatarUrl() {
        return publisherAvatarUrl;
    }

    public void setPublisherAvatarUrl(String publisherAvatarUrl) {
        this.publisherAvatarUrl = publisherAvatarUrl;
    }

    public long getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(long publishTime) {
        this.publishTime = publishTime;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public String getTaskImgUrls() {
        return taskImgUrls;
    }

    public void setTaskImgUrls(String taskImgUrls) {
        this.taskImgUrls = taskImgUrls;
    }

    public String getPublishPhoneBrand() {
        return publishPhoneBrand;
    }

    public void setPublishPhoneBrand(String publishPhoneBrand) {
        this.publishPhoneBrand = publishPhoneBrand;
    }

    public double getTaskLatitude() {
        return taskLatitude;
    }

    public void setTaskLatitude(double taskLatitude) {
        this.taskLatitude = taskLatitude;
    }

    public double getTaskLongitude() {
        return taskLongitude;
    }

    public void setTaskLongitude(double taskLongitude) {
        this.taskLongitude = taskLongitude;
    }

    public String getLocationDesc() {
        return locationDesc;
    }

    public void setLocationDesc(String locationDesc) {
        this.locationDesc = locationDesc;
    }

    public String getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(String likeCount) {
        this.likeCount = likeCount;
    }

    public String getFavCount() {
        return favCount;
    }

    public void setFavCount(String favCount) {
        this.favCount = favCount;
    }

    public int getRewards() {
        return rewards;
    }

    public void setRewards(int rewards) {
        this.rewards = rewards;
    }

    public int getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(int taskStatus) {
        this.taskStatus = taskStatus;
    }

    public String getWorkerUid() {
        return workerUid;
    }

    public void setWorkerUid(String workerUid) {
        this.workerUid = workerUid;
    }

    public String getWorkerUsername() {
        return workerUsername;
    }

    public void setWorkerUsername(String workerUsername) {
        this.workerUsername = workerUsername;
    }

    public String getWorkerAvatarUrl() {
        return workerAvatarUrl;
    }

    public void setWorkerAvatarUrl(String workerAvatarUrl) {
        this.workerAvatarUrl = workerAvatarUrl;
    }

    public long getWorkTime() {
        return workTime;
    }

    public void setWorkTime(long workTime) {
        this.workTime = workTime;
    }

    public long getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(long finishTime) {
        this.finishTime = finishTime;
    }
}
