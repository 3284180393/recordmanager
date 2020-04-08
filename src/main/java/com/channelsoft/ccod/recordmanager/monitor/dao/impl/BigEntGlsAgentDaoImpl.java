package com.channelsoft.ccod.recordmanager.monitor.dao.impl;

import com.channelsoft.ccod.recordmanager.monitor.dao.IGlsAgentDao;
import com.channelsoft.ccod.recordmanager.monitor.vo.GlsAgentVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * @ClassName: BigEntGlsAgentDaoImpl
 * @Author: lanhb
 * @Description: 大域平台的IGlsAgentDao接口的实现类
 * @Date: 2020/4/8 19:08
 * @Version: 1.0
 */
public class BigEntGlsAgentDaoImpl implements IGlsAgentDao {

    private final static Logger logger = LoggerFactory.getLogger(BigEntGlsAgentDaoImpl.class);

    @Autowired
    JdbcTemplate glsJdbcTemplate;

    @Value("${db.table.dbAgentRelate}")
    private String dbAgentRelateTable;

    @Value("${db.table.enterprise}")
    private String enterpriseTable;

    @Override
    public List<GlsAgentVo> select() {
        String sql = String.format("select * from %s GDAR INNER JOIN %s GEI ON GDAR.ENT_ID=GEI.ENTERPRISEID AND GEI.ISOPEN=1",
                this.dbAgentRelateTable, this.enterpriseTable);
        logger.debug(String.format("begin to query all gls agent, sql=%s", sql));
        List<GlsAgentVo> list = glsJdbcTemplate.query(sql, new GlsAgentRowMapper());
        logger.debug(String.format("find %d gls agent", list.size()));
        return list;
    }

    private class GlsAgentRowMapper implements RowMapper<GlsAgentVo>
    {
        @Override
        public GlsAgentVo mapRow(ResultSet rs, int i) throws SQLException
        {
            GlsAgentVo po = new GlsAgentVo();
            po.setEntId(rs.getString("ENT_ID"));
            po.setAgentId(rs.getString("AGENT_ID"));
            po.setSchemaName(rs.getString("SCHEME_NAME"));
            po.setDbName(rs.getString("DB_NAME"));
            return po;
        }
    }

}
