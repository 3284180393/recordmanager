package com.channelsoft.ccod.recordmanager.config;

import com.channelsoft.ccod.recordmanager.constant.GlsType;

/**
 * @ClassName: GlsDbCfg
 * @Author: lanhb
 * @Description: 用来定义gls库相关信息
 * @Date: 2021/1/27 20:38
 * @Version: 1.0
 */
public class GlsDbCfg {

    public GlsType type;  //gls数据库类型

    public GlsType getType() {
        return type;
    }

    public void setType(GlsType type) {
        this.type = type;
    }
}
