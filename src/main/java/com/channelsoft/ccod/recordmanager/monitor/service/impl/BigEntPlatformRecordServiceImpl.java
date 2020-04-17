package com.channelsoft.ccod.recordmanager.monitor.service.impl;

import com.channelsoft.ccod.recordmanager.config.BigEntPlatformCondition;
import com.channelsoft.ccod.recordmanager.monitor.dao.IGlsAgentDao;
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
@Conditional(BigEntPlatformCondition.class)
@Service
public class BigEntPlatformRecordServiceImpl extends PlatformRecordBaseService {

    private final static Logger logger = LoggerFactory.getLogger(BigEntPlatformRecordServiceImpl.class);

    @Autowired
    IGlsAgentDao glsAgentDao;

    protected String dbName = "db1";

    @PostConstruct
    public void init() throws Exception
    {
        cfgCheck();
        System.out.println("222222222222222222222222222222222222222222222222222");
    }

    protected PlatformRecordCheckResultSumVo checkPlatformRecord(Date beginTime, Date endTime) throws Exception
    {
        Date checkTime = new Date();
        List<GlsAgentVo> glsAgentList = queryGLSAgent();
        List<RecordDetailVo> recordList = searchPlatformRecord(beginTime, endTime, glsAgentList);
        List<EntRecordCheckResultSumVo> entRecordCheckResultList = checkBigEntPlatformRecord(recordList, checkTime, beginTime, endTime, glsAgentList);
        PlatformRecordCheckResultSumVo resultVo = new PlatformRecordCheckResultSumVo(this.platformId, this.platformName, checkTime, beginTime, endTime, entRecordCheckResultList);
        return resultVo;
    }

    protected List<GlsAgentVo> queryGLSAgent()
    {
        List<GlsAgentVo> glsAgentList = this.glsAgentDao.select();
        Map<String, List<GlsAgentVo>> entAgentMap = glsAgentList.stream().collect(Collectors.groupingBy(GlsAgentVo::getEntId));
        for(String entId : entAgentMap.keySet())
        {
            if(!isEnterpriseChosen(entId))
            {
                logger.debug(String.format("%s not been chosen, %d agent given up", entId, entAgentMap.get(entId).size()));
                entAgentMap.remove(entId);
            }
        }
        glsAgentList = entAgentMap.values().stream().flatMap(list -> list.stream()).collect(Collectors.toList());
        return glsAgentList;
    }

    protected List<RecordDetailVo> searchPlatformRecord(Date beginTime, Date endTime, List<GlsAgentVo> agentList) throws Exception
    {
        Map<String, List<GlsAgentVo>> dbAgentMap = agentList.stream().collect(Collectors.groupingBy(GlsAgentVo::getDbName));
        List<GlsAgentVo> dbAgentList = new ArrayList<>();
        for(String name : dbAgentMap.keySet())
        {
            if(dbName.equals(name))
            {
                logger.debug(String.format("%d agent on db %s", dbAgentMap.get(name).size(), name));
                dbAgentList.addAll(dbAgentMap.get(name));
            }
            else
            {
                logger.error(String.format("db %s is unknown, %d agent given up", name, dbAgentMap.get(name).size()));
            }
        }
        List<RecordDetailVo> recordList = getRecordDetailFromDB(this.recordDetailDao, beginTime, endTime, dbAgentList);;
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
        logger.debug(String.format("%s(%s) find %s record from %s to %s", this.platformName, this.platformId, recordList.size(), sf.format(beginTime), sf.format(endTime)));
        return recordList;
    }

    protected List<RecordDetailVo> getRecordDetailFromDB(IRecordDetailDao dao, Date beginTime, Date endTime, List<GlsAgentVo> agentList) throws Exception
    {
        Map<String, List<GlsAgentVo>> schemaAgentMap = agentList.stream().collect(Collectors.groupingBy(GlsAgentVo::getSchemaName));
        List<RecordDetailVo> recordList = new ArrayList<>();
        for(String schemaName : schemaAgentMap.keySet())
        {
            List<RecordDetailVo> schemaRecordList = dao.select(schemaName, beginTime, endTime);
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
                    for(RecordDetailVo detailVo : agentRecordMap.get(agentId))
                        detailVo.setEnterpriseId(acceptAgentMap.get(agentId).getEntId());
                    recordList.addAll(agentRecordMap.get(agentId));
                }
            }
        }
        return recordList;
    }

    protected List<EntRecordCheckResultSumVo> checkBigEntPlatformRecord(List<RecordDetailVo> recordList, Date checkTime, Date beginTime, Date endTime, List<GlsAgentVo> agentList)
    {
        Map<String, List<RecordDetailVo>> entRecordMap = recordList.stream().collect(Collectors.groupingBy(RecordDetailVo::getEnterpriseId));
        Map<String, List<GlsAgentVo>> entAgentMap = agentList.stream().collect(Collectors.groupingBy(GlsAgentVo::getEntId));
        List<EntRecordCheckResultSumVo> entRecordCheckResultList = new ArrayList<>();
        for(String enterpriseId : entAgentMap.keySet())
        {
            EnterpriseVo enterpriseVo = new EnterpriseVo();
            enterpriseVo.setEnterpriseId(enterpriseId);
            enterpriseVo.setEnterpriseName(entAgentMap.get(enterpriseId).get(0).getEntName());
            List<RecordDetailVo> entRecordList = entRecordMap.containsKey(enterpriseId) ? entRecordMap.get(enterpriseId) : new ArrayList<>();
            EntRecordCheckResultSumVo entRecordCheckResultVo = checkEntRecord(enterpriseVo, checkTime, beginTime, endTime, entRecordList);
            entRecordCheckResultList.add(entRecordCheckResultVo);
        }
        return entRecordCheckResultList;
    }
}
