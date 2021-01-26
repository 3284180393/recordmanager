package com.channelsoft.ccod.recordmanager.monitor.dao.impl;

import com.channelsoft.ccod.recordmanager.config.GLSCondition;
import com.channelsoft.ccod.recordmanager.monitor.dao.IGlsAgentDao;
import com.channelsoft.ccod.recordmanager.monitor.vo.GlsAgentVo;
import org.apache.commons.lang3.StringUtils;
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
import java.util.stream.Collectors;

/**
 * @ClassName: GlsAgentDaoImpl
 * @Author: lanhb
 * @Description: IGlsAgentDao接口的实现类
 * @Date: 2020/4/8 19:02
 * @Version: 1.0
 */
@Conditional(GLSCondition.class)
@Component(value = "glsAgentDao")
public class GlsAgentDaoImpl implements IGlsAgentDao {

    private final static Logger logger = LoggerFactory.getLogger(GlsAgentDaoImpl.class);

    @Autowired
    JdbcTemplate glsJdbcTemplate;

    @Value("${db.table.dbAgentRelate}")
    private String dbAgentRelateTable;

    @Value("${db.table.enterprise}")
    private String enterpriseTable;

    @Override
    public List<GlsAgentVo> select() {
        String sql = String.format("select GDAR.AGENT_ID, GDAR.DB_NAME, GDAR.SCHEME_NAME, GEI.ENTERPRISEID, GEI.ENTERPRISENAME from %s GDAR INNER JOIN %s GEI ON GDAR.ENT_ID=GEI.ENTERPRISEID AND GEI.ISOPEN=1",
                this.dbAgentRelateTable, this.enterpriseTable);
        logger.debug(String.format("begin to query all gls agent, sql=%s", sql));
        List<GlsAgentVo> list = glsJdbcTemplate.query(sql, new GlsAgentRowMapper());
        logger.debug(String.format("find %d gls agent", list.size()));
        list = list.stream().filter(a-> StringUtils.isNotBlank(a.getDbName()) && StringUtils.isNotBlank(a.getSchemaName()) && StringUtils.isNotBlank(a.getEntId()) && StringUtils.isNotBlank(a.getAgentId()))
                .collect(Collectors.toList());
        logger.debug(String.format("%d gls agent is valid", list.size()));
        return list;
    }

    private class GlsAgentRowMapper implements RowMapper<GlsAgentVo>
    {
        @Override
        public GlsAgentVo mapRow(ResultSet rs, int i) throws SQLException
        {
            GlsAgentVo po = new GlsAgentVo();
            po.setEntId(rs.getString("ENTERPRISEID"));
            po.setEntName(rs.getString("ENTERPRISENAME"));
            po.setAgentId(rs.getString("AGENT_ID"));
            po.setSchemaName(rs.getString("SCHEME_NAME"));
            po.setDbName(rs.getString("DB_NAME"));
            return po;
        }
    }
}
