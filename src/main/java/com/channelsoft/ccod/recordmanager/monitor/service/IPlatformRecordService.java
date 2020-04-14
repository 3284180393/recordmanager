package com.channelsoft.ccod.recordmanager.monitor.service;

import com.channelsoft.ccod.recordmanager.monitor.po.*;
import com.channelsoft.ccod.recordmanager.monitor.vo.PlatformRecordBackupResultSumVo;
import com.channelsoft.ccod.recordmanager.monitor.vo.PlatformRecordCheckResultSumVo;

import java.util.Date;
import java.util.List;

/**
 * @ClassName: IPlatformRecordService
 * @Author: lanhb
 * @Description: 用来定义平台录音服务的接口类
 * @Date: 2020/4/8 20:54
 * @Version: 1.0
 */
public interface IPlatformRecordService {

    /**
     * 检查某个时间段的平台录音
     * @param beginTime 检查的开始时间
     * @param endTime 检查的结束时间
     * @return 检查结果
     */
    PlatformRecordCheckResultSumVo check(Date beginTime, Date endTime);

    /**
     * 备份指定日期的平台录音
     * @param backupDate 需要备份的日期
     * @return 备份结果
     */
    PlatformRecordBackupResultSumVo backup(Date backupDate) throws Exception;

    /**
     * 查询指定时间段内的平台录音检查结果
     * @param beginTime 开始时间
     * @param endTime 结束时间
     * @return 检查结果
     * @throws Exception
     */
    List<PlatformRecordCheckResultPo> queryPlatformRecordCheckResult(Date beginTime, Date endTime) throws Exception;

    /**
     * 查询指定时间段内所有企业录音检查结果
     * @param beginTime 开始时间
     * @param endTime 结束时间
     * @return 查询结果
     * @throws Exception
     */
    List<EntRecordCheckResultPo> queryEntRecordCheckResult(Date beginTime, Date endTime) throws Exception;

    /**
     * 查询某个企业在某个时间段内检查失败的呼叫明细
     * @param enterpriseId 企业id
     * @param beginTime 开始时间
     * @param endTime 结束时间
     * @return 查询结果
     * @throws Exception
     */
    List<CheckFailRecordDetailPo> queryEntRecordCheckDetail(String enterpriseId, Date beginTime, Date endTime) throws Exception;

    /**
     * 查询平台在某个时间录音备份结果
     * @param beginTime 开始时间
     * @param endTime 结束时间
     * @return 查询结果
     * @throws Exception
     */
    List<PlatformRecordBackupResultPo> queryPlatformRecordBackupResult(Date beginTime, Date endTime) throws Exception;

    /**
     * 查询平台在某个时间段内备份失败的录音文件明细
     * @param beginTime 开始时间
     * @param endTime 结束时间
     * @return 查询结果
     * @throws Exception
     */
    List<MissBackupRecordDetailPo> queryPlatformBackupMissRecordDetail(Date beginTime, Date endTime) throws Exception;

    /**
     * 查询平台在某个时间段内应该备份而未备份的呼叫明细
     * @param beginTime 开始时间
     * @param endTime 结束时间
     * @return 查询结果
     * @throws Exception
     */
    List<FailBackupRecordFilePo> queryPlatformFailBackupFile(Date beginTime, Date endTime) throws Exception;
}
