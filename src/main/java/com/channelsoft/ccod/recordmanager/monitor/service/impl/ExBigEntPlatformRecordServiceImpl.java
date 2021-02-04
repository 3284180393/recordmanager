package com.channelsoft.ccod.recordmanager.monitor.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.channelsoft.ccod.recordmanager.config.BigEntPlatformCondition;
import com.channelsoft.ccod.recordmanager.config.BigExEntPlatformCondition;
import com.channelsoft.ccod.recordmanager.config.RecordStoreRule;
import com.channelsoft.ccod.recordmanager.monitor.dao.IDBSchemaDao;
import com.channelsoft.ccod.recordmanager.monitor.dao.IGlsAgentDao;
import com.channelsoft.ccod.recordmanager.monitor.dao.IRecordDetailDao;
import com.channelsoft.ccod.recordmanager.monitor.dao.IUcdsAgentDao;
import com.channelsoft.ccod.recordmanager.monitor.po.BakRecordIndex;
import com.channelsoft.ccod.recordmanager.monitor.vo.*;
import com.channelsoft.ccod.recordmanager.utils.GrokParser;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @ClassName: ExBigEntPlatformRecordServiceImpl
 * @Author: lanhb
 * @Description: 该类大域平台只有一个业务库，但座席不能从gls库的GLS_DB_AGENT_RELATE表获取只能从ucds中获取
 * @Date: 2021/1/27 20:09
 * @Version: 1.0
 */
@Conditional(BigExEntPlatformCondition.class)
@Service
public class ExBigEntPlatformRecordServiceImpl extends BigEntPlatformRecordServiceImpl{

    private final static Logger logger = LoggerFactory.getLogger(ExBigEntPlatformRecordServiceImpl.class);

    @Autowired
    IUcdsAgentDao ucdsAgentDao;

    @Autowired
    IDBSchemaDao dbSchemaDao;

    @PostConstruct
    public void init() throws Exception
    {
        System.out.println("^^^^^^^^^^^^^^^^^^^ex big ent");
    }

    protected List<GlsAgentVo> getValidGlsAgent(){
        List<GlsAgentVo> list = ucdsAgentDao.select();
        logger.info(String.format("find %d agent from ucds", list.size()));
        return list;
    }

    protected List<RecordDetailVo> searchPlatformRecord(Date beginTime, Date endTime, List<GlsAgentVo> agentList) throws Exception
    {
        List<DBSchemaVo> dbSchemas = dbSchemaDao.select().stream().collect(Collectors.groupingBy(DBSchemaVo::getDbName)).get(dbName);
        if(dbSchemas == null){
            throw new Exception(String.format("not find any schema at DB %s", dbName));
        }
        Map<String, List<DBSchemaVo>> schemaEntMap = dbSchemas.stream().collect(Collectors.groupingBy(DBSchemaVo::getSchemaName));
        List<RecordDetailVo> recordList = new ArrayList<>();
        Map<String, List<GlsAgentVo>> agentMap = agentList.stream().collect(Collectors.groupingBy(GlsAgentVo::getAgentId));
        for(String schemaName : schemaEntMap.keySet())
        {
            if(enterpriseCfg.getIgnoreSchemaList() != null && enterpriseCfg.getIgnoreSchemaList().contains(schemaName)){
                logger.debug(String.format("schema %s will be skip and not check", schemaName));
                continue;
            }
            List<RecordDetailVo> schemaRecordList = recordDetailDao.select(schemaName, beginTime, endTime);
            schemaRecordList.forEach(r->r.setSchemaName(schemaName));
            Map<String, List<RecordDetailVo>> agentRecordMap = schemaRecordList.stream().collect(Collectors.groupingBy(RecordDetailVo::getAgentId));
            logger.debug(String.format("schema %s has find %d call details for %d agent %s",
                    schemaName, schemaRecordList.size(), agentRecordMap.size(), String.join(",", agentRecordMap.keySet())));
            List<RecordDetailVo> filters = new ArrayList<>();
            for(String agentId : agentRecordMap.keySet()){
                List<RecordDetailVo> agentDetails = agentRecordMap.get(agentId);
                logger.debug(String.format("find %d call detail for agent %s", agentDetails.size(), agentId));
                List<GlsAgentVo> relativeAgents = agentMap.get(agentId);
                if(relativeAgents == null){
                    logger.error(String.format("not find agent %s at ucds, %d call details are given up", agentId, agentDetails.size()));
                }
                else if(relativeAgents.size() == 1){
                    logger.debug(String.format("agent %s has been unique defined at ucds, %d call details entId set to %s",
                            agentId, agentDetails.size(), relativeAgents.get(0).getEntId()));
                    agentDetails.forEach(d->d.setEnterpriseId(relativeAgents.get(0).getEntId()));
                    filters.addAll(agentDetails);
                }
                else{
                    logger.warn(String.format("agent %s has %d multi definition %s at ucds with %d call details, try to decide entId from recordIndex",
                            agentId, relativeAgents.size(), JSONArray.toJSON(relativeAgents), agentDetails.size()));
                    String entId = parseEntIdFromIndex(agentId, relativeAgents, schemaName, schemaEntMap.get(schemaName), agentDetails);
                    if(!isEnterpriseChosen(entId)){
                        String.format("entId %s is not been chosen, %d record of agent %s are given up", entId, agentDetails.size(), agentId);
                    }
                    else{
                        logger.debug(String.format("%d records of agent %s are been set entId to %s", agentDetails.size(), agentId, entId));
                        agentDetails.forEach(d->d.setEnterpriseId(entId));
                        filters.addAll(agentDetails);
                    }
                }
            }
            int agentCount = filters.stream().collect(Collectors.groupingBy(RecordDetailVo::getAgentId)).size();
            int entCount = filters.stream().collect(Collectors.groupingBy(RecordDetailVo::getEnterpriseId)).size();
            logger.debug(String.format("%d agent at %d enterprise have %d valid call details at schema %s, all count is %d",
                    agentCount, entCount, filters.size(), schemaName, schemaRecordList.size()));
            recordList.addAll(filters);
        }
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
        logger.debug(String.format("%s(%s) find %s record from %s to %s", this.platformName, this.platformId, recordList.size(), sf.format(beginTime), sf.format(endTime)));
        recordList = removeDuplicatedRecordDetail(recordList);
        return recordList;
    }

