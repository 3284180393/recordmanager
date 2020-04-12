package com.channelsoft.ccod.recordmanager.monitor.dao;

import com.channelsoft.ccod.recordmanager.monitor.po.MissBackupRecordDetailPo;

import java.util.Date;
import java.util.List;

/**
 * @ClassName: IMissBackupRecordDetailDao
 * @Author: lanhb
 * @Description: MissBackupRecordDetailPo的dao接口类
 * @Date: 2020/4/12 19:13
 * @Version: 1.0
 */
public interface IMissBackupRecordDetailDao {

    /**
     * 向数据库添加一条应该备份而没有备份录音明细
     * @param platformRecordBackupId 平台备份任务id
     * @param detailPo 需要添加的呼叫明细
     * @return 新添加记录的id
     */
    int insert(int platformRecordBackupId, MissBackupRecordDetailPo detailPo);

    /**
     * 查询指定时间段内应该备份而没有备份录音明细
     * @param beginDate 指定时间段的开始时间
     * @param endDate 指定时间段的结束时间
     * @return 查询结果
     */
    List<MissBackupRecordDetailPo> select(Date beginDate, Date endDate);


}
