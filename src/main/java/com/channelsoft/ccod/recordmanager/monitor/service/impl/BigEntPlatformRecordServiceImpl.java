package com.channelsoft.ccod.recordmanager.monitor.service.impl;

import com.channelsoft.ccod.recordmanager.backup.vo.PlatformRecordBackupResultVo;
import com.channelsoft.ccod.recordmanager.monitor.dao.IGlsAgentDao;
import com.channelsoft.ccod.recordmanager.monitor.dao.IRecordDetailDao;
import com.channelsoft.ccod.recordmanager.monitor.service.IPlatformRecordService;
import com.channelsoft.ccod.recordmanager.monitor.vo.GlsAgentVo;
import com.channelsoft.ccod.recordmanager.monitor.vo.PlatformRecordCheckResultVo;
import com.channelsoft.ccod.recordmanager.monitor.vo.RecordDetailVo;
import com.channelsoft.ccod.recordmanager.utils.EnterpriseRecordTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @ClassName: BigEntPlatformRecordServiceImpl
 * @Author: lanhb
 * @Description: 大域企业IPlatformRecordService接口实现类
 * @Date: 2020/4/8 21:09
 * @Version: 1.0
 */
public class BigEntPlatformRecordServiceImpl implements IPlatformRecordService {

    private final static Logger logger = LoggerFactory.getLogger(BigEntPlatformRecordServiceImpl.class);

    @Autowired
    IGlsAgentDao glsAgentDao;

    @Autowired
    IRecordDetailDao recordDetailDao;

    @Value("${ccod.platformId}")
    private String platformId;

    @Value("${ccod.platformName}")
    private String platformName;

    @Override
    public PlatformRecordCheckResultVo check(Date beginTime, Date endTime) {
        List<GlsAgentVo> glsAgentList = this.glsAgentDao.select();
        Map<String, List<GlsAgentVo>> entAgentMap = glsAgentList.stream().collect(Collectors.groupingBy(GlsAgentVo::getEntId));
        for(String entId : entAgentMap.keySet())
        {
            if(!EnterpriseRecordTool.isChosen(entId))
            {
                logger.debug(String.format("%s not been chosen", entId));
                entAgentMap.remove(entId);
            }
        }
        glsAgentList = entAgentMap.values().stream().flatMap(list -> list.stream()).collect(Collectors.toList());
        Map<String, List<GlsAgentVo>> schemaAgentMap = glsAgentList.stream().collect(Collectors.groupingBy(GlsAgentVo::getSchemaName));
        List<RecordDetailVo> recordList = new ArrayList<>();
        for(String schemaName : schemaAgentMap.keySet())
        {
            List<RecordDetailVo> schemaRecordList = recordDetailDao.select(schemaName, beginTime, endTime);
            Map<String, List<RecordDetailVo>> agentRecordMap = schemaRecordList.stream().collect(Collectors.groupingBy(RecordDetailVo::getAgentId));
            Map<String, GlsAgentVo> acceptAgentMap = schemaAgentMap.get(schemaName).stream().collect(Collectors.toMap(GlsAgentVo::getAgentId, Function.identity()));
            for(String agentId : agentRecordMap.keySet())
            {
                if(!acceptAgentMap.containsKey(agentId))
                {
                    logger.warn(String.format("agent %s is not wanted agent for schema %s, so %d record given up",
                            agentId, schemaName, agentRecordMap.get(agentId).size()));
                }
                else
                {
                    recordList.addAll(agentRecordMap.get(agentId));
                }
            }
        }
        return null;
    }

    @Override
    public PlatformRecordBackupResultVo backup(Date backupDate) {
        return null;
    }
}