    List<RecordDetailVo> removeDuplicatedRecordDetail(List<RecordDetailVo> recordList){
        Map<String, List<RecordDetailVo>> sessionMap = recordList.stream().collect(Collectors.groupingBy(RecordDetailVo::getSessionId));
        logger.debug(String.format("%d records has %d sessionId", recordList.size(), sessionMap.size()));
        List<RecordDetailVo> retList = new ArrayList<>();
        for(String sessionId : sessionMap.keySet()){
            List<RecordDetailVo> details = sessionMap.get(sessionId);
            if(details.size() == 1){
                retList.addAll(details);
            }
            else{
                logger.warn(String.format("sessionId %s for agent %s in enterprise %s has %d record, remove duplicate",
                        sessionId, details.get(0).getAgentId(), details.get(0).getEnterpriseId(), details.size()));
                boolean removed = false;
                for(RecordDetailVo detail : details){
                    if(StringUtils.isNotBlank(detail.getRecordIndex())){
                        logger.debug(String.format("detail [agentId=%s, entId=%s, sessionId=%s, schemaName=%s] is OK",
                                detail.getAgentId(), detail.getEnterpriseId(), detail.getSessionId(), detail.getSchemaName()));
                        removed = true;
                        retList.add(detail);
                    }
                    else {
                        logger.debug(String.format("detail [agentId=%s, entId=%s, sessionId=%s, schemaName=%s] is invalid, remove it",
                                detail.getAgentId(), detail.getEnterpriseId(), detail.getSessionId(), detail.getSchemaName()));
                    }
                }
                if(!removed){
                    logger.warn(String.format("all details of [agentId=%s, entId=%s, sessionId=%s] has not recordIndex",
                            details.get(0).getAgentId(), details.get(0).getEnterpriseId(), details.get(0).getSessionId()));
                    recordList.add(details.get(0));
                }
            }
        }
        logger.info(String.format("after remove duplicate %d record remain %d", recordList.size(), retList.size()));
        return retList;
    }

    /**
     * 从录音索引恢复出座席所属的企业id
     * @param agentId 座席id
     * @param namedAgents 座席id关联的可能座席
     * @param schemaName 呼叫明细所属的schema
     * @param schemaEnts schema下的所有企业
     * @param agentRecords 需要从录音明细设置企业id的呼叫明细
     * @return 座席所属的企业id
     */
    protected String parseEntIdFromIndex(String agentId, List<GlsAgentVo>namedAgents, String schemaName,
                                                       List<DBSchemaVo> schemaEnts, List<RecordDetailVo> agentRecords){
        String entId = null;
        for(RecordDetailVo detail : agentRecords){
            if(StringUtils.isBlank(detail.getRecordIndex())){
                continue;
            }
            Map<String, Object> matcher = null;
            for(RecordStoreRule rule : recordStoreCfg.getMaster().getStoreRules()){
                String path = rule.getExample().replace(rule.getRecordIndex(), detail.getRecordIndex());
                matcher = GrokParser.match(rule.getGrokPattern(), path);
                if(matcher != null){
                    break;
                }
            }
            if(matcher == null){
                logger.error(String.format("can not parse entId from recordIndex for record detail %s", JSONObject.toJSONString(detail)));
                continue;
            }
            entId = (String)matcher.get("entId");
            logger.debug(String.format("get entId=%s from recordIndex of record detail %s", entId, JSONObject.toJSON(detail)));
            break;
        }
        if(entId != null){
            logger.debug(String.format("entId of agent %s is %s from recordIndex", agentId, entId));
            return entId;
        }
        Map<String, DBSchemaVo> entMap = schemaEnts.stream().collect(Collectors.toMap(DBSchemaVo::getEntId, Function.identity()));
        for(GlsAgentVo agent : namedAgents){
            if(entMap.containsKey(agent.getEntId())){
                entId = agent.getEntId();
                logger.warn(String.format("entId of agent %s has been decided by schema ent : %s", agentId, entId));
                break;
            }
        }
        if(entId == null){
            entId = namedAgents.get(0).getEntId();
            logger.warn(String.format("entId of agent %s has been chosen by first agent definition : %s", agentId, entId));
        }
        return entId;
    }

    protected List<RecordDetailVo> getRecordDetailFromDB(IRecordDetailDao dao, Date beginTime, Date endTime, List<GlsAgentVo> agentList, List<BakRecordIndex> hasBakNotMasterList)
    {
        Set<String> schemaSet = glsAgentDao.select().stream().filter(a->a.getDbName().equals(dbName)).map(a->a.getSchemaName())
                .collect(Collectors.toSet());
        logger.debug(String.format("find %d schema at Database %s", schemaSet.size(), dbName));
        List<RecordDetailVo> recordList = new ArrayList<>();
        for(String schemaName : schemaSet)
        {
            List<RecordDetailVo> schemaRecordList = dao.select(schemaName, beginTime, endTime);
            List<RecordDetailVo> filters = filterAndSetEntIdForSchemaRecord(schemaName, schemaRecordList, agentList);
            recordList.addAll(filters);
        }
        return recordList;
    }
}
