package com.channelsoft.ccod.recordmanager.monitor.dao;

import com.channelsoft.ccod.recordmanager.monitor.po.PlatformRecordCheckResultPo;

import java.util.Date;
import java.util.List;

/**
 * @ClassName: IPlatformRecordCheckResultDao
 * @Author: lanhb
 * @Description: 用来定义PlatformCheckResultVo的dao接口
 * @Date: 2020/4/11 18:27
 * @Version: 1.0
 */
public interface IPlatformRecordCheckResultDao {

    /**
     * 向数据库添加一条平台录音检查结果
     */
    int insert(PlatformRecordCheckResultPo checkResultVo);

    /**
     * 查询指定时间段的平台录音检查结果
     * @param beginTime
     * @param endTime
     * @return
     */
    List<PlatformRecordCheckResultPo> select(Date beginTime, Date endTime);
}
