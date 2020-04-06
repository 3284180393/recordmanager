package com.channelsoft.ccod.recordmanager.notify.vo;

import com.alibaba.fastjson.JSON;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName: SendResult
 * @Author: lanhb
 * @Description: 封装发送消息结果实体类
 * @Date: 2020/4/6 11:36
 * @Version: 1.0
 */
public class SendResult {

    private boolean isSuccess;
    private Integer errorCode;
    private String errorMsg;

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    @Override
    public String toString() {
        Map<String, Object> items = new HashMap<String, Object>();
        items.put("errorCode", errorCode);
        items.put("errorMsg", errorMsg);
        items.put("isSuccess", isSuccess);
        return JSON.toJSONString(items);
    }
}
