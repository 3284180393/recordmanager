package com.channelsoft.ccod.recordmanager.monitor.dao.impl;

import com.channelsoft.ccod.recordmanager.monitor.dao.IPlatformRecordBackupResultDao;
import com.channelsoft.ccod.recordmanager.monitor.po.PlatformRecordBackupResultPo;
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
 * @ClassName: PlatformRecordBackupResultDaoImpl
 * @Author: lanhb
 * @Description: sqlite的IPlatformRecordBackupResultDao接口实现类
 * @Date: 2020/4/12 13:45
 * @Version: 1.0
 */
@Component
public class PlatformRecordBackupResultDaoImpl implements IPlatformRecordBackupResultDao {

    private final static Logger logger = LoggerFactory.getLogger(PlatformRecordBackupResultDaoImpl.class);

    @Autowired
    JdbcTemplate sqliteJdbcTemplate;

    @Override
    public int insert(PlatformRecordBackupResultPo backupResultPo) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String sql = "insert into platform_record_backup_result (platformId, platformName, backupDate, startTime, endTime, result, comment, backupCount, failCount, notBackupCount) " +
                "values (?,?,?,?,?,?,?,?,?,?)";
        PreparedStatementCreator preparedStatementCreator = con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, backupResultPo.getPlatformId());
            ps.setString(2, backupResultPo.getPlatformName());
            ps.setString(3, sf.format(backupResultPo.getBackupDate()));
            ps.setString(4, sf.format(backupResultPo.getStartTime()));
            ps.setString(5, sf.format(backupResultPo.getEndTime()));
            ps.setInt(6, backupResultPo.isResult() ? 1 : 0);
            ps.setString(7, backupResultPo.getComment());
            ps.setInt(8, backupResultPo.getBackupCount());
            ps.setInt(9,  backupResultPo.getFailCount());
            ps.setInt(10, backupResultPo.getNotBackupCount());
            return ps;
        };
        logger.debug(String.format("insert platform record backup result sql=%s", sql));
        sqliteJdbcTemplate.update(preparedStatementCreator, keyHolder);
        logger.debug(String.format("insert platform record backup result success, id=%d", keyHolder.getKey().intValue()));
        return keyHolder.getKey().intValue();
    }

    @Override
    public List<PlatformRecordBackupResultPo> select(Date startDate, Date endDate) {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String sql = String.format("select * from platform_record_backup_result where backupDate>='%s' and backupDate<='%s'",
                sf.format(startDate), sf.format(endDate));
        logger.debug(String.format("begin to query all platform check result from %s to %s"
                , sf.format(startDate), sf.format(endDate)));
        List<PlatformRecordBackupResultPo> list = sqliteJdbcTemplate.query(sql, new RowMap());
        logger.debug(String.format("find %d record of platform backup record", list.size()));
        return list;
    }

    class RowMap implements RowMapper<PlatformRecordBackupResultPo>
    {
        @Override
        public PlatformRecordBackupResultPo mapRow(ResultSet rs, int i) throws SQLException
        {
            PlatformRecordBackupResultPo po = new PlatformRecordBackupResultPo();
            po.setResult(rs.getBoolean("result"));
            po.setPlatformName(rs.getString("platformName"));
            po.setPlatformId(rs.getString("platformId"));
            po.setComment(rs.getString("comment"));
            po.setBackupDate(rs.getTimestamp("backupDate"));
            po.setStartTime(rs.getTimestamp("startTime"));
            po.setEndTime(rs.getTimestamp("endTime"));
            po.setId(rs.getInt("id"));
            po.setBackupCount(rs.getInt("backupCount"));
            po.setFailCount(rs.getInt("failCount"));
            po.setNotBackupCount(rs.getInt("notBackupCount"));
            return po;
        }
    }
}
