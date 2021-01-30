package com.channelsoft.ccod.recordmanager.monitor.dao.impl;

import com.channelsoft.ccod.recordmanager.config.UCDSCondtion;
import com.channelsoft.ccod.recordmanager.config.UcdsAgentCondition;
import com.channelsoft.ccod.recordmanager.monitor.dao.IUcdsAgentDao;
import com.channelsoft.ccod.recordmanager.monitor.vo.GlsAgentVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Conditional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * @ClassName: UcdsAgentDaoImpl
 * @Author: lanhb
 * @Description: 通过ucds库实现座席查询
 * @Date: 2021/1/27 20:14
 * @Version: 1.0
 */
@Conditional(UcdsAgentCondition.class)
@Component(value = "ucdsAgentDao")
public class UcdsAgentDaoImpl implements IUcdsAgentDao {

    private final static Logger logger = LoggerFactory.getLogger(UcdsAgentDaoImpl.class);

    @Value("${db.table.agent}")
    private String agentTable;

    @Autowired
    JdbcTemplate ucdsJdbcTemplate;

    @Override
    public List<GlsAgentVo> select() {
        String sql = String.format("select ua.agent_id, ua.enterprise_id from %s ua", agentTable);
        logger.debug(String.format("begin to query all agent from ucds, sql=%s", sql));
        List<GlsAgentVo> list = ucdsJdbcTemplate.query(sql, new GlsAgentRowMapper());
        logger.debug(String.format("find %d agent at ucds", list.size()));
        return list;
    }

    private class GlsAgentRowMapper implements RowMapper<GlsAgentVo>
    {
        @Override
        public GlsAgentVo mapRow(ResultSet rs, int i) throws SQLException
        {
            GlsAgentVo po = new GlsAgentVo();
            po.setEntId(rs.getString("enterprise_id"));
            po.setAgentId(rs.getString("agent_id"));
            return po;
        }
    }
}
