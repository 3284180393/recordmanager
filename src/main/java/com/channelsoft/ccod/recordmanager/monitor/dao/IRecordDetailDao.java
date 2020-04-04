package com.channelsoft.ccod.recordmanager.monitor.dao;

import com.channelsoft.ccod.recordmanager.monitor.vo.RecordDetailVo;

import java.util.Date;
import java.util.List;

/**
 * @ClassName: IRecordDetailDao
 * @Author: lanhb
 * @Description: RecordDetail的dao接口类
 * @Date: 2020/4/4 13:12
 * @Version: 1.0
 */
public interface IRecordDetailDao {

    /**
     * 查询指定时间的录音详情
     * @param enterpriseId 企业id
     * @param beginTime 开始时间
     * @param endTime 结束时间
     * @return 查询结果
     */
    List<RecordDetailVo> select(String enterpriseId, Date beginTime, Date endTime);
}
