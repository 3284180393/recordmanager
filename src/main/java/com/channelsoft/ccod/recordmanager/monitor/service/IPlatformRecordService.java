package com.channelsoft.ccod.recordmanager.monitor.service;

import com.channelsoft.ccod.recordmanager.monitor.vo.PlatformRecordBackupResultSumVo;
import com.channelsoft.ccod.recordmanager.monitor.vo.PlatformRecordCheckResultSumVo;

import java.util.Date;

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
}
