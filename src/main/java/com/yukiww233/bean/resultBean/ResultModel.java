package com.yukiww233.bean.resultBean;

import com.alibaba.fastjson.annotation.JSONType;

/**
 * Created by disagree on 2017/3/20.
 */
public class ResultModel {
    private int ret_code;
    private String message;
    private Object _ret;

    public ResultModel(int ret_code, String message, Object _ret) {
        this.ret_code = ret_code;
        this.message = message;
        this._ret = _ret;
    }

    public int getRet_code() {
        return ret_code;
    }

    public void setRet_code(int ret_code) {
        this.ret_code = ret_code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object get_ret() {
        return _ret;
    }

    public void set_ret(Object _ret) {
        this._ret = _ret;
    }


}
