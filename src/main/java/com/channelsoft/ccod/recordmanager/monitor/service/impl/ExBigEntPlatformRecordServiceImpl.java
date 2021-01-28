package com.channelsoft.ccod.recordmanager.monitor.service.impl;

import com.channelsoft.ccod.recordmanager.config.BigEntPlatformCondition;
import com.channelsoft.ccod.recordmanager.config.BigExEntPlatformCondition;
import com.channelsoft.ccod.recordmanager.monitor.vo.GlsAgentVo;
import com.channelsoft.ccod.recordmanager.monitor.vo.RecordDetailVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;

import java.util.*;
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

    @Override
    protected List<RecordDetailVo> filterSchemaRecordDetail(String schemaName, List<RecordDetailVo> schemaRecordList, List<GlsAgentVo> agents){
        Set<String> agentIds = agents.stream().collect(Collectors.groupingBy(GlsAgentVo::getAgentId)).keySet();
        List<RecordDetailVo> filters = new ArrayList<>();
        schemaRecordList.stream().collect(Collectors.groupingBy(RecordDetailVo::getAgentId)).forEach((k, v)->{
            if(!agentIds.contains(k)){
                logger.debug(String.format("%s not belong to %s, %d record given up", k, schemaName, v.size()));
            }
            else{
                v.forEach(r->r.setEnterpriseId(schemaName));
                filters.addAll(v);
            }
        });
        return filters;
    }
}
