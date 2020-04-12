package com.channelsoft.ccod.recordmanager.monitor.dao;

import com.channelsoft.ccod.recordmanager.monitor.po.CheckFailRecordDetailPo;

import java.util.Date;
import java.util.List;

/**
 * @ClassName: ICheckFailRecordDetailDao
 * @Author: lanhb
 * @Description: CheckFailRecordDetailPo的dao接口类
 * @Date: 2020/4/12 15:31
 * @Version: 1.0
 */
public interface ICheckFailRecordDetailDao {
    /**
     * 向数据库添加一条录音检查失败记录
     * @param platformRecordCheckId 该条记录对应的平台检查id
     * @param enterpriseRecordCheckId 该条记录对应的企业检查id
     * @param detailPo 需要添加的录音检查失败记录
     * @return 新加记录的主键id
     */
    int insert(int platformRecordCheckId, int enterpriseRecordCheckId, CheckFailRecordDetailPo detailPo);

    /**
     * 查询满足指定条件的平台录音检查记录
     * @param platformCheckId 平台录音检查id，如果为空则忽略该条件
     * @param enterpriseCheckId 企业录音检查id，如果为空则忽略该条件
     * @return 查询结果
     */
    List<CheckFailRecordDetailPo> select(Integer platformCheckId, Integer enterpriseCheckId);

    /**
     * 查询指定企业在某个时间段的录音检查失败记录
     * @param enterpriseId 企业id
     * @param beginTime 开始时间
     * @param endTime 结束时间
     * @return 查询结果
     */
    List<CheckFailRecordDetailPo> select(String enterpriseId, Date beginTime, Date endTime);
}
