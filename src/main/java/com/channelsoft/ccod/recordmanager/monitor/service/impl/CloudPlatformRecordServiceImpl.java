package com.channelsoft.ccod.recordmanager.monitor.service.impl;

import com.channelsoft.ccod.recordmanager.backup.vo.PlatformRecordBackupResultVo;
import com.channelsoft.ccod.recordmanager.monitor.service.IPlatformRecordService;
import com.channelsoft.ccod.recordmanager.monitor.vo.PlatformRecordCheckResultVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * @ClassName: CloudPlatformRecordServiceImpl
 * @Author: lanhb
 * @Description: 云平台IPlatformRecordService接口实现类
 * @Date: 2020/4/8 21:15
 * @Version: 1.0
 */
public class CloudPlatformRecordServiceImpl implements IPlatformRecordService {

    private final static Logger logger = LoggerFactory.getLogger(CloudPlatformRecordServiceImpl.class);

    @Override
    public PlatformRecordCheckResultVo check(Date beginTime, Date endTime) {
        return null;
    }

    @Override
    public PlatformRecordBackupResultVo backup(Date backupDate) {
        return null;
    }
}