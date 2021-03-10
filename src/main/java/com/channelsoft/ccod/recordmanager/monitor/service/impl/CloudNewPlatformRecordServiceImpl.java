package com.channelsoft.ccod.recordmanager.monitor.service.impl;

import com.channelsoft.ccod.recordmanager.config.CloudNewPlatformCondition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * @program: recordmanager
 * @ClassName: CloudNewPlatformRecordServiceImpl
 * @author: lanhb
 * @description: 类型为CLOUD_NEW的平台的平台录音服务接口实现类
 * @create: 2021-03-10 14:09
 **/
@Conditional(CloudNewPlatformCondition.class)
@Service
public class CloudNewPlatformRecordServiceImpl extends PlatformRecordBaseService{

    protected Logger logger = LoggerFactory.getLogger(CloudNewPlatformRecordServiceImpl.class);

    @PostConstruct
    public void init() throws Exception
    {
        logger.info(String.format("platform record service for new cloud which has 1 mysql buz db is been created"));
        cfgCheck();
    }

}
