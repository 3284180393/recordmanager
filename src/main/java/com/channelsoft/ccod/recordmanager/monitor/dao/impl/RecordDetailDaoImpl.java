package com.channelsoft.ccod.recordmanager.monitor.dao.impl;

import com.channelsoft.ccod.recordmanager.config.CallCheckRule;
import com.channelsoft.ccod.recordmanager.config.DBConstructCfg;
import com.channelsoft.ccod.recordmanager.config.NormalPlatformCondition;
import com.channelsoft.ccod.recordmanager.constant.RecordType;
import com.channelsoft.ccod.recordmanager.monitor.dao.IRecordDetailDao;
import com.channelsoft.ccod.recordmanager.monitor.vo.RecordDetailVo;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Conditional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @ClassName: RecordDetailDaoImpl
 * @Author: lanhb
 * @Description: 普通平台的IRecordlDetailDao接口实现类，适用ucds库为mysql，业务库为单一oracle场景
 * @Date: 2020/4/4 13:15
 * @Version: 1.0
 */
@Component(value = "recordDetailDao")
public class RecordDetailDaoImpl implements IRecordDetailDao {

    private final static Logger logger = LoggerFactory.getLogger(RecordDetailDaoImpl.class);

    @Autowired
    JdbcTemplate businessJdbcTemplate;

    @Value("${ccod.recordType}")
    private RecordType recordType;

    @Value("${db.table.detail}")
    private String detailTable;

    @Value("${db.table.mix}")
    private String mixTable;

    @Value("${db.table.combination}")
    private String combinationTable;

    @Value("${db.table.bak}")
    private String bakTable;

    @Value("${ccod.hasBak}")
    private boolean hasBak;

    @Autowired
    CallCheckRule callCheckRule;

    @PostConstruct
    public void init()
    {
//        initTestParams();
//        try
//        {
//            String enterpriseId = "0000099999";
//            SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
//            Date beginTime = sf.parse("20190814092600");
//            Date endTime = sf.parse("20190814092800");
//            List<RecordDetailVo> list = this.select(enterpriseId, beginTime, endTime);
//            System.out.println(list.size());
//            for(RecordDetailVo detailVo : list)
//            {
//                if(StringUtils.isNotBlank(detailVo.getRecordIndex()))
//                {
//                    System.out.print(detailVo.getSessionId());
//                }
//            }
//        }
//        catch (Exception ex)
//        {
//            ex.printStackTrace();
//        }
        System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$");
    }

    @Override
    public List<RecordDetailVo> select(String schemaName, Date beginTime, Date endTime) {
        String sql = generateSql(schemaName, beginTime, endTime);
        logger.debug(String.format("begin to query %s record detail with type=%s from %s to %s, sql=%s",
                schemaName, this.recordType, beginTime, endTime, sql));
        List<RecordDetailVo> retList = this.businessJdbcTemplate.query(sql, new MapRow());
        logger.debug(String.format("%s has %d record from %s to %s with type=%s",
                schemaName, retList.size(), beginTime, endTime, this.recordType.name));
        return retList;
    }

    private String generateSql(String schemaName, Date beginTime, Date endTime)
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

    private void initTestParams()
    {
//        this.callTypes = new int[]{0, 1};
//        this.endTypes = new int[]{254, 255};
//        this.detailTable = "R_DETAIL";
//        this.mixTable = "ENT_RECORD_BX_TABLE_H_201806";
//        this.combinationTable = "ENT_RECORD_BX_TABLE_H_201806";
//        this.bakTable = "ENT_RECORD_BX_TABLE_H_201806";
//        this.minTalkDuration = 1;
//        this.recordType = RecordType.MIX_AND_COMBINATION;
//        this.hasBak = true;
    }

    @Test
    public void sqlTest()
    {
        initTestParams();
        try
        {
            String enterpriseId = "0000099999";
            SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
            Date beginTime = sf.parse("20190814");
            Date endTime = sf.parse("20190815");
            String sql = generateSql(enterpriseId, beginTime, endTime);
            System.out.println(sql);
            sql = generateSql(enterpriseId, beginTime, endTime);
            System.out.println(sql);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
