package com.channelsoft.ccod.recordmanager.monitor.service.impl;

import com.channelsoft.ccod.recordmanager.config.BigEnt2DBPlatformCondition;
import com.channelsoft.ccod.recordmanager.monitor.dao.IRecordDetailDao;
import com.channelsoft.ccod.recordmanager.monitor.vo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.stream.Collectors;
import java.util.Map;

/**
 * @ClassName: BigEntWith2BuzDBPlatformRecordServiceImpl
 * @Author: lanhb
 * @Description: 有两个业务库的大域企业IPlatformRecordService接口实现类
 * @Date: 2020/4/8 21:12
 * @Version: 1.0
 */
@Conditional(BigEnt2DBPlatformCondition.class)
@Service
public class BigEntWith2BuzDBPlatformRecordServiceImpl extends BigEntPlatformRecordServiceImpl {

    private final static Logger logger = LoggerFactory.getLogger(BigEntWith2BuzDBPlatformRecordServiceImpl.class);

    private String dbName = "db1";

    private String dbName2 = "db2";

    @Autowired
    protected IRecordDetailDao recordDetail2Dao;

    protected List<RecordDetailVo> searchPlatformRecord(Date beginTime, Date endTime, List<GlsAgentVo> agentList) throws Exception
    {
        Map<String, List<GlsAgentVo>> dbAgentMap = agentList.stream().collect(Collectors.groupingBy(GlsAgentVo::getDbName));
        List<RecordDetailVo> recordList = new ArrayList<>();
        final ExecutorService executor = Executors.newFixedThreadPool(2);
        List<FutureTask<List<RecordDetailVo>>> taskList = new ArrayList<>();
        for(String dbName : dbAgentMap.keySet())
        {
            if(this.dbName.equals(dbName))
            {
                FutureTask<List<RecordDetailVo>> task = new FutureTask<>(() -> {
                    List<RecordDetailVo> queryList = getRecordDetailFromDB(recordDetailDao, beginTime, endTime, dbAgentMap.get(dbName));
                    return queryList;
                });
                taskList.add(task);
                executor.execute(task);
            }
            else if(this.dbName2.equals(dbName))
            {
                FutureTask<List<RecordDetailVo>> task = new FutureTask<>(() -> {
                    List<RecordDetailVo> queryList = getRecordDetailFromDB(recordDetail2Dao, beginTime, endTime, dbAgentMap.get(dbName2));
                    return queryList;
                });
                taskList.add(task);
                executor.execute(task);
            }
        }
        executor.shutdown();
        for(FutureTask<List<RecordDetailVo>> task : taskList)
        {
            List<RecordDetailVo> queryList = task.get();
            recordList.addAll(queryList);
        }
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
        logger.debug(String.format("%s(%s) find %s record from %s to %s", this.platformName, this.platformId, recordList.size(), sf.format(beginTime), sf.format(endTime)));
        return recordList;
    }
}
