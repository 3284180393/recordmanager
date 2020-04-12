package com.channelsoft.ccod.recordmanager.monitor.dao;

import com.channelsoft.ccod.recordmanager.monitor.po.EntRecordCheckResultPo;

import java.util.Date;
import java.util.List;

/**
 * @ClassName: IEntRecordCheckResultDao
 * @Author: lanhb
 * @Description: EntRecordCheckResultPo的dao接口类
 * @Date: 2020/4/12 14:38
 * @Version: 1.0
 */
public interface IEntRecordCheckResultDao {
    /**
     * 添加一条新的企业录音检查结果
     * @param platformRecordCheckId 该次企业录音检查对应的平台录音检查id
     * @param entRecordCheckResultPo 需要添加的企业录音检查结果
     * @return 添加成功后该记录的id
     */
    int insert(int platformRecordCheckId, EntRecordCheckResultPo entRecordCheckResultPo);

    /**
     * 查询某个时间段内企业录音检查结果
     * @param enterpriseId
     * @param beginTime
     * @param endTime
     * @return
     */
    List<EntRecordCheckResultPo> select(String enterpriseId, Date beginTime, Date endTime);
}
