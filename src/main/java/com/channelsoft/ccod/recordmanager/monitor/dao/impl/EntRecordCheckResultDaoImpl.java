package com.channelsoft.ccod.recordmanager.monitor.dao.impl;

import com.channelsoft.ccod.recordmanager.monitor.dao.IEntRecordCheckResultDao;
import com.channelsoft.ccod.recordmanager.monitor.po.EntRecordCheckResultPo;
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
 * @ClassName: EntRecordCheckResultDaoImpl
 * @Author: lanhb
 * @Description: IEntRecordCheckResultDao接口的sqlite实现类
 * @Date: 2020/4/12 14:49
 * @Version: 1.0
 */
@Component
public class EntRecordCheckResultDaoImpl implements IEntRecordCheckResultDao {

    private final static Logger logger = LoggerFactory.getLogger(PlatformRecordCheckResultDaoImpl.class);

    @Autowired
    JdbcTemplate sqliteJdbcTemplate;

    @Override
    public int insert(int platformRecordCheckId, EntRecordCheckResultPo checkResultVo) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String sql = String.format("insert into ent_record_check_result (id, platformCheckId, enterpriseId, enterpriseName, checkTime, beginTime, endTime, result, comment, hasBak, checkCount, successCount, notIndexCount, notFileCount, notBakIndexCount, notBakFileCount) values (NULL, %d, '%s', '%s', '%s', '%s', '%s', %d, '%s', %d, %d, %d, %d, %d, %d, %d)",
                platformRecordCheckId, checkResultVo.getEnterpriseId(), checkResultVo.getEnterpriseName(), sf.format(checkResultVo.getCheckTime()),
                sf.format(checkResultVo.getBeginTime()),
                sf.format(checkResultVo.getEndTime()), checkResultVo.isResult() ? 1 : 0, checkResultVo.getComment(),
                checkResultVo.isHasBak() ? 1 : 0, checkResultVo.getCheckCount(),
                checkResultVo.getSuccessCount(), checkResultVo.getNotIndexCount(), checkResultVo.getNotFileCount(),
                checkResultVo.getNotBakIndexCount(), checkResultVo.getNotBakIndexCount());
        PreparedStatementCreator preparedStatementCreator = con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            return ps;
        };
        logger.debug(String.format("insert new enterprise record check result sql=%s", sql));
        sqliteJdbcTemplate.update(preparedStatementCreator, keyHolder);
        logger.debug(String.format("insert new enterprise record check result success, id=%d", keyHolder.getKey().intValue()));
        return keyHolder.getKey().intValue();
    }

    @Override
    public List<EntRecordCheckResultPo> select(String enterpriseId, Date beginTime, Date endTime) {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String sql = String.format("select * from ent_record_check_result where enterpriseId=%s and beginTime>='%s' and endTime<='%s'",
                enterpriseId, sf.format(beginTime), sf.format(endTime));
        logger.debug(String.format("begin to query all enterprise record check result from %s to %s"
                , sf.format(beginTime), sf.format(endTime)));
        List<EntRecordCheckResultPo> list = sqliteJdbcTemplate.query(sql, new RowMap());
        logger.debug(String.format("find %d record of enterprise check result", list.size()));
        return list;
    }

    class RowMap implements RowMapper<EntRecordCheckResultPo>
    {
        @Override
        public EntRecordCheckResultPo mapRow(ResultSet rs, int i) throws SQLException
        {
            EntRecordCheckResultPo po = new EntRecordCheckResultPo();
            po.setId(rs.getInt("id"));
            po.setPlatformCheckId(rs.getInt("platformCheckId"));
            po.setEnterpriseId(rs.getString("enterpriseId"));
            po.setEnterpriseName(rs.getString("enterpriseName"));
            po.setCheckTime(rs.getTime("checkTime"));
            po.setBeginTime(rs.getTime("beginTime"));
            po.setEndTime(rs.getTime("endTime"));
            po.setResult(rs.getBoolean("result"));
            po.setComment(rs.getString("comment"));
            po.setHasBak(rs.getBoolean("hasBak"));
            po.setCheckCount(rs.getInt("checkCount"));
            po.setSuccessCount(rs.getInt("successCount"));
            po.setNotIndexCount(rs.getInt("notIndexCount"));
            po.setNotFileCount(rs.getInt("notFileCount"));
            po.setNotBakIndexCount(rs.getInt("notBakIndexCount"));
            po.setNotBakFileCount(rs.getInt("notBakFileCount"));
            return po;
        }
    }
}
