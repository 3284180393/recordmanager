package com.channelsoft.ccod.recordmanager.monitor.service.impl;

import com.channelsoft.ccod.recordmanager.config.BigEntPlatformCondition;
import com.channelsoft.ccod.recordmanager.config.BigExEntPlatformCondition;
import com.channelsoft.ccod.recordmanager.monitor.dao.IGlsAgentDao;
import com.channelsoft.ccod.recordmanager.monitor.dao.IUcdsAgentDao;
import com.channelsoft.ccod.recordmanager.monitor.vo.GlsAgentVo;
import com.channelsoft.ccod.recordmanager.monitor.vo.RecordDetailVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
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

    @PostConstruct
    public void init() throws Exception
    {
        System.out.println("^^^^^^^^^^^^^^^^^^^ex big ent");
    }

    protected List<GlsAgentVo> getValidGlsAgent(){
        List<GlsAgentVo> list = ucdsAgentDao.select();
        List<GlsAgentVo> glsList = glsAgentDao.select();
        logger.info(String.format("count=%d", glsList.stream().map(a->a.getDbName()).collect(Collectors.toSet()).size()));
        Map<String, List<GlsAgentVo>> glsAgentMap = glsList.stream().collect(Collectors.groupingBy(GlsAgentVo::getAgentId));
        logger.debug(String.format("find %d agent from gls with agentId count %d", glsList.size(), glsAgentMap.size()));
        List<GlsAgentVo> retList = new ArrayList<>();
        for(GlsAgentVo agent : list){
            String agentId = agent.getAgentId();
            String entId = agent.getEntId();
            if(!glsAgentMap.containsKey(agentId)){
                logger.error(String.format("not find agent %s at gls", agentId));
                continue;
            }
            List<GlsAgentVo> agents = glsAgentMap.get(agentId).stream().collect(Collectors.groupingBy(GlsAgentVo::getEntId)).get(agent.getEntId());
            if(agents == null){
                logger.debug(String.format("can not find agent %s at enterprise %s from gls DB", agent.getAgentId(), agent.getEntId()));
            }
            else{
                if(agents.size() > 1){
                    Set<String> set = agents.stream().map(a->a.getDbName()).collect(Collectors.toSet());
                    if(set.size() > 1){
                        logger.error(String.format("agent %s(%s) at multi db %s", agentId, entId, String.join(",", set)));
                        continue;
                    }
                    set = agents.stream().map(a->a.getSchemaName()).collect(Collectors.toSet());
                    if(set.size() > 1){
                        if(set.size() > 1) {
                            logger.error(String.format("agent %s(%s) at multi schemaName %s", agentId, entId, String.join(",", set)));
                            continue;
                        }
                    }
                }
                retList.add(agents.get(0));
            }
        }
        logger.info(String.format("find %d agent from ucds and %d is valid", list.size(), retList.size()));
        return retList;
    }

//    @Override
//    protected List<RecordDetailVo> filterSchemaRecordDetail(String schemaName, List<RecordDetailVo> schemaRecordList, List<GlsAgentVo> agents){
//        Set<String> agentIds = agents.stream().collect(Collectors.groupingBy(GlsAgentVo::getAgentId)).keySet();
//        List<RecordDetailVo> filters = new ArrayList<>();
//        schemaRecordList.stream().collect(Collectors.groupingBy(RecordDetailVo::getAgentId)).forEach((k, v)->{
//            if(!agentIds.contains(k)){
//                logger.debug(String.format("%s not belong to %s, %d record given up", k, schemaName, v.size()));
//            }
//            else{
//                v.forEach(r->r.setEnterpriseId(schemaName));
//                filters.addAll(v);
//            }
//        });
//        return filters;
//    }
}
