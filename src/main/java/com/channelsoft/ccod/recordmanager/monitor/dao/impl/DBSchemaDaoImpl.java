package com.channelsoft.ccod.recordmanager.monitor.dao.impl;

import com.channelsoft.ccod.recordmanager.config.GLSCondition;
import com.channelsoft.ccod.recordmanager.monitor.dao.IDBSchemaDao;
import com.channelsoft.ccod.recordmanager.monitor.vo.DBSchemaVo;
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
 * @ClassName: DBSchemaDaoImpl
 * @Author: lanhb
 * @Description: IDBSchemaDao的实现类
 * @Date: 2020/4/8 16:01
 * @Version: 1.0
 */
@Conditional(GLSCondition.class)
@Component(value = "dbSchemaDao")
public class DBSchemaDaoImpl implements IDBSchemaDao {

    private final static Logger logger = LoggerFactory.getLogger(RecordDetailDaoImpl.class);

    @Autowired
    JdbcTemplate glsJdbcTemplate;

    @Value("${db.table.dbAgentRelate}")
    private String dbAgentRelate;

    @Override
    public List<DBSchemaVo> select()
    {
        // TODO Auto-generated method stub
        String sql = String.format("SELECT SCHEME_NAME AS SCHEMA_NAME, DB_NAME AS DB_NAME, ENT_ID AS ENT_ID from %s GROUP BY SCHEME_NAME, DB_NAME, ENT_ID", this.dbAgentRelate);
        logger.debug("begin to query ent, db and schema relation, sql : " + sql);
        List<DBSchemaVo> list = glsJdbcTemplate.query(sql, new DBSchemaRowMapper());
        logger.debug(String.format("find %d schema and db relation", list.size()));
        list = list.stream().filter(d-> StringUtils.isNotBlank(d.getDbName()) && StringUtils.isNotBlank(d.getEntId()) && StringUtils.isNotBlank(d.getSchemaName()))
                .collect(Collectors.toList());
        logger.info(String.format("%d schema and db relation is valid", list.size()));
        return list;
    }

    class DBSchemaRowMapper implements RowMapper<DBSchemaVo>
    {
        @Override
        public DBSchemaVo mapRow(ResultSet rs, int i) throws SQLException
        {
            DBSchemaVo po = new DBSchemaVo();
            po.setDbName(rs.getString("DB_NAME"));
            po.setSchemaName(rs.getString("SCHEMA_NAME"));
            po.setEntId(rs.getString("ENT_ID"));
            return po;
        }
    }
}
