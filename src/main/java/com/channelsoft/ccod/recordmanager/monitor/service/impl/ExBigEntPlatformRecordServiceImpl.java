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
        List<DBSchemaVo> dbSchemas = dbSchemaDao.select();
        List<BakRecordIndex> hasBakAndNotMasters = new ArrayList<>();
        List<RecordDetailVo> recordList = new ArrayList<>();
        for(DBSchemaVo schema : dbSchemas)
        {
            List<RecordDetailVo> schemaRecordList = recordDetailDao.select(schema.getSchemaName(), beginTime, endTime);
            List<RecordDetailVo> filters = filterAndSetEntIdForSchemaRecord(schema.getSchemaName(), schemaRecordList, agentList);
            recordList.addAll(filters);
        }
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
        logger.debug(String.format("%s(%s) find %s record from %s to %s", this.platformName, this.platformId, recordList.size(), sf.format(beginTime), sf.format(endTime)));
        return recordList;
    }

    protected List<RecordDetailVo> filterAndSetEntIdForSchemaRecord(String schemaName, List<RecordDetailVo> schemaRecordList, List<GlsAgentVo> agents){
        logger.debug(String.format("begin to filter %d record detail of schema %s with platformType=EX_BIG_ENT", schemaRecordList.size(), schemaName));
        Map<String, List<RecordDetailVo>> agentRecordMap = schemaRecordList.stream().collect(Collectors.groupingBy(RecordDetailVo::getAgentId));
        Map<String, List<GlsAgentVo>> acceptAgentMap = agents.stream().collect(Collectors.groupingBy(GlsAgentVo::getAgentId));
        List<RecordDetailVo> recordList = new ArrayList<>();
        for(String agentId : agentRecordMap.keySet())
        {
            if(!acceptAgentMap.containsKey(agentId)) {
                logger.warn(String.format("agent %s at schema %s is not wanted agent, so %d record given up",
                        agentId, schemaName, agentRecordMap.get(agentId).size()));
                continue;
            }
            else if(acceptAgentMap.get(agentId).size() == 1){
                String entId = acceptAgentMap.get(agentId).get(0).getEntId();
                agentRecordMap.get(agentId).forEach(r->r.setEnterpriseId(entId));
                recordList.addAll(agentRecordMap.get(entId));
            }
            else {
                logger.warn(String.format("agent %s has multi definition %s, try to parse entId from record index",
                        agentId, JSONArray.toJSONString(acceptAgentMap.get(agentId))));
                List<RecordDetailVo> agentRecords = parseEntIdFromIndex(agentId, acceptAgentMap.get(agentId), schemaName, agentRecordMap.get(agentId));
                recordList.addAll(agentRecords);
            }

        }
        logger.debug(String.format("%d record detail filtered of %s", recordList.size(), schemaName));
        return recordList;
    }

    /**
     * 从录音索引恢复出座席呼叫明细关联的企业id
     * @param agentId 座席id
     * @param namedAgents 座席id关联的可能座席
     * @param schemaName 呼叫明细所属的schema
     * @param agentRecords 需要从录音明细设置企业id的呼叫明细
     * @return 恢复出的有效呼叫记录
     */
    protected List<RecordDetailVo> parseEntIdFromIndex(String agentId, List<GlsAgentVo>namedAgents, String schemaName, List<RecordDetailVo> agentRecords){
        List<RecordDetailVo> retList = new ArrayList<>();
        for(RecordDetailVo detail : agentRecords){
            String entId = null;
            if(StringUtils.isBlank(detail.getRecordIndex())){
                for(GlsAgentVo agent : namedAgents){
                    if(schemaName.equals(agent.getSchemaName())){
                        logger.debug(String.format("entId for not index %s is %s", JSONObject.toJSONString(detail), entId));
                        entId = agent.getEntId();
                    }
                    if(entId == null){
                        logger.error(String.format("can not decide endId for [agentId=%s,sessionId=%s], because not agent with schemaName=%s and agentId=%s",
                                detail.getAgentId(), detail.getSessionId(), schemaName, detail.getAgentId()));
                        continue;
                    }
                }
                entId = namedAgents.get(0).getEntId();
                logger.warn(String.format("[agentId=%s, sessionId=%s] has not recordIndex, set endId=%s",
                        agentId, detail.getSessionId(), entId));
            }
            else{
                Map<String, Object> matcher = null;
                for(RecordStoreRule rule : recordStoreCfg.getMaster().getStoreRules()){
                    String path = rule.getExample().replace(rule.getRecordIndex(), detail.getRecordIndex());
                    matcher = GrokParser.match(rule.getGrokPattern(), path);
                    if(matcher != null){
                        break;
                    }
                }
                if(matcher == null){
                    entId = namedAgents.get(0).getEntId();
                    logger.error(String.format("can not parse recordIndex for record detail %s, set entId=%s",
                            JSONObject.toJSONString(detail), entId));

                }
                else{
                    entId = (String)matcher.get("entId");
                    if(!isEnterpriseChosen(entId)){
                        logger.error(String.format("entId is %s for [agentId=%s, schemaName=%s and sessionId=%s] is not been checked", entId, agentId, schemaName, detail.getSessionId()));
                        continue;
                    }
                }
            }
            detail.setEnterpriseId(entId);
            retList.add(detail);
        }
        Set<String> set = retList.stream().map(d->d.getEnterpriseId()).collect(Collectors.toSet());;
        if(set.size() > 1){
            logger.error(String.format("%d record for agent %s at schema %s belong to %d enterprises %s",
                    retList.size(), agentId, schemaName, set.size(), String.join(",", set)));
        }
        else{
            logger.debug(String.format("%d record for agent %s at schema %s belong to enterprises %s",
                    retList.size(), agentId, schemaName, String.join(",", set)));
        }
        return retList;
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
