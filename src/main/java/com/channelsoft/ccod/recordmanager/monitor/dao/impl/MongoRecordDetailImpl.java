package com.channelsoft.ccod.recordmanager.monitor.dao.impl;

import com.channelsoft.ccod.recordmanager.config.MongoBuzCondition;
import com.channelsoft.ccod.recordmanager.monitor.dao.IRecordDetailDao;
import com.channelsoft.ccod.recordmanager.monitor.vo.RecordDetailVo;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * @ClassName: MongoRecordDetailImpl
 * @Author: lanhb
 * @Description: 业务库为mongo的IRecordDetailDao接口实现类
 * @Date: 2020/4/10 10:54
 * @Version: 1.0
 */
@Conditional(MongoBuzCondition.class)
@Component(value = "recordDetailDao")
public class MongoRecordDetailImpl implements IRecordDetailDao {

    @Override
    public List<RecordDetailVo> select(String schemaName, Date beginTime, Date endTime) {
        return null;
    }
}
