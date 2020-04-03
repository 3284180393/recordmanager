package com.channelsoft.ccod.recordmanager.monitor.service;

import com.channelsoft.ccod.recordmanager.monitor.vo.CallDetailVo;

import java.util.Date;
import java.util.List;

/**
 * @ClassName: IPlatformCallService
 * @Author: lanhb
 * @Description: 用来定义同平台呼叫服务相关的接口
 * @Date: 2020/4/3 22:07
 * @Version: 1.0
 */
public interface IPlatformCallService {
    /**
     * 查询指定时间段的平台呼叫
     * @param beginTime 开始时间
     * @param endTime  结束时间
     * @return 指定时间段内平台呼叫
     * @throws Exception
     */
    List<CallDetailVo> query(Date beginTime, Date endTime) throws Exception;
}
