package com.channelsoft.ccod.recordmanager.monitor.dao;

import com.channelsoft.ccod.recordmanager.monitor.po.BakRecordIndex;
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
     * 从业务库指定的schema查询的录音详情
     * @param schemaName schema名
     * @param beginTime 录音对应的呼叫结束时间不早于此时间
     * @param endTime 录音对应的结束时间不晚于此时间
     * @return 查询结果
     */
    List<RecordDetailVo> select(String schemaName, Date beginTime, Date endTime);

    /**
     * 查询指定企业指定session id的备份录音索引明细
     * @param entId 企业id
     * @param sessionIds session id
     * @return 备份录音索引明细
     */
    List<BakRecordIndex> select(String entId, List<String> sessionIds);
}
