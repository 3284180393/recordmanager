package com.channelsoft.ccod.recordmanager.monitor.dao.impl;

import com.channelsoft.ccod.recordmanager.config.CallCheckRule;
import com.channelsoft.ccod.recordmanager.constant.RecordType;
import com.channelsoft.ccod.recordmanager.monitor.dao.IDBSchemaDao;
import com.channelsoft.ccod.recordmanager.monitor.dao.IRecordDetailDao;
import com.channelsoft.ccod.recordmanager.monitor.vo.DBSchemaVo;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @ClassName: BigEntRecordDetailDaoImpl
 * @Author: lanhb
 * @Description: 用来实现单业务库的大域企业录音查询
 * @Date: 2020/4/8 15:09
 * @Version: 1.0
 */
public class BigEntRecordDetailDaoImpl implements IRecordDetailDao {

    private final static Logger logger = LoggerFactory.getLogger(RecordDetailDaoImpl.class);

    @Autowired
    JdbcTemplate glsJdbcTemplate;

    @Autowired
    JdbcTemplate buzJdbcTemplate;

    @Autowired
    IDBSchemaDao dbSchemaDao;

    @Value("${ccod.recordType}")
    private RecordType recordType;

    @Value("${db.gls.name}")
    private String glsDBName;

    @Value("${db.business.name}")
    private String buzDBName;

    @Value("${db.table.detail}")
    private String detailTable;

    @Value("${db.table.mix}")
    private String mixTable;

    @Value("${db.table.combination}")
    private String combinationTable;

    @Value("${db.table.bak}")
    private String bakTable;

    @Value("${db.table.dbAgentRelate}")
    private String dbAgentRelateTable;

    @Value("${ccod.hasBak}")
    private boolean hasBak;

    @Autowired
    CallCheckRule callCheckRule;

    @Override
    public List<RecordDetailVo> select(String enterpriseId, Date beginTime, Date endTime) {
        List<DBSchemaVo> schemaList = this.dbSchemaDao.select();
        List<RecordDetailVo> retList = new ArrayList<>();
        for(DBSchemaVo schemaVo : schemaList)
        {
            String sql = generateSql(schemaVo.getSchemaName(), beginTime, endTime);
            List<RecordDetailVo> queryList = this.buzJdbcTemplate.query(sql, new MapRow());
            logger.debug(String.format("find %d record at schema %s", queryList.size(), schemaVo.getSchemaName()));
            retList.addAll(queryList);
        }
        return retList;
    }

    private String generateSql(String schemaName,Date beginTime, Date endTime)
    {
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT ");
        sql.append("GDAR.ENT_ID AS ENT_ID,");
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
        sql.append(String.format(" INNER JOIN \"%s\".%s GDAR ON RD.AGENT_ID=GDAR.AGENT_ID AND GDAR.DB_NAME='%s' AND GDAR.SCHEME_NAME='%s'",
                this.glsDBName, this.dbAgentRelateTable, this.buzDBName, schemaName));
        switch (this.recordType)
        {
            case MIX:
                sql.append(" LEFT JOIN \"").append(schemaName).append("\".")
                        .append(this.mixTable).append(" ERT");
                sql.append(" ON RD.SESSION_ID = ERT.SESSION_ID AND RD.AGENT_ID = ERT.AGENT_ID");
                break;
            case COMBINATION:
                sql.append(" LEFT JOIN \"").append(schemaName).append("\".")
                        .append(this.combinationTable).append(" ERBT");
                sql.append(" ON RD.SESSION_ID = ERBT.SESSION_ID AND RD.AGENT_ID = ERBT.AGENT_ID");
                break;
            case MIX_AND_COMBINATION:
                sql.append(" LEFT JOIN \"").append(schemaName).append("\".")
                        .append(this.mixTable).append(" ERT");
                sql.append(" ON RD.SESSION_ID = ERT.SESSION_ID AND RD.AGENT_ID = ERT.AGENT_ID");
                sql.append(" LEFT JOIN \"").append(schemaName).append("\".")
                        .append(this.combinationTable).append(" ERBT");
                sql.append(" ON RD.SESSION_ID = ERBT.SESSION_ID AND RD.AGENT_ID = ERBT.AGENT_ID");
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

    private class MapRow implements RowMapper<RecordDetailVo>
    {
        @Override
        public RecordDetailVo mapRow(ResultSet rs, int i) throws SQLException
        {
            RecordDetailVo detailVo = new RecordDetailVo();
            detailVo.setEnterpriseId(rs.getString("ENT_ID"));
            detailVo.setRecordType(recordType);
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
                    if(StringUtils.isNotBlank(mixIndex))
                    {
                        detailVo.setRecordType(RecordType.MIX);
                        detailVo.setRecordIndex(mixIndex);
                    }
                    else if(StringUtils.isNotBlank(combinationIndex))
                    {
                        detailVo.setRecordType(RecordType.COMBINATION);
                        detailVo.setRecordIndex(combinationIndex);
                    }
                    else
                    {
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
