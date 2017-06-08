package com.yukiww233.controller;

import com.yukiww233.mapper.PasswordMapper;
import com.yukiww233.mapper.TokenMapper;
import com.yukiww233.mapper.UserMapper;
import com.yukiww233.mapper.WalletMapper;
import com.yukiww233.utils.Utils;
import io.rong.RongCloud;
import io.rong.models.TokenResult;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.yukiww233.utils.Constant.appKey;
import static com.yukiww233.utils.Constant.appSecret;
import static com.yukiww233.utils.Utils.getResult;
import static com.yukiww233.utils.Utils.getResult2;

/**
 * Created by disagree on 2017/3/14.
 */
@RestController
public class UserController {
    @Autowired
    PasswordMapper passwordMapper;

    @Autowired
    UserMapper userMapper;

    @Autowired
    TokenMapper tokenMapper;

    @Autowired
    WalletMapper walletMapper;


    @RequestMapping(value = "/uploadImage", produces = "application/json")
    public String uploadImage(@RequestParam(value = "file") File file) {
        return "";
    }

    @RequestMapping(value = "/avatarUrl/update",produces ="application/json")
    public String updateAvatarUrl(@RequestParam(value = "token") String token,
                                  @RequestParam(value = "url")String url){
        if (!checkToken(token)) {
            return getResult(1, 0, "token无效！", null);
        }
        String uid=tokenMapper.selectUid(token);
        userMapper.updateUrl(url,uid);
        return getResult(1, 1, "修改成功！", null);
    }

    @RequestMapping(value = "/register", produces = "application/json")
    public String register(@RequestParam(value = "userName") String username,
                           @RequestParam(value = "encryptPassword") String password,
                           @RequestParam(value = "phoneNumber") String phoneNumber) {
        if (Utils.checkAllNumbers(username)) {
            return getResult(1, 0, "用户名不能为纯数字！", null);
        }
        String uid = UUID.randomUUID().toString();
        if (userMapper.findUserName(username) != null) {
            return getResult(1, 0, "该用户名已存在！", null);
        }
        if (userMapper.findPhoneNumber(phoneNumber) != null) {
            return getResult(1, 0, "该手机号已注册！", null);
        }
        passwordMapper.register(uid, password);
        userMapper.register(uid, username, phoneNumber);
        walletMapper.insert(uid, 1000.00);
        return getResult(1, 1, "注册成功！", null);
    }


    @RequestMapping(value = "/login", produces = "application/json")
    public String login(@RequestParam(value = "userName") String username,
                        @RequestParam(value = "encryptPassword") String password) {
        if (userMapper.findUserName(username) == null && userMapper.findPhoneNumber(username) == null) {
            return getResult(1, 0, "该用户名不存在或手机号未注册！", null);
        } else {
            String uid = "";
            if (userMapper.findUserName(username) != null) {
                System.out.println(userMapper.findUserName(username));
                uid = userMapper.findUserName(username).get("uid").toString();
            }
            if (userMapper.findPhoneNumber(username) != null) {
                uid = userMapper.findPhoneNumber(username).get("uid").toString();
            }
            if (password.equals(passwordMapper.findPassword(uid))) {
                Map<String, Object> map = new HashMap<String, Object>();
                String token = UUID.randomUUID().toString().replaceAll("\\-", "");
                map.putAll(userMapper.getUserInfo(uid));
                map.put("token", token);
                if (tokenMapper.selectToken(uid) != null)
                    tokenMapper.updateToken(uid, token);
                else
                    tokenMapper.insertToken(uid, token);
                return getResult(1, 1, "登录成功！", map);
            }
        }
        return getResult(1, 0, "密码错误！", null);
    }

    @RequestMapping(value = "/userInfo/get", produces = "application/json")
    public String getUserInfo(@RequestParam(value = "token") String token) {
        if (!checkToken(token)) {
            return getResult(1, 0, "token无效！", null);
        }
        String uid = tokenMapper.selectUid(token);
        return getResult(1, 1, "查询成功！", userMapper.getUserInfo(uid));
    }

    @RequestMapping(value ="userInfo2/get", produces = "application/json")
    public String getUserInfo2(@RequestParam(value = "uid") String uid){
        if(userMapper.getUserInfo2(uid)==null){
            return getResult(1, 0, "uid无效！", null);
        }
        return getResult(1, 1, "查询成功！", userMapper.getUserInfo2(uid));
    }

    public Map<String, Object> getBalance(String token) {
        Map<String, Object> map = new HashedMap();
        map.put("result", "success");
        map.put("balance", "100.1");
        return map;
    }

    @RequestMapping(value = "/userInfo/update", produces = "application/json")
    public String updateUserInfo(@RequestParam(value = "token") String token,
                                 @RequestParam(value = "nickname") String nickname,
                                 @RequestParam(value = "sex") int sex,
                                 @RequestParam(value = "location") String location,
                                 @RequestParam(value = "email") String email,
                                 @RequestParam(value = "avatarUrl")String url) {
        if (!checkToken(token)) {
            return getResult(1, 0, "token无效！", null);
        }
        String uid = tokenMapper.selectUid(token);
        userMapper.update(nickname, sex, location, email, uid,url);
        return getResult(1, 1, "更新成功！", null);
    }

    @RequestMapping(value = "/logout", produces = "application/json")
    public String logOut(@RequestParam(value = "token") String token) {
        if (!checkToken(token)) {
            return getResult(1, 0, "token无效！", null);
        }
        tokenMapper.deleteToken(token);
        return getResult(1, 1, "注销成功！", null);
    }

    @RequestMapping(value = "/balance/get", produces = "application/json")
    public String getBalance2(@RequestParam(value = "token") String token) {
        if (!checkToken(token)) {
            return getResult(1, 0, "token无效！", null);
        }
        String uid = tokenMapper.selectUid(token);
        Map<String, Object> map = new HashedMap();
        map.put("balance", walletMapper.getBalance(uid));
        return getResult2(1, 1, "查询成功！", map);
    }

    @RequestMapping(value = "/getIMToken", produces = "application/json")
    public String getIMToken(@RequestParam(value = "token") String token) {
        return IMToken(token);
    }

    private String IMToken(String token) {
        if (!checkToken(token)) {
            return getResult(1, 0, "token无效！", null);
        }
        String uid = tokenMapper.selectUid(token);
        String username = userMapper.getUserInfo(uid).get("username").toString();
        RongCloud rongCloud = RongCloud.getInstance(appKey, appSecret);
        Map<String, Object> map = new HashMap<String, Object>();
        TokenResult userGetTokenResult = null;
        try {
            // 获取 Token
            userGetTokenResult = rongCloud.user.getToken(username, username, "http://www.rongcloud.cn/images/logo.png");
            map.put("IMToken", userGetTokenResult.getToken());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (userGetTokenResult.getCode() == 200) {
            return getResult(1, 1, userGetTokenResult.getErrorMessage(), map);
        } else
            return getResult(1, 0, userGetTokenResult.getErrorMessage(), null);
    }

    private Boolean checkToken(String token) {
        return tokenMapper.selectUid(token) != null;
    }


}
