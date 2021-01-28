package com.channelsoft.ccod.recordmanager.config;

import com.channelsoft.ccod.recordmanager.constant.DBType;

/**
 * @ClassName: BusinessDBCfg
 * @Author: lanhb
 * @Description: 用来定义业务库相关信息
 * @Date: 2021/1/27 20:39
 * @Version: 1.0
 */
public class BusinessDBCfg {

    public DBType type;  //业务库类型

    public int count;  //业务库数量

    public DBType getType() {
        return type;
    }

    public void setType(DBType type) {
        this.type = type;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
