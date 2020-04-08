package com.channelsoft.ccod.recordmanager.monitor.dao.impl;

import com.channelsoft.ccod.recordmanager.monitor.dao.IRecordDetailDao;
import com.channelsoft.ccod.recordmanager.monitor.vo.RecordDetailVo;

import java.util.Date;
import java.util.List;

/**
 * @ClassName: BigEntWith2BuzDBRecordDetailDaoImpl
 * @Author: lanhb
 * @Description: 有两个业务库一个gls配置库的大域平台IRecordDetialDao接口实现
 * @Date: 2020/4/8 18:12
 * @Version: 1.0
 */
public class BigEntWith2BuzDBRecordDetailDaoImpl implements IRecordDetailDao {

    @Override
    public List<RecordDetailVo> select(String enterpriseId, Date beginTime, Date endTime) {
        return null;
    }
}
