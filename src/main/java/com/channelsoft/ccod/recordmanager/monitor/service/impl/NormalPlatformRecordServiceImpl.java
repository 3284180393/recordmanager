package com.channelsoft.ccod.recordmanager.monitor.service.impl;

import com.channelsoft.ccod.recordmanager.config.NormalPlatformCondition;
import com.channelsoft.ccod.recordmanager.monitor.dao.IEnterpriseDao;
import com.channelsoft.ccod.recordmanager.monitor.dao.IRecordDetailDao;
import com.channelsoft.ccod.recordmanager.monitor.vo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @ClassName: NormalPlatformRecordServiceImpl
 * @Author: lanhb
 * @Description: 普通平台的IPlatformRecordService的接口实现类
 * @Date: 2020/4/8 21:06
 * @Version: 1.0
 */
@Conditional(NormalPlatformCondition.class)
@Service
public class NormalPlatformRecordServiceImpl extends PlatformRecordBaseService {

    private final static Logger logger = LoggerFactory.getLogger(NormalPlatformRecordServiceImpl.class);

    @PostConstruct
    public void init() throws Exception
    {
        logger.info(String.format("platform record service for normal platform is been created"));
        cfgCheck();
    }
}
