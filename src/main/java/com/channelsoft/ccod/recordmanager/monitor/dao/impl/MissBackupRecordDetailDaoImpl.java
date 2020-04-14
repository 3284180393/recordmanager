package com.channelsoft.ccod.recordmanager.monitor.dao.impl;

import com.channelsoft.ccod.recordmanager.monitor.dao.IMissBackupRecordDetailDao;
import com.channelsoft.ccod.recordmanager.monitor.po.MissBackupRecordDetailPo;
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
 * @ClassName: MissBackupRecordDetailDaoImpl
 * @Author: lanhb
 * @Description: IMissBackupRecordDetailDao接口的sqlite实现类
 * @Date: 2020/4/12 20:57
 * @Version: 1.0
 */
@Component
public class MissBackupRecordDetailDaoImpl implements IMissBackupRecordDetailDao {

    private final static Logger logger = LoggerFactory.getLogger(CheckFailRecordDetailDaoImpl.class);

    @Autowired
    JdbcTemplate sqliteJdbcTemplate;

    @Override
    public int insert(int platformRecordBackupId, MissBackupRecordDetailPo detailPo) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String sql = "insert into miss_backup_record_detail (backupId, enterpriseId, enterpriseName, sessionId, agentId, startTime, endTime, talkDuration, callType, endType, recordIndex, bakRecordIndex, failReason) " +
                "values (?,?,?,?,?,?,?,?,?,?,?,?,?)";
        PreparedStatementCreator preparedStatementCreator = con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, platformRecordBackupId);
            ps.setString(2, detailPo.getEnterpriseId());
            ps.setString(3, detailPo.getEnterpriseName());
            ps.setString(4, detailPo.getSessionId());
            ps.setString(5, detailPo.getAgentId());
            ps.setString(6, sf.format(detailPo.getStartTime()));
            ps.setString(7, sf.format(detailPo.getEndTime()));
            ps.setInt(8, detailPo.getTalkDuration());
            ps.setInt(9, detailPo.getCallType());
            ps.setInt(10, detailPo.getEndType());
            ps.setString(11, detailPo.getRecordIndex());
            ps.setString(12, detailPo.getBakRecordIndex());
            ps.setString(13, detailPo.getFailReason());
            return ps;
        };
        logger.debug(String.format("insert miss backup record detail, sql=%s", sql));
        sqliteJdbcTemplate.update(preparedStatementCreator, keyHolder);
        logger.debug(String.format("insert miss backup record detail success, id=%d", keyHolder.getKey().intValue()));
        return keyHolder.getKey().intValue();
    }

    @Override
    public List<MissBackupRecordDetailPo> select(Date beginDate, Date endDate) {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String sql = String.format("select * from miss_backup_record_detail where backupDate>='%s' and backupDate<='%s'",
                sf.format(beginDate), sf.format(endDate));
        logger.debug(String.format("begin to query not backup check record detail from %s to %s",
                sf.format(beginDate), sf.format(endDate)));
        List<MissBackupRecordDetailPo> list = sqliteJdbcTemplate.query(sql, new RowMap());
        logger.debug(String.format("find %d not backup record detail", list.size()));
        return list;
    }

    class RowMap implements RowMapper<MissBackupRecordDetailPo>
    {
        @Override
        public MissBackupRecordDetailPo mapRow(ResultSet rs, int i) throws SQLException
        {
            MissBackupRecordDetailPo po = new MissBackupRecordDetailPo();
            po.setId(rs.getInt("id"));
            po.setBackupId(rs.getInt("backupId"));
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
            po.setFailReason(rs.getString("bakRecordIndex"));
            return po;
        }
    }
}
