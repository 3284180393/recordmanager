package com.channelsoft.ccod.recordmanager.monitor.dao.impl;

import com.channelsoft.ccod.recordmanager.config.DBConstructCfg;
import com.channelsoft.ccod.recordmanager.constant.RecordType;
import com.channelsoft.ccod.recordmanager.monitor.dao.ICallDetailDao;
import com.channelsoft.ccod.recordmanager.monitor.vo.CallDetailVo;

import java.util.Date;
import java.util.List;

/**
 * @ClassName: CallDetailDaoImpl
 * @Author: lanhb
 * @Description: 普通平台的ICallDetailDao接口实现类，适用ucds库为mysql，业务库为单一oracle场景
 * @Date: 2020/4/4 12:56
 * @Version: 1.0
 */
public class CallDetailDaoImpl implements ICallDetailDao {

    private RecordType recordType;

    private DBConstructCfg dbConstructCfg;

    private int[] callTypes;

    private int[] endTypes;

    @Override
    public List<CallDetailVo> select(Date beginTime, Date endTime) {
        return null;
    }
}
