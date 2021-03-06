package com.channelsoft.ccod.recordmanager.monitor.service.impl;

import com.channelsoft.ccod.recordmanager.config.BigEntPlatformCondition;
import com.channelsoft.ccod.recordmanager.monitor.dao.BakRecordIndexDao;
import com.channelsoft.ccod.recordmanager.monitor.dao.IGlsAgentDao;
import com.channelsoft.ccod.recordmanager.monitor.dao.IRecordDetailDao;
import com.channelsoft.ccod.recordmanager.monitor.po.BakRecordIndex;
import com.channelsoft.ccod.recordmanager.monitor.vo.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;


import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;
import java.util.*;
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

    @Value("${spring.datasource.business.dbName}")
    protected String dbName;

    @PostConstruct
    public void init() throws Exception
    {
        logger.info(String.format("platform record service for big ent with 1 oracle business database is been created"));
        cfgCheck();
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

    /**
     * 查询座席
     * @return 平台所有座席
     */
    protected List<GlsAgentVo> getValidGlsAgent(){
        List<GlsAgentVo> glsAgentList = this.glsAgentDao.select();
        return glsAgentList;
    }

    protected List<GlsAgentVo> queryGLSAgent()
    {
        List<GlsAgentVo> glsAgentList = getValidGlsAgent();
        Map<String, List<GlsAgentVo>> entAgentMap = glsAgentList.stream().collect(Collectors.groupingBy(GlsAgentVo::getEntId));
        List<GlsAgentVo> retList = new ArrayList<>();
        for(String entId : entAgentMap.keySet())
        {
            if(!isEnterpriseChosen(entId)) {
                logger.debug(String.format("%s not been chosen, %d agent given up", entId, entAgentMap.get(entId).size()));
            }
            else {
                logger.debug(String.format("%s has %d agent", entId, entAgentMap.get(entId).size()));
                retList.addAll(entAgentMap.get(entId));
            }
        }
        entAgentMap = retList.stream().collect(Collectors.groupingBy(GlsAgentVo::getEntId));
        logger.debug(String.format("platform has %d enterprise %d agent", entAgentMap.size(), retList.size()));
        return retList;
    }

    protected List<RecordDetailVo> searchPlatformRecord(Date beginTime, Date endTime, List<GlsAgentVo> agentList) throws Exception
    {
        Map<String, List<GlsAgentVo>> dbAgentMap = agentList.stream().collect(Collectors.groupingBy(GlsAgentVo::getDbName));
        List<GlsAgentVo> dbAgentList = new ArrayList<>();
        for(String name : dbAgentMap.keySet())
        {
            if(dbName.equals(name)) {
                logger.debug(String.format("%d agent on db %s", dbAgentMap.get(name).size(), name));
                dbAgentList.addAll(dbAgentMap.get(name));
            }
            else {
                logger.error(String.format("db %s is unknown, %d agent given up", name, dbAgentMap.get(name).size()));
            }
        }
        List<BakRecordIndex> hasBakAndNotMasters = new ArrayList<>();
        List<RecordDetailVo> recordList = getRecordDetailFromDB(this.recordDetailDao, beginTime, endTime, dbAgentList, hasBakAndNotMasters);;
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
        logger.debug(String.format("%s(%s) find %s record from %s to %s", this.platformName, this.platformId, recordList.size(), sf.format(beginTime), sf.format(endTime)));
        return recordList;
    }

    /**
     * 过滤schema中无效的呼叫明细，并为有效的呼叫明细设置企业id
     * @param schemaName schema名
     * @param schemaRecordList schema下的录音
     * @param agents schema关联座席
     * @return 设置了企业id的有效录音
     */
    protected List<RecordDetailVo> filterAndSetEntIdForSchemaRecord(String schemaName, List<RecordDetailVo> schemaRecordList, List<GlsAgentVo> agents){
        logger.debug(String.format("begin to filter %d record detail of schema %s", schemaRecordList.size(), schemaName));
        Map<String, List<RecordDetailVo>> agentRecordMap = schemaRecordList.stream().collect(Collectors.groupingBy(RecordDetailVo::getAgentId));
        Map<String, GlsAgentVo> acceptAgentMap = agents.stream().collect(Collectors.toMap(GlsAgentVo::getAgentId, Function.identity()));
        List<RecordDetailVo> validRecords = new ArrayList<>();
        List<RecordDetailVo> recordList = new ArrayList<>();
        for(String agentId : agentRecordMap.keySet())
        {
            if(!acceptAgentMap.containsKey(agentId)) {
                logger.warn(String.format("agent %s is not wanted agent for schema %s, so %d record given up",
                        agentId, schemaName, agentRecordMap.get(agentId).size()));
            }
            else
            {
                List<RecordDetailVo> agentRecords = agentRecordMap.get(agentId);
                agentRecords.forEach(r->r.setEnterpriseId(acceptAgentMap.get(agentId).getEntId()));
                logger.debug(String.format("%s(%s) has %d record detail",
                        agentId, acceptAgentMap.get(agentId).getEntId(), agentRecords.size()));
                recordList.addAll(agentRecords);
                validRecords.addAll(agentRecords);
            }
        }
        logger.debug(String.format("%d record detail filtered of %s", recordList.size(), schemaName));
        return recordList;
    }

    protected List<RecordDetailVo> getRecordDetailFromDB(IRecordDetailDao dao, Date beginTime, Date endTime, List<GlsAgentVo> agentList, List<BakRecordIndex> hasBakNotMasterList)    {
        Map<String, List<GlsAgentVo>> schemaAgentMap = agentList.stream().collect(Collectors.groupingBy(GlsAgentVo::getSchemaName));
        List<RecordDetailVo> recordList = new ArrayList<>();
        for(String schemaName : schemaAgentMap.keySet())
        {
            List<RecordDetailVo> schemaRecordList = dao.select(schemaName, beginTime, endTime);
            List<RecordDetailVo> filters = filterAndSetEntIdForSchemaRecord(schemaName, schemaRecordList, schemaAgentMap.get(schemaName));
            recordList.addAll(filters);
            if(filters.size() > 0 && hasBak)
            {
                List<String> sessionIds = filters.stream()
                        .filter(r-> StringUtils.isBlank(r.getRecordIndex()) && StringUtils.isNotBlank(r.getBakRecordIndex()))
                        .map(r->r.getSessionId()).collect(Collectors.toList());
                if(sessionIds.size() > 0)
                {
                    logger.warn(String.format("%d sessions[%s] of schema %s has bak index but not master index",
                            sessionIds.size(), String.join(",", sessionIds), schemaName));
                    List<BakRecordIndex> bakIndexes = dao.select(schemaName, sessionIds);
                    hasBakNotMasterList.addAll(bakIndexes);
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
