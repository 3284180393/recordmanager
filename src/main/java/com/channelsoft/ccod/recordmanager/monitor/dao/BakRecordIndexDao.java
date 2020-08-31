package com.channelsoft.ccod.recordmanager.monitor.dao;

import com.channelsoft.ccod.recordmanager.monitor.po.BakRecordIndex;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

/**
 * (BakRecordIndex)表数据库访问层
 *
 * @author makejava
 * @since 2020-08-24 18:45:13
 */
public interface BakRecordIndexDao {

    /**
     * 查询指定条件的录音索引
     * @param beginTime 开始时间
     * @param endTime 结束时间
     * @return 查询结果
     */
    List<BakRecordIndex> select( Date beginTime, Date endTime);

    /**
     * 新增数据
     *
     * @param bakRecordIndex 实例对象
     */
    void insert(BakRecordIndex bakRecordIndex);


}