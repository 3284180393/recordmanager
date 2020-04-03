package com.channelsoft.ccod.recordmanager.monitor.service.impl;

import com.channelsoft.ccod.recordmanager.config.BigEntPlatformCondition;
import com.channelsoft.ccod.recordmanager.monitor.service.IPlatformCallService;
import com.channelsoft.ccod.recordmanager.monitor.vo.CallDetailVo;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @ClassName: BigEntPlatformCallServiceImpl
 * @Author: lanhb
 * @Description: 大域企业呼叫服务实现类
 * @Date: 2020/4/3 22:13
 * @Version: 1.0
 */
@Conditional(BigEntPlatformCondition.class)
@Service
public class BigEntPlatformCallServiceImpl implements IPlatformCallService {

    @Override
    public List<CallDetailVo> query(Date beginTime, Date endTime) throws Exception {
        return null;
    }
}
