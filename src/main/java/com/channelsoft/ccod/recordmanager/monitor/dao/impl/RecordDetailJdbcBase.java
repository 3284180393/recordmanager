package com.channelsoft.ccod.recordmanager.monitor.dao.impl;

import com.channelsoft.ccod.recordmanager.config.CallCheckRule;
import com.channelsoft.ccod.recordmanager.constant.RecordType;
import com.channelsoft.ccod.recordmanager.monitor.po.BakRecordIndex;
import com.channelsoft.ccod.recordmanager.monitor.vo.RecordDetailVo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @program: recordmanager
 * @ClassName: RecordDetailJdbcBase
 * @author: lanhb
 * @description: 用来实现一个基于jdbc实现的录音明细查询的基类
 * @create: 2021-03-10 10:09
 **/
public abstract class RecordDetailJdbcBase {

    @Value("${ccod.recordType}")
    protected RecordType recordType;

    @Value("${db.table.detail}")
    protected String detailTable;

    @Value("${db.table.mix}")
    protected String mixTable;

    @Value("${db.table.combination}")
    protected String combinationTable;

    @Value("${db.table.bak}")
    protected String bakTable;

    @Value("${ccod.hasBak}")
    protected boolean hasBak;

    @Value("${debug}")
    protected boolean debug;

    @Autowired
    CallCheckRule callCheckRule;

    protected Logger logger = LoggerFactory.getLogger(RecordDetailJdbcBase.class);

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

    protected List<BakRecordIndex> queryBakRecordDetailByJdbc(JdbcTemplate jdbc, String entId, List<String> sessionIds)
    {
        String sql = String.format("select * from \"%s\".%s where SESSION_ID IN(%s)",
                entId, this.bakTable, sessionIds.stream().map(id->String.format("'%s'",id)).collect(Collectors.joining(",")));
        logger.debug(String.format("begin to query bak record index, sql=%s", sql));
        List<BakRecordIndex> list = jdbc.query(sql, new BakRecordIndexRowMap(entId));
        logger.debug(String.format("find %d bak record index record", list.size()));
        return list;
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
        sql.append("RD.CALLTYPE AS CALLTYPE");
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
        sql.append(" FROM \"").append(schemaName).append("\".").append(this.detailTable)
                .append(" RD");
        switch (this.recordType)
        {
            case MIX:
                sql.append(" LEFT JOIN \"").append(schemaName).append("\".")
                        .append(this.mixTable).append(" ERT");
                sql.append(" ON RD.SESSION_ID = ERT.SESSION_ID AND ((RD.AGENT_ID = ERT.AGENT_ID) OR (RD.AGENT_ID IS NULL and ERT.AGENT_ID IS NULL))");
                break;
            case COMBINATION:
                sql.append(" LEFT JOIN \"").append(schemaName).append("\".")
                        .append(this.combinationTable).append(" ERBT");
                sql.append(" ON RD.SESSION_ID = ERBT.SESSION_ID AND ((RD.AGENT_ID = ERBT.AGENT_ID) OR (RD.AGENT_ID IS NULL and ERBT.AGENT_ID IS NULL))");
                break;
            case MIX_AND_COMBINATION:
                sql.append(" LEFT JOIN \"").append(schemaName).append("\".")
                        .append(this.mixTable).append(" ERT");
                sql.append(" ON RD.SESSION_ID = ERT.SESSION_ID AND ((RD.AGENT_ID = ERT.AGENT_ID) OR (RD.AGENT_ID IS NULL and ERT.AGENT_ID IS NULL))");
                sql.append(" LEFT JOIN \"").append(schemaName).append("\".")
                        .append(this.combinationTable).append(" ERBT");
                sql.append(" ON RD.SESSION_ID = ERBT.SESSION_ID AND ((RD.AGENT_ID = ERBT.AGENT_ID) OR (RD.AGENT_ID IS NULL and ERBT.AGENT_ID IS NULL))");
                break;
        }
        if (this.hasBak)
        {
            sql.append(" LEFT JOIN \"").append(schemaName).append("\".")
                    .append(this.bakTable).append(" BRT");
            sql.append(" ON RD.SESSION_ID = BRT.SESSION_ID AND RD.AGENT_ID = BRT.AGENT_ID");
        }
        SimpleDateFormat sFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		 sql.append(" WHERE 1=1 ");
        sql.append(" WHERE 1=1 AND RD.TALK_DURATION > ")
                .append(this.callCheckRule.getMinTalkDuration())
                .append(" AND RD.END_TIME >= to_date('")
                .append(sFormat.format(beginTime))
                .append("','yyyy-MM-dd HH24:mi:ss') AND RD.END_TIME < to_date('")
                .append(sFormat.format(endTime))
                .append("','yyyy-MM-dd HH24:mi:ss')");
        if (this.callCheckRule.getCallTypes() != null && this.callCheckRule.getCallTypes().size() > 0)
        {
            sql.append(" AND (RD.CALLTYPE=").append(this.callCheckRule.getCallTypes().get(0));
            for (int i = 1; i < this.callCheckRule.getCallTypes().size(); i++)
            {
                sql.append(" OR RD.CALLTYPE=").append(this.callCheckRule.getCallTypes().get(i));
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

    protected class MapRow implements RowMapper<RecordDetailVo>
    {

        @Override
        public RecordDetailVo mapRow(ResultSet rs, int i) throws SQLException
        {
            RecordDetailVo detailVo = new RecordDetailVo();
            detailVo.setRecordType(recordType);
            System.out.println(String.format("start=%s and end=%s", rs.getTimestamp("START_TIME"), rs.getTimestamp("END_TIME")));
            detailVo.setSessionId(rs.getString("SESSION_ID"));
            detailVo.setStartTime(rs.getTimestamp("START_TIME"));
            detailVo.setEndTime(rs.getTimestamp("END_TIME"));
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

    protected class BakRecordIndexRowMap implements RowMapper<BakRecordIndex> {
        String entId;
        BakRecordIndexRowMap(String entId) {
            this.entId = entId;
        }
        @Override
        public BakRecordIndex mapRow(ResultSet rs, int i) throws SQLException {
            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            BakRecordIndex po = new BakRecordIndex();
            po.setRecordName(rs.getString("RECORD_NAME"));
            po.setEntId(entId);
            po.setSessionId(rs.getString("SESSION_ID"));
            po.setRemoteUri(rs.getString("REMOTE_URI"));
            po.setLocalUri(rs.getString("LOCAL_URI"));
            po.setAgentId(rs.getString("AGENT_ID"));
            po.setCmsName(rs.getString("CMS_NAME"));
            po.setCallType(rs.getInt("CALL_TYPE"));
            po.setDeviceNumber(rs.getString("DEVICE_NUMBER"));
            try {
                po.setStartTime(sf.parse(rs.getString("START_TIME")));
                po.setEndTime(sf.parse(rs.getString("END_TIME")));
            } catch (Exception ex) {
                logger.error("parse start or end time error", ex);
            }
            try {
                po.setCtiStartTime(sf.parse(rs.getString("CTI_START_TIME")));
                po.setCtiEndTime(sf.parse(rs.getString("CTI_END_TIME")));
            } catch (Exception ex) {
                logger.error("parse cti start or end time error", ex);
            }
            po.setSkillName(rs.getString("SKILL_NAME"));
            return po;
        }
    }

}
