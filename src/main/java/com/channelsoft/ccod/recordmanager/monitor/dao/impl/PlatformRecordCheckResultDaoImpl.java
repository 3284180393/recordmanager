package com.channelsoft.ccod.recordmanager.monitor.dao.impl;

import com.channelsoft.ccod.recordmanager.monitor.dao.IPlatformRecordCheckResultDao;
import com.channelsoft.ccod.recordmanager.monitor.po.PlatformRecordCheckResultPo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @ClassName: PlatformRecordCheckResultDaoImpl
 * @Author: lanhb
 * @Description: IPlatformRecordCheckResultDao接口的sqlite实现类
 * @Date: 2020/4/11 18:37
 * @Version: 1.0
 */
@Component
public class PlatformRecordCheckResultDaoImpl implements IPlatformRecordCheckResultDao {

    private final static Logger logger = LoggerFactory.getLogger(PlatformRecordCheckResultDaoImpl.class);

    @Autowired
    JdbcTemplate sqliteJdbcTemplate;

    @PostConstruct
    public void init()
    {
        try
        {
            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date beginTime = sf.parse("2020-03-01 00:00:00");
            Date endTime = sf.parse("2020-05-31 00:00:00");
            List<PlatformRecordCheckResultPo> list = select(beginTime, endTime);
            System.out.println(list.size());
            PlatformRecordCheckResultPo resultPo = new PlatformRecordCheckResultPo();
            Date now = new Date();
            resultPo.setBeginTime(now);
            resultPo.setCheckTime(now);
            resultPo.setComment("just a test");
            resultPo.setEndTime(now);
            resultPo.setPlatformId("shltPA");
            resultPo.setPlatformName("上海联通平安");
            resultPo.setResult(true);
            resultPo.setTimeUsage(1234);
            int id = insert(resultPo);
            System.out.println(id);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

    }

    @Override
    public int insert(PlatformRecordCheckResultPo checkResultVo) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String sql = "insert into platform_record_check_result (platformId, platformName, checkTime, timeUsage, beginTime, endTime, result, comment, checkEntCount, failEntCount, checkCount, successCount, notIndexCount, notFileCount, notBakIndexCount, notBakFileCount) " +
                "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        PreparedStatementCreator preparedStatementCreator = con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, checkResultVo.getPlatformId());
            ps.setString(2, checkResultVo.getPlatformName());
            ps.setString(3, sf.format(checkResultVo.getCheckTime()));
            ps.setInt(4, checkResultVo.getTimeUsage());
            ps.setString(5, sf.format(checkResultVo.getBeginTime()));
            ps.setString(6, sf.format(checkResultVo.getEndTime()));
            ps.setInt(7, checkResultVo.isResult() ? 1 : 0);
            ps.setString(8, checkResultVo.getComment());
            ps.setInt(9, checkResultVo.getCheckEntCount());
            ps.setInt(10, checkResultVo.getFailEntCount());
            ps.setInt(11, checkResultVo.getCheckCount());
            ps.setInt(12, checkResultVo.getSuccessCount());
            ps.setInt(13, checkResultVo.getNotIndexCount());
            ps.setInt(14, checkResultVo.getNotFileCount());
            ps.setInt(15, checkResultVo.getNotBakIndexCount());
            ps.setInt(16, checkResultVo.getNotFileCount());
            return ps;
        };
        logger.debug(String.format("insert new platform record checkResult sql=%s", sql));
        sqliteJdbcTemplate.update(preparedStatementCreator, keyHolder);
        logger.debug(String.format("insert new platform record checkResult success, id=%d", keyHolder.getKey().intValue()));
        return keyHolder.getKey().intValue();
    }

    @Override
    public List<PlatformRecordCheckResultPo> select(Date beginTime, Date endTime) {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String sql = String.format("select * from platform_record_check_result where checkTime>='%s' and checkTime<='%s'",
                sf.format(beginTime), sf.format(endTime));
        logger.debug(String.format("begin to query all platform check result from %s to %s"
                , sf.format(beginTime), sf.format(endTime)));
        List<PlatformRecordCheckResultPo> list = sqliteJdbcTemplate.query(sql, new RowMap());
        logger.debug(String.format("find %d record of platform check record", list.size()));
        return list;
    }

    class RowMap implements RowMapper<PlatformRecordCheckResultPo>
    {
        @Override
        public PlatformRecordCheckResultPo mapRow(ResultSet rs, int i) throws SQLException
        {
            PlatformRecordCheckResultPo po = new PlatformRecordCheckResultPo();
            po.setResult(rs.getBoolean("result"));
            po.setPlatformName(rs.getString("platformName"));
            po.setPlatformId(rs.getString("platformId"));
            po.setEndTime(rs.getTime("endTime"));
            po.setComment(rs.getString("comment"));
            po.setCheckTime(rs.getTime("checkTime"));
            po.setBeginTime(rs.getTime("beginTime"));
            po.setTimeUsage(rs.getInt("timeUsage"));
            po.setId(rs.getInt("id"));
            po.setCheckEntCount(rs.getInt("checkEntCount"));
            po.setFailEntCount(rs.getInt("failEntCount"));
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
