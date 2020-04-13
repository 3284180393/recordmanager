package com.channelsoft.ccod.recordmanager.monitor.dao.impl;

import com.channelsoft.ccod.recordmanager.monitor.dao.IFailBackupRecordFileDao;
import com.channelsoft.ccod.recordmanager.monitor.po.FailBackupRecordFilePo;
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
 * @ClassName: FailBackupRecordFileDaoImpl
 * @Author: lanhb
 * @Description: IFailBackupRecordFileDao接口的sqlite实现类
 * @Date: 2020/4/12 21:45
 * @Version: 1.0
 */
@Component
public class FailBackupRecordFileDaoImpl implements IFailBackupRecordFileDao {

    private final static Logger logger = LoggerFactory.getLogger(FailBackupRecordFileDaoImpl.class);

    @Autowired
    JdbcTemplate sqliteJdbcTemplate;

    @Override
    public int insert(int platformBackupId, FailBackupRecordFilePo recordFilePo) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String sql = "insert into fail_backup_record_file (platformBackupId, recordDate, fileSavePath, backupPath, failReason) " +
                "values (?,?,?,?,?)";
        PreparedStatementCreator preparedStatementCreator = con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, platformBackupId);
            ps.setString(2, sf.format(recordFilePo.getRecordDate()));
            ps.setString(3, recordFilePo.getFileSavePath());
            ps.setString(4, recordFilePo.getBackupPath());
            ps.setString(5, recordFilePo.getFailReason());
            return ps;
        };
        logger.debug(String.format("insert fail backup record file, sql=%s", sql));
        sqliteJdbcTemplate.update(preparedStatementCreator, keyHolder);
        logger.debug(String.format("insert fail backup record file success, id=%d", keyHolder.getKey().intValue()));
        return keyHolder.getKey().intValue();
    }

    @Override
    public List<FailBackupRecordFilePo> select(Date beginDate, Date endDate) {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String sql = String.format("select * from fail_backup_record_file where recordDate>='%s' and recordDate<='%s'",
                sf.format(beginDate), sf.format(endDate));
        logger.debug(String.format("begin to query fail backup record file from %s to %s",
                sf.format(beginDate), sf.format(endDate)));
        List<FailBackupRecordFilePo> list = sqliteJdbcTemplate.query(sql, new RowMap());
        logger.debug(String.format("find %d fail backup record file", list.size()));
        return list;
    }

    class RowMap implements RowMapper<FailBackupRecordFilePo>
    {
        @Override
        public FailBackupRecordFilePo mapRow(ResultSet rs, int i) throws SQLException
        {
            FailBackupRecordFilePo po = new FailBackupRecordFilePo();
            po.setId(rs.getInt("id"));
            po.setPlatformBackupId(rs.getInt("platformBackupId"));
            po.setRecordDate(rs.getTime("recordDate"));
            po.setFileSavePath(rs.getString("fileSavePath"));
            po.setFailReason(rs.getString("backupPath"));
            po.setFailReason(rs.getString("failReason"));
            return po;
        }
    }
}
