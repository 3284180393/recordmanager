package com.channelsoft.ccod.recordmanager.monitor.dao;

import com.channelsoft.ccod.recordmanager.monitor.vo.CallDetailVo;

import java.util.Date;
import java.util.List;

/**
 * @ClassName: ICallDetailDao
 * @Author: lanhb
 * @Description: 用来定义呼叫明细的dao接口
 * @Date: 2020/4/3 23:37
 * @Version: 1.0
 */
public interface ICallDetailDao {

    List<CallDetailVo> select(Date beginTime, Date endTime);

}
