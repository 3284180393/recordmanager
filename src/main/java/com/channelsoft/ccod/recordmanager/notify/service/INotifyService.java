package com.channelsoft.ccod.recordmanager.notify.service;

import com.channelsoft.ccod.recordmanager.backup.vo.PlatformRecordBackupResultVo;
import com.channelsoft.ccod.recordmanager.monitor.vo.PlatformRecordCheckResultSumVo;

/**
 * @ClassName: INotifyService
 * @Author: lanhb
 * @Description: 用来定义通知服务的接口类
 * @Date: 2020/4/6 14:29
 * @Version: 1.0
 */
public interface INotifyService {

    /**
     * 通知平台录音检查结果
     * @param checkResultVo 平台录音检查结果
     */
    void notify(PlatformRecordCheckResultSumVo checkResultVo);

    /**
     * 通知平台录音备份结果
     * @param backupResultVo 平台录音备份结果
     */
    void notify(PlatformRecordBackupResultVo backupResultVo);
}
