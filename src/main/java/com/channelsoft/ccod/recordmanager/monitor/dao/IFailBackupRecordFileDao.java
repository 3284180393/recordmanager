package com.channelsoft.ccod.recordmanager.monitor.dao;

import com.channelsoft.ccod.recordmanager.monitor.po.FailBackupRecordFilePo;

import java.util.Date;
import java.util.List;

/**
 * @ClassName: IFailBackupRecordFileDao
 * @Author: lanhb
 * @Description: FailBackupRecordFilePo的dao接口
 * @Date: 2020/4/12 21:35
 * @Version: 1.0
 */
public interface IFailBackupRecordFileDao {

    /**
     * 向数据库添加一条新的录音文件备份失败记录
     * @param recordFilePo 备份失败的录音文件
     * @return 新添加记录的id
     */
    int insert(FailBackupRecordFilePo recordFilePo);

    /**
     * 查询指定时间段备份失败的录音文件信息
     * @param beginDate 指定时间段的开始时间
     * @param endDate 指定时间的结束时间
     * @return 查询结果
     */
    List<FailBackupRecordFilePo> select(Date beginDate, Date endDate);
}
