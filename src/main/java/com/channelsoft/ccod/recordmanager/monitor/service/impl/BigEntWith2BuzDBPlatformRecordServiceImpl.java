package com.channelsoft.ccod.recordmanager.monitor.service.impl;

import com.channelsoft.ccod.recordmanager.backup.vo.PlatformRecordBackupResultVo;
import com.channelsoft.ccod.recordmanager.monitor.service.IPlatformRecordService;
import com.channelsoft.ccod.recordmanager.monitor.vo.PlatformRecordCheckResultVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * @ClassName: BigEntWith2BuzDBPlatformRecordServiceImpl
 * @Author: lanhb
 * @Description: 有两个业务库的大域企业IPlatformRecordService接口实现类
 * @Date: 2020/4/8 21:12
 * @Version: 1.0
 */
public class BigEntWith2BuzDBPlatformRecordServiceImpl implements IPlatformRecordService {

    private final static Logger logger = LoggerFactory.getLogger(BigEntWith2BuzDBPlatformRecordServiceImpl.class);

    @Override
    public PlatformRecordCheckResultVo check(Date beginTime, Date endTime) {
        return null;
    }

    @Override
    public PlatformRecordBackupResultVo backup(Date backupDate) {
        return null;
    }
}
