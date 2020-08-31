package com.channelsoft.ccod.recordmanager.monitor.dao.impl;

import com.channelsoft.ccod.recordmanager.monitor.dao.BakRecordIndexDao;
import com.channelsoft.ccod.recordmanager.monitor.po.BakRecordIndex;
import com.channelsoft.ccod.recordmanager.monitor.po.EntRecordCheckResultPo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
 * @ClassName: BakRecordIndexDaoImpl
 * @Author: lanhb
 * @Description: BakRecordIndexDao接口的实现类
 * @Date: 2020/8/24 20:09
 * @Version: 1.0
 */
@Component(value = "bakRecordIndexDao")
public class BakRecordIndexDaoImpl implements BakRecordIndexDao {

    private final static Logger logger = LoggerFactory.getLogger(BakRecordIndexDaoImpl.class);

    @Autowired
    JdbcTemplate sqliteJdbcTemplate;

    @Value("${db.table.bak}")
    protected String bakRecordIndexTable;

    @Override
    public List<BakRecordIndex> select(Date beginTime, Date endTime) {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String sql = String.format("select * from bak_record_index where START_TIME>='%s' and END_TIME<'%s' order by ENT_ID",
                sf.format(beginTime), sf.format(endTime));
        logger.debug(String.format("begin to query all bak record index from %s to %s"
                , sf.format(beginTime), sf.format(endTime)));
        List<BakRecordIndex> list = sqliteJdbcTemplate.query(sql, new RowMap());
        logger.debug(String.format("find %d bak record index record from %s to %s",
                list.size(), sf.format(beginTime), sf.format(endTime)));
        return list;
    }

    @Override
    public void insert(BakRecordIndex bakRecordIndex) {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String sql = "insert into bak_record_index (RECORD_NAME, ENT_ID, SESSION_ID, REMOTE_URI, LOCAL_URI, AGENT_ID," +
                " CMS_NAME, CALL_TYPE, DEVICE_NUMBER, START_TIME, END_TIME, CTI_START_TIME, CTI_END_TIME, SKILL_NAME, notBakFileCount) values " +
                "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatementCreator preparedStatementCreator = con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, bakRecordIndex.getRecordName());
            ps.setString(2, bakRecordIndex.getEntId());
            ps.setString(3, bakRecordIndex.getSessionId());
            ps.setString(4, bakRecordIndex.getRemoteUri());
            ps.setString(5, bakRecordIndex.getLocalUri());
            ps.setString(6, bakRecordIndex.getAgentId());
            ps.setString(7, bakRecordIndex.getCmsName());
            ps.setInt(8, bakRecordIndex.getCallType());
            ps.setString(9, bakRecordIndex.getDeviceNumber());
            ps.setString(10, sf.format(bakRecordIndex.getStartTime()));
            ps.setString(11, sf.format(bakRecordIndex.getEndTime()));
            ps.setString(12, sf.format(bakRecordIndex.getCtiStartTime()));
            ps.setString(13, sf.format(bakRecordIndex.getCtiEndTime()));
            ps.setString(14, bakRecordIndex.getSkillName());
            return ps;
        };
        logger.debug(String.format("insert bak_record_index sql=%s", sql));
        sqliteJdbcTemplate.update(preparedStatementCreator);
        logger.debug(String.format("insert bak_record_index success"));
    }

    class RowMap implements RowMapper<BakRecordIndex>
    {
        @Override
        public BakRecordIndex mapRow(ResultSet rs, int i) throws SQLException
        {
            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            BakRecordIndex po = new BakRecordIndex();
            po.setRecordName(rs.getString("RECORD_NAME"));
            po.setEntId(rs.getString("ENT_ID"));
            po.setSessionId(rs.getString("SESSION_ID"));
            po.setRemoteUri(rs.getString("REMOTE_URI"));
            po.setLocalUri(rs.getString("LOCAL_URI"));
            po.setAgentId(rs.getString("AGENT_ID"));
            po.setCmsName(rs.getString("CMS_NAME"));
            po.setCallType(rs.getInt("CALL_TYPE"));
            po.setDeviceNumber(rs.getString("DEVICE_NUMBER"));
            try{
                po.setStartTime(sf.parse(rs.getString("START_TIME")));
                po.setEndTime(sf.parse(rs.getString("END_TIME")));
            }
            catch (Exception ex)
            {
                logger.error("parse start or end time error", ex);
            }
            try{
                po.setCtiStartTime(sf.parse(rs.getString("CTI_START_TIME")));
                po.setCtiEndTime(sf.parse(rs.getString("CTI_END_TIME")));
            }
            catch (Exception ex)
            {
                logger.error("parse cti start or end time error", ex);
            }
            po.setSkillName(rs.getString("SKILL_NAME"));
            return po;
        }
    }
}
