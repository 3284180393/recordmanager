package com.channelsoft.ccod.recordmanager.monitor.dao.impl;

import com.channelsoft.ccod.recordmanager.monitor.dao.ICheckFailRecordDetailDao;
import com.channelsoft.ccod.recordmanager.monitor.po.CheckFailRecordDetailPo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @ClassName: CheckFailRecordDetailDaoImpl
 * @Author: lanhb
 * @Description: sqlite的ICheckFailRecordDetailDao接口实现类
 * @Date: 2020/4/12 15:47
 * @Version: 1.0
 */
@Component
public class CheckFailRecordDetailDaoImpl implements ICheckFailRecordDetailDao {

    private final static Logger logger = LoggerFactory.getLogger(CheckFailRecordDetailDaoImpl.class);

    @Autowired
    JdbcTemplate sqliteJdbcTemplate;

    @Override
    public int insert(int platformRecordCheckId, int entRecordCheckId, CheckFailRecordDetailPo detailPo) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String sql = "insert into fail_check_record_detail (platformCheckId, entCheckId, enterpriseId, enterpriseName, sessionId, agentId, startTime, endTime, talkDuration, callType, endType, recordIndex, bakRecordIndex, failReason) " +
                "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        PreparedStatementCreator preparedStatementCreator = con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, platformRecordCheckId);
            ps.setInt(2, entRecordCheckId);
            ps.setString(3, detailPo.getEnterpriseId());
            ps.setString(4, detailPo.getEnterpriseName());
            ps.setString(5, detailPo.getSessionId());
            ps.setString(6, detailPo.getAgentId());
            ps.setString(7, sf.format(detailPo.getStartTime()));
            ps.setString(8, sf.format(detailPo.getEndTime()));
            ps.setInt(9, detailPo.getTalkDuration());
            ps.setInt(10, detailPo.getCallType());
            ps.setInt(11, detailPo.getEndType());
            ps.setString(12, detailPo.getRecordIndex());
            ps.setString(13, detailPo.getBakRecordIndex());
            ps.setString(14, detailPo.getFailReason());
            return ps;
        };
        logger.debug(String.format("insert new fail check record detail, sql=%s", sql));
        sqliteJdbcTemplate.update(preparedStatementCreator, keyHolder);
        logger.debug(String.format("insert new fail check record detail success, id=%d", keyHolder.getKey().intValue()));
        return keyHolder.getKey().intValue();
    }

    @Override
    public List<CheckFailRecordDetailPo> select(Integer platformCheckId, Integer entRecordCheckId) {
        String sql = String.format("select * from fail_check_record_detail where 1=1 ");
        if(platformCheckId != null)
            sql = String.format("%s and platformCheckId=%d", sql, platformCheckId);
        if(entRecordCheckId != null)
            sql = String.format("%s and entCheckId=%d", sql, entRecordCheckId);
        logger.debug(String.format("begin to query platformCheckId=%d and and entCheckId=%d fail check record detail, sql=%s",
                platformCheckId, entRecordCheckId, sql));
        List<CheckFailRecordDetailPo> list = sqliteJdbcTemplate.query(sql, new RowMap());
        logger.debug(String.format("find platformCheckId=%d and and entCheckId=%d %d fail check record detail",
                platformCheckId, entRecordCheckId, list.size()));
        return list;
    }

    @Override
    public List<CheckFailRecordDetailPo> select(String enterpriseId, Date beginTime, Date endTime) {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String sql = String.format("select * from fail_check_record_detail where enterpriseId='%s' and endTime>='%s' and endTime<='%s'",
                enterpriseId, sf.format(beginTime), sf.format(endTime));
        logger.debug(String.format("begin to query %s fail check record detail from %s to %s",
                enterpriseId, sf.format(beginTime), sf.format(endTime)));
        List<CheckFailRecordDetailPo> list = sqliteJdbcTemplate.query(sql, new RowMap());
        logger.debug(String.format("find %s %d fail check record detail", enterpriseId, list.size()));
        return list;
    }

    class RowMap implements RowMapper<CheckFailRecordDetailPo>
    {
        @Override
        public CheckFailRecordDetailPo mapRow(ResultSet rs, int i) throws SQLException
        {
            CheckFailRecordDetailPo po = new CheckFailRecordDetailPo();
            po.setId(rs.getInt("id"));
            po.setPlatformCheckId(rs.getInt("platformCheckId"));
            po.setEntCheckId(rs.getInt("entCheckId"));
            po.setEnterpriseId(rs.getString("enterpriseId"));
            po.setEnterpriseName(rs.getString("enterpriseName"));
            po.setSessionId(rs.getString("sessionId"));
            po.setAgentId(rs.getString("agentId"));
            po.setStartTime(rs.getTimestamp("startTime"));
            po.setEndTime(rs.getTimestamp("endTime"));
            po.setTalkDuration(rs.getInt("talkDuration"));
            po.setCallType(rs.getInt("callType"));
            po.setEndType(rs.getInt("endType"));
            po.setRecordIndex(rs.getString("recordIndex"));
            po.setBakRecordIndex(rs.getString("bakRecordIndex"));
            po.setFailReason(rs.getString("failReason"));
            return po;
        }
    }
}
