package com.channelsoft.ccod.recordmanager.config;

/**
 * @ClassName: SysLog
 * @Author: lanhb
 * @Description: 用来定义写syslog日志的配置
 * @Date: 2020/4/15 15:42
 * @Version: 1.0
 */
public class SysLog {
    private boolean write;

    private String tag;

    public boolean isWrite() {
        return write;
    }

    public void setWrite(boolean write) {
        this.write = write;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
