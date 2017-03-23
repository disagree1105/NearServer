package com.yukiww233.bean.resultBean;

import org.springframework.stereotype.Component;

/**
 * Created by disagree on 2017/3/20.
 */
@Component
public class BaseModel {
    private int ret_code;
    private ResultModel result;

    public BaseModel() {
    }

    public BaseModel(int ret_code, ResultModel result) {
        this.ret_code = ret_code;
        this.result = result;
    }

    public BaseModel(int ret_code1, int ret_code2, String message, Object _ret) {
        this.ret_code = ret_code1;
        this.result = new ResultModel(ret_code2, message, _ret);
    }

    public int getRet_code() {
        return ret_code;
    }

    public void setRet_code(int ret_code) {
        this.ret_code = ret_code;
    }

    public ResultModel getResult() {
        return result;
    }

    public void setResult(ResultModel result) {
        this.result = result;
    }


}
