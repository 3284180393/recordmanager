package com.channelsoft.ccod.recordmanager.monitor.service.impl;

import com.channelsoft.ccod.recordmanager.backup.vo.PlatformRecordBackupResultVo;
import com.channelsoft.ccod.recordmanager.monitor.service.IPlatformRecordService;
import com.channelsoft.ccod.recordmanager.monitor.vo.PlatformRecordCheckResultVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * @ClassName: NormalPlatformRecordServiceImpl
 * @Author: lanhb
 * @Description: 普通平台的IPlatformRecordService的接口实现类
 * @Date: 2020/4/8 21:06
 * @Version: 1.0
 */
public class NormalPlatformRecordServiceImpl implements IPlatformRecordService {

    private final static Logger logger = LoggerFactory.getLogger(NormalPlatformRecordServiceImpl.class);

    @Override
    public PlatformRecordCheckResultVo check(Date beginTime, Date endTime) {
        return null;
    }

    @Override
    public PlatformRecordBackupResultVo backup(Date backupDate) {
        return null;
    }
}
