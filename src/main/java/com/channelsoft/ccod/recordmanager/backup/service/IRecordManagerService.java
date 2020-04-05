package com.channelsoft.ccod.recordmanager.backup.service;

import com.channelsoft.ccod.recordmanager.monitor.vo.EntRecordCheckResultVo;
import com.channelsoft.ccod.recordmanager.monitor.vo.PlatformRecordCheckResultVo;
import com.channelsoft.ccod.recordmanager.monitor.vo.RecordBackupResultVo;

import java.util.Date;
import java.util.List;

/**
 * @ClassName: IRecordManagerService
 * @Author: lanhb
 * @Description: 录音管理服务的接口
 * @Date: 2020/4/5 10:54
 * @Version: 1.0
 */
public interface IRecordManagerService {

    /**
     * 检查某个某个时间段满足条件的呼叫是否生成了录音索引，以及索引对应的录音文件是否存在
     * @param beginTime 开始时间
     * @param endTime 结束时间
     * @return 录音检查结果
     * @throws Exception
     */
    PlatformRecordCheckResultVo check(Date beginTime, Date endTime) throws Exception;

    RecordBackupResultVo backup(Date chosenDate) throws Exception;

}
