package com.channelsoft.ccod.recordmanager.monitor.dao.impl;

import com.channelsoft.ccod.recordmanager.monitor.dao.INextBackupDateDao;
import com.channelsoft.ccod.recordmanager.monitor.po.NextBackupDatePo;
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
import java.util.List;

/**
 * @ClassName: NextBackupDateDaoImpl
 * @Author: lanhb
 * @Description: INextBackupDateDao接口的sqlite实现类
 * @Date: 2020/4/12 22:16
 * @Version: 1.0
 */
@Component
public class NextBackupDateDaoImpl implements INextBackupDateDao {

    private final static Logger logger = LoggerFactory.getLogger(NextBackupDateDaoImpl.class);

    @Autowired
    JdbcTemplate sqliteJdbcTemplate;

    @Override
    public int insert(NextBackupDatePo nextBackupDatePo) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String sql = String.format("insert into next_backup_date (id, nextBackupDate, updateTime) values (NULL, '%s', '%s')",
                sf.format(nextBackupDatePo.getNextBackupDate()), sf.format(nextBackupDatePo.getUpdateTime()));
        PreparedStatementCreator preparedStatementCreator = con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            return ps;
        };
        logger.debug(String.format("insert next backup date, sql=%s", sql));
        sqliteJdbcTemplate.update(preparedStatementCreator, keyHolder);
        logger.debug(String.format("insert new next backup date success, id=%d", keyHolder.getKey().intValue()));
        return keyHolder.getKey().intValue();
    }

    @Override
    public NextBackupDatePo selectLast() {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String sql = String.format("select * from next_backup_date where 1=1");
        logger.debug(String.format("begin to query next backup date"));
        List<NextBackupDatePo> list = sqliteJdbcTemplate.query(sql, new RowMap());
        logger.debug(String.format("find %d next backup date", list.size()));
        if(list.size() > 0)
            return list.get(list.size() - 1);
        return null;
    }

    @Override
    public void delete() {
        String sql = "delete from next_backup_date where 1=1";
        logger.debug(String.format("delete all next backup record, sql=%s", sql));
        sqliteJdbcTemplate.update(sql);
        logger.debug("delete success");
    }

    class RowMap implements RowMapper<NextBackupDatePo>
    {
        @Override
        public NextBackupDatePo mapRow(ResultSet rs, int i) throws SQLException
        {
            NextBackupDatePo po = new NextBackupDatePo();
            po.setId(rs.getInt("id"));
            po.setNextBackupDate(rs.getTime("nextBackupDate"));
            po.setUpdateTime(rs.getTime("updateTime"));
            return po;
        }
    }
}