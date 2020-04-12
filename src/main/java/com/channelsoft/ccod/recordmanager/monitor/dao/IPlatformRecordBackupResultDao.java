package com.channelsoft.ccod.recordmanager.monitor.dao;

import com.channelsoft.ccod.recordmanager.monitor.po.PlatformRecordBackupResultPo;

import java.util.Date;
import java.util.List;

/**
 * @ClassName: IPlatformRecordBackupResultDao
 * @Author: lanhb
 * @Description: PlatformRecordBackupResultPo的dao接口
 * @Date: 2020/4/12 13:17
 * @Version: 1.0
 */
public interface IPlatformRecordBackupResultDao {
    /**
     * 向数据库添加一条平台录音备份结果
     * @param backupResultPo 需要添加的平台录音备份结果
     * @return 添加记录的主键id
     */
    int insert(PlatformRecordBackupResultPo backupResultPo);

    /**
     * 查询指定时间段的平台录音备份记录
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 查询结果
     */
    List<PlatformRecordBackupResultPo> select(Date startDate, Date endDate);
}
