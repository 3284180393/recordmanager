package com.channelsoft.ccod.recordmanager.monitor.dao.impl;

import com.alibaba.fastjson.JSONObject;
import com.channelsoft.ccod.recordmanager.config.CallCheckRule;
import com.channelsoft.ccod.recordmanager.config.MongoBuzCondition;
import com.channelsoft.ccod.recordmanager.monitor.dao.IRecordDetailDao;
import com.channelsoft.ccod.recordmanager.monitor.po.BakRecordIndex;
import com.channelsoft.ccod.recordmanager.monitor.vo.RecordDetailVo;
import com.mongodb.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: MongoRecordDetailDaoImpl
 * @Author: lanhb
 * @Description: 基于mongodb的IRecordDetailDao接口实现类
 * @Date: 2020/4/16 14:27
 * @Version: 1.0
 */
@Conditional(MongoBuzCondition.class)
@Component(value = "recordDetailDao")
public class MongoRecordDetailDaoImpl implements IRecordDetailDao {

    @Autowired
    CallCheckRule callCheckRule;

    private final static Logger logger = LoggerFactory.getLogger(MongoRecordDetailDaoImpl.class);

    @Value("${spring.datasource.business.conn-str}")
    private String connStr;

    private String user = null;

    private String password = null;

    private String source = null;

    @Value("${debug}")
    private boolean debug;

    @Autowired
    private Environment env;

    @PostConstruct
    public void init() throws Exception
    {
//        mongoTest();
        logger.info(String.format("mongo connStr=%s", connStr));
        if(env.containsProperty("spring.datasource.business.user")){
            this.user = env.getProperty("spring.datasource.business.user");
            logger.info(String.format("mongo user=%s", user));
        }
        if(env.containsProperty("spring.datasource.business.password")){
            this.password = env.getProperty("spring.datasource.business.password");
            logger.info(String.format("mongo password=%s", password));
        }
        if(env.containsProperty("spring.datasource.business.source")){
            this.source = env.getProperty("spring.datasource.business.source");
            logger.info(String.format("mongo source=%s", source));
        }
    }

