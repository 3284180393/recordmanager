package com.channelsoft.ccod.recordmanager.monitor.service;

import com.channelsoft.ccod.recordmanager.monitor.vo.*;

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

    /**
     * 检查某个企业的录音
     * @param enterpriseVo 需要检查的企业信息
     * @param checkTime 执行检查的时间
     * @param beginTime 录音结束时间不早于此时间
     * @param endTime 录音结束时间不迟于此时间
     * @param entRecordList  该企业指定时间段的录音列表
     * @return 企业录音检查结果
     */
    EntRecordCheckResultVo checkEntRecord(EnterpriseVo enterpriseVo, Date checkTime, Date beginTime, Date endTime, List<RecordDetailVo> entRecordList);

    /**
     * 检查指定企业的录音是否需要被检查/备份
     * @param enterpriseId 企业id
     * @return true:该企业录音需要被检查/备份,false:该企业录音无需检查/备份
     */
    boolean isEnterpriseChosen(String enterpriseId);


}
