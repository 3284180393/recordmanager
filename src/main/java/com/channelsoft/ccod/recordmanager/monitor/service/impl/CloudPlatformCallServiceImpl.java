package com.channelsoft.ccod.recordmanager.monitor.service.impl;

import com.channelsoft.ccod.recordmanager.config.CloudPlatformCondition;
import com.channelsoft.ccod.recordmanager.monitor.service.IPlatformCallService;
import com.channelsoft.ccod.recordmanager.monitor.vo.CallDetailVo;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @ClassName: CloudPlatformCallServiceImpl
 * @Author: lanhb
 * @Description: 云平台呼叫服务实现类
 * @Date: 2020/4/3 22:15
 * @Version: 1.0
 */
@Conditional(CloudPlatformCondition.class)
@Service
public class CloudPlatformCallServiceImpl implements IPlatformCallService {

    @Override
    public List<CallDetailVo> query(Date beginTime, Date endTime) throws Exception {
        return null;
    }
}
