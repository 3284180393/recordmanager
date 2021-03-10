package com.channelsoft.ccod.recordmanager.monitor.dao.impl;

import com.channelsoft.ccod.recordmanager.monitor.dao.IRecordDetailDao;
import com.channelsoft.ccod.recordmanager.monitor.po.BakRecordIndex;
import com.channelsoft.ccod.recordmanager.monitor.vo.RecordDetailVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @program: recordmanager
 * @ClassName: MysqlRecordDetailDaoImpl
 * @author: lanhb
 * @description: 用来实现mysql呼叫明细dao接口
 * @create: 2021-03-10 10:04
 **/
@Component(value = "mysqlRecordDetailDao")
public class MysqlRecordDetailDaoImpl extends RecordDetailJdbcBase implements IRecordDetailDao {

    protected Logger logger = LoggerFactory.getLogger(MysqlRecordDetailDaoImpl.class);

    @Autowired
    JdbcTemplate ucdsJdbcTemplate;

    @Override
    public List<RecordDetailVo> select(String schemaName, Date beginTime, Date endTime) {
        return this.queryRecordDetailByJdbc(ucdsJdbcTemplate, schemaName, beginTime, endTime);
    }

    @Override
    public List<BakRecordIndex> select(String entId, List<String> sessionIds) {
        return this.queryBakRecordDetailByJdbc(ucdsJdbcTemplate, entId, sessionIds);
    }

    protected String generateSql(String schemaName, Date beginTime, Date endTime)
    {
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT ");
        sql.append("RD.SESSION_ID AS SESSION_ID,");
        sql.append("RD.START_TIME AS START_TIME,");
        sql.append("RD.END_TIME AS END_TIME,");
        sql.append("RD.AGENT_ID AS AGENT_ID,");
        sql.append("RD.TALK_DURATION AS TALK_DURATION,");
        sql.append("RD.END_TYPE AS END_TYPE,");
        sql.append("RD.CALL_TYPE AS CALLTYPE");
        switch (this.recordType)
        {
            case MIX:
                sql.append(",ERT.RECORD_NAME AS RECORD_INDEX");
                break;
            case COMBINATION:
                sql.append(",ERBT.RECORD_NAME AS RECORD_INDEX");
                break;
            case MIX_AND_COMBINATION:
                sql.append(",ERT.RECORD_NAME AS MIX_RECORD_INDEX");
                sql.append(",ERBT.RECORD_NAME AS COMBINATION_RECORD_INDEX");
                break;
        }
        if (this.hasBak)
        {
            sql.append(",BRT.RECORD_NAME AS RECORD_INDEX_BAK");
        }
        sql.append(" FROM `").append(schemaName).append("`.").append(this.detailTable)
                .append(" RD");
        switch (this.recordType)
        {
            case MIX:
                sql.append(" LEFT JOIN `").append(schemaName).append("`.")
                        .append(this.mixTable).append(" ERT");
                sql.append(" ON RD.SESSION_ID = ERT.SESSION_ID AND ((RD.AGENT_ID = ERT.AGENT_ID) OR (RD.AGENT_ID IS NULL and ERT.AGENT_ID IS NULL))");
                break;
            case COMBINATION:
                sql.append(" LEFT JOIN `").append(schemaName).append("`.")
                        .append(this.combinationTable).append(" ERBT");
                sql.append(" ON RD.SESSION_ID = ERBT.SESSION_ID AND ((RD.AGENT_ID = ERBT.AGENT_ID) OR (RD.AGENT_ID IS NULL and ERBT.AGENT_ID IS NULL))");
                break;
            case MIX_AND_COMBINATION:
                sql.append(" LEFT JOIN `").append(schemaName).append("`.")
                        .append(this.mixTable).append(" ERT");
                sql.append(" ON RD.SESSION_ID = ERT.SESSION_ID AND ((RD.AGENT_ID = ERT.AGENT_ID) OR (RD.AGENT_ID IS NULL and ERT.AGENT_ID IS NULL))");
                sql.append(" LEFT JOIN `").append(schemaName).append("`.")
                        .append(this.combinationTable).append(" ERBT");
                sql.append(" ON RD.SESSION_ID = ERBT.SESSION_ID AND ((RD.AGENT_ID = ERBT.AGENT_ID) OR (RD.AGENT_ID IS NULL and ERBT.AGENT_ID IS NULL))");
                break;
        }
        if (this.hasBak)
        {
            sql.append(" LEFT JOIN `").append(schemaName).append("`.")
                    .append(this.bakTable).append(" BRT");
            sql.append(" ON RD.SESSION_ID = BRT.SESSION_ID AND RD.AGENT_ID = BRT.AGENT_ID");
        }
        SimpleDateFormat sFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		 sql.append(" WHERE 1=1 ");
        sql.append(" WHERE 1=1 AND RD.TALK_DURATION > ")
                .append(this.callCheckRule.getMinTalkDuration())
                .append(String.format(" AND RD.END_TIME >= %d AND RD.END_TIME < %d", beginTime.getTime()/1000, endTime.getTime()/1000));
        if (this.callCheckRule.getCallTypes() != null && this.callCheckRule.getCallTypes().size() > 0)
        {
            sql.append(" AND (RD.CALL_TYPE=").append(this.callCheckRule.getCallTypes().get(0));
            for (int i = 1; i < this.callCheckRule.getCallTypes().size(); i++)
            {
                sql.append(" OR RD.CALL_TYPE=").append(this.callCheckRule.getCallTypes().get(i));
            }
            sql.append(")");
        }
        if (this.callCheckRule.getEndTypes() != null && this.callCheckRule.getEndTypes().size() > 0)
        {
            sql.append(" AND (RD.END_TYPE=").append(this.callCheckRule.getEndTypes().get(0));
            for (int i = 1; i < this.callCheckRule.getEndTypes().size(); i++)
            {
                sql.append(" OR RD.END_TYPE=").append(this.callCheckRule.getEndTypes().get(i));
            }
            sql.append(")");
        }
        return sql.toString();
    }
}
