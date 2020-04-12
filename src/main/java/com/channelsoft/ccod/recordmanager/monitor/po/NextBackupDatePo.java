package com.channelsoft.ccod.recordmanager.monitor.po;

import java.util.Date;

/**
 * @ClassName: NextBackupDatePo
 * @Author: lanhb
 * @Description: 用来记录下一个备份日期的pojo类
 * @Date: 2020/4/12 22:07
 * @Version: 1.0
 */
public class NextBackupDatePo {

    private int id;  //id数据库主键id

    private Date nextBackupDate;  //下一次备份哪天的录音

    private Date updateTime;  //信息更新时间

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getNextBackupDate() {
        return nextBackupDate;
    }

    public void setNextBackupDate(Date nextBackupDate) {
        this.nextBackupDate = nextBackupDate;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
