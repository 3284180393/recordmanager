package com.channelsoft.ccod.recordmanager.monitor.dao.impl;

import com.channelsoft.ccod.recordmanager.config.GLSCondition;
import com.channelsoft.ccod.recordmanager.monitor.dao.IDBSchemaDao;
import com.channelsoft.ccod.recordmanager.monitor.vo.DBSchemaVo;
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

    @Value("${db.table.dbEntRelate}")
    private String dbEntRelate;

    @Override
    public List<DBSchemaVo> select()
    {
        // TODO Auto-generated method stub
        String sql = String.format("SELECT SCHEME_NAME AS SCHEMA_NAME, DB_NAME AS DB_NAME from %s GROUP BY SCHEME_NAME, DB_NAME", this.dbEntRelate);
        logger.debug("begin to query db and schema relation, sql : " + sql);
        List<DBSchemaVo> list = glsJdbcTemplate.query(sql, new DBSchemaRowMapper());
        logger.debug(String.format("find %d schema and db relation", list.size()));
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
            return po;
        }
    }
}
