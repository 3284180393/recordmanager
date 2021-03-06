package com.channelsoft.ccod.recordmanager.monitor.dao.impl;

import com.channelsoft.ccod.recordmanager.constant.RecordType;
import com.channelsoft.ccod.recordmanager.monitor.dao.IRecordDetailDao;
import com.channelsoft.ccod.recordmanager.monitor.po.BakRecordIndex;
import com.channelsoft.ccod.recordmanager.monitor.vo.RecordDetailVo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
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

    protected List<RecordDetailVo> queryRecordDetailByJdbc(JdbcTemplate jdbc, String schemaName, Date beginTime, Date endTime)
    {
        String sql = generateSql(schemaName, beginTime, endTime);
        logger.debug(String.format("begin to query %s record detail with type=%s from %s to %s, sql=%s",
                schemaName, this.recordType, beginTime, endTime, sql));
        List<RecordDetailVo> retList =jdbc.query(sql, new MapRow());
        logger.debug(String.format("%s has %d record from %s to %s with type=%s",
                schemaName, retList.size(), beginTime, endTime, this.recordType.name));
        return retList;
    }

    protected class MapRow implements RowMapper<RecordDetailVo>
    {

        @Override
        public RecordDetailVo mapRow(ResultSet rs, int i) throws SQLException
        {
            RecordDetailVo detailVo = new RecordDetailVo();
            detailVo.setRecordType(recordType);
            System.out.println(String.format("start=%s and end=%s", rs.getTimestamp("START_TIME"), rs.getTimestamp("END_TIME")));
            detailVo.setSessionId(rs.getString("SESSION_ID"));
            long start = rs.getLong("START_TIME") * 1000L;
            detailVo.setStartTime(new Date(start));
            long end = rs.getLong("END_TIME") * 1000L;
            detailVo.setEndTime(new Date(end));
            detailVo.setAgentId(rs.getString("AGENT_ID"));
            detailVo.setTalkDuration(rs.getInt("TALK_DURATION"));
            detailVo.setEndType(rs.getInt("END_TYPE"));
            detailVo.setCallType(rs.getInt("CALLTYPE"));
            switch (recordType)
            {
                case MIX:
                case COMBINATION:
                    detailVo.setRecordIndex(rs.getString("RECORD_INDEX"));
                    break;
                case MIX_AND_COMBINATION:
                    String mixIndex = rs.getString("MIX_RECORD_INDEX");
                    String combinationIndex = rs.getString("COMBINATION_RECORD_INDEX");
                    if(StringUtils.isNotBlank(mixIndex)) {
                        detailVo.setRecordType(RecordType.MIX);
                        detailVo.setRecordIndex(mixIndex);
                    }
                    else if(StringUtils.isNotBlank(combinationIndex)) {
                        detailVo.setRecordType(RecordType.COMBINATION);
                        detailVo.setRecordIndex(combinationIndex);
                    }
                    else {
                        logger.warn("MIX_RECORD_INDEX and COMBINATION_RECORD_INDEX of %s is blank");
                    }
                    break;
            }
            detailVo.setHasBak(hasBak);
            if (hasBak)
            {
                detailVo.setBakRecordIndex(rs.getString("RECORD_INDEX_BAK"));
            }
            return detailVo;
        }
    }
}