    @Override
    public List<RecordDetailVo> select(String schemaName, Date beginTime, Date endTime) {
        MongoClient client;
        if(StringUtils.isNotBlank(user) && StringUtils.isNotBlank(password) && StringUtils.isNotBlank(source)){
//        if(StringUtils.isNotBlank(user) && StringUtils.isNotBlank(password)){
            logger.debug(String.format("connect to %s with user=%s and password=%s, source=%s",
                    connStr, user, password, schemaName));
            MongoCredential credential = MongoCredential.createScramSha1Credential(user, source, password.toCharArray());
//            MongoCredential credential = MongoCredential.createCredential(user, schemaName, password.toCharArray());
            List credentials = new ArrayList();
            credentials.add(credential);
            String[] arr = connStr.split("\\:");
            ServerAddress serverAddress = new ServerAddress(arr[0], Integer.parseInt(arr[1]));
            List<ServerAddress> addrs = new ArrayList<>();
            addrs.add(serverAddress);
            client = new MongoClient(addrs, credentials);
        }
        else{
            logger.debug(String.format("connect to %s", connStr));
             client = new MongoClient(connStr);
        }
        try{
//            schemaName = "1002021109";
            DB db = client.getDB(schemaName);
            DBCollection collection = db.getCollection("session_detail");
            List<Integer> callTypes = callCheckRule.getCallTypes();
            List<Integer> endTypes = callCheckRule.getEndTypes();
            BasicDBObject queryObject = new BasicDBObject().append(
                    QueryOperators.AND,
                    new BasicDBObject[] {
                            new BasicDBObject("end_time", new BasicDBObject()
                                    .append(QueryOperators.GT,
                                            beginTime.getTime() + "")),
                            new BasicDBObject("end_time", new BasicDBObject()
                                    .append(QueryOperators.LTE,
                                            endTime.getTime() + "")) });
            if(callCheckRule.getMinTalkDuration() > 0)
            {
                queryObject.append("talk_duration", new BasicDBObject(
                        QueryOperators.GTE, callCheckRule.getMinTalkDuration()));
            }
            if(endTypes != null && endTypes.size() > 0)
            {
                String[] arr = new String[endTypes.size()];
                for (int i = 0; i < endTypes.size(); i++)
                {
                    arr[i] = endTypes.get(i) + "";
                }
                queryObject.append("end_type", new BasicDBObject(
                        QueryOperators.IN, arr));
            }
            if(callTypes != null && callTypes.size() > 0)
            {
                String[] arr = new String[callTypes.size()];
                for (int i = 0; i < callTypes.size(); i++)
                {
                    arr[i] = callTypes.get(i) + "";
                }
                queryObject.append("call_type", new BasicDBObject(
                        QueryOperators.IN, arr));
            }
            logger.debug(String.format("mongo query call detail condition is : %s", JSONObject.toJSONString(queryObject)));
            Cursor cursor = collection.find(queryObject);
            Date firstStartTime = new Date();
            List<RecordDetailVo> recordList = new ArrayList<>();
            while (cursor.hasNext())
            {
                RecordDetailVo recordVo = new RecordDetailVo();
                JSONObject jsonObject = JSONObject.parseObject(cursor
                        .next().toString());
                String agentId = jsonObject.getString("agent_id");
                recordVo.setAgentId(agentId);
                recordVo.setCallType(jsonObject.getIntValue("call_type"));
                recordVo.setEndTime(new Date(Long.parseLong(jsonObject
                        .getString("end_time"))));
                recordVo.setEndType(jsonObject.getInteger("end_type"));
                recordVo.setEnterpriseId(schemaName);
                String sessionId = jsonObject.getString("session_id");
                recordVo.setSessionId(sessionId);
                recordVo.setStartTime(new Date(Long.parseLong(jsonObject
                        .getString("start_time"))));
                if (recordVo.getStartTime().getTime() < firstStartTime
                        .getTime())
                {
                    firstStartTime = recordVo.getStartTime();
                }
                recordVo.setTalkDuration(jsonObject
                        .getIntValue("talk_duration"));
                recordList.add(recordVo);
            }
            logger.debug(String.format("find %d record", recordList.size()));
            Map<String, List<RecordDetailVo>> sessionRecordMap = recordList.stream().collect(Collectors.groupingBy(RecordDetailVo::getSessionId));
            collection = db.getCollection("ent_record_bx_table");
            queryObject = new BasicDBObject().append(
                    "session_id", new BasicDBObject(
                            QueryOperators.IN, sessionRecordMap.keySet()));
            if (callTypes != null && callTypes.size() > 0)
            {
                queryObject.append("call_type", new BasicDBObject(
                        QueryOperators.IN, callTypes));
            }
            List<RecordDetailVo> hasIndexRecordList = new ArrayList<>();
            logger.debug(String.format("mongo query record index condition is : %s", JSONObject.toJSONString(queryObject)));
            cursor = collection.find(queryObject);
            while (cursor.hasNext())
            {
                JSONObject jsonObject = JSONObject.parseObject(cursor
                        .next().toString());
                String agentId = jsonObject.getString("agent_id1");
                String sessionId = jsonObject.getString("session_id");
                for(RecordDetailVo detailVo : sessionRecordMap.get(sessionId))
                {
                    if(agentId.equals(detailVo.getAgentId()))
                    {
                        detailVo.setRecordIndex(jsonObject.getString("record_name"));
                        hasIndexRecordList.add(detailVo);
                    }
                }
            }
            Map<String, List<RecordDetailVo>> indexRecordMap = hasIndexRecordList.stream().collect(Collectors.groupingBy(RecordDetailVo::getRecordIndex));
            indexRecordMap.remove(null);
            collection = db.getCollection("ent_record_fastdfs_url");
            queryObject = new BasicDBObject().append(
                    "record_name", new BasicDBObject(
                            QueryOperators.IN, indexRecordMap.keySet()));
            logger.debug(String.format("mongo query record file fastdfs condition is : %s", JSONObject.toJSONString(queryObject)));
            cursor = collection.find(queryObject);
            while (cursor.hasNext())
            {
                JSONObject jsonObject = JSONObject.parseObject(cursor
                        .next().toString());
                String recordIndex = jsonObject.getString("record_name");
                String fastDfsUrl = jsonObject.getString("fastdfs_url");
                for(RecordDetailVo detailVo : indexRecordMap.get(recordIndex))
                {
                    detailVo.setRecordFileFastDfsUrl(fastDfsUrl);
                }
            }
            return recordList;
        }
        finally {
            client.close();
        }
    }

    @Override
    public List<BakRecordIndex> select(String entId, List<String> sessionIds) {
        return new ArrayList<>();
    }

    public void mongoTest()
    {
        Calendar ca = Calendar.getInstance();
        Date endTime = ca.getTime();
        ca.add(Calendar.YEAR, -5);
        Date startTime = ca.getTime();
        try
        {
            select("20190423", startTime, endTime);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
