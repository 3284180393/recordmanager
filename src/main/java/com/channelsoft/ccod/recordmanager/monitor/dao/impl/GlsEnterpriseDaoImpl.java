package com.channelsoft.ccod.recordmanager.monitor.dao.impl;

import com.channelsoft.ccod.recordmanager.config.BigEntPlatformCondition;
import com.channelsoft.ccod.recordmanager.config.EnterpriseCfg;
import com.channelsoft.ccod.recordmanager.config.GLSCondition;
import com.channelsoft.ccod.recordmanager.config.NormalPlatformCondition;
import com.channelsoft.ccod.recordmanager.constant.EnterpriseChoseMethod;
import com.channelsoft.ccod.recordmanager.monitor.dao.IEnterpriseDao;
import com.channelsoft.ccod.recordmanager.monitor.vo.EnterpriseVo;
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
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @ClassName: GlsEnterpriseDaoImpl
 * @Author: lanhb
 * @Description: 用来定义gls库的企业dao实现类
 * @Date: 2020/4/4 19:50
 * @Version: 1.0
 */
@Conditional(GLSCondition.class)
@Component(value = "enterpriseDao")
public class GlsEnterpriseDaoImpl implements IEnterpriseDao {

    private final static Logger logger = LoggerFactory.getLogger(GlsEnterpriseDaoImpl.class);

    @Autowired
    JdbcTemplate glsJdbcTemplate;

    @Autowired
    EnterpriseCfg enterpriseCfg;

    @Value("${db.table.enterprise}")
    private String enterpriseTable;

    @Value("${db.table.dbEntRelate}")
    private String dbEntRelate;

    @Override
    public List<EnterpriseVo> select() {
        String sql = String.format("SELECT GEI.ENTERPRISEID AS ENT_ID,GEI.ENTERPRISENAME AS ENT_NAME FROM %s GEI WHERE GEI.ISOPEN=1",
                this.enterpriseTable);
        logger.debug(String.format("begin to query all enterprise, sql=%s", sql));
        List<EnterpriseVo> list = this.glsJdbcTemplate.query(sql, new MapRow());
        logger.debug(String.format("find %d enterprise in platform", list.size()));
        return list;
    }

    private class MapRow implements RowMapper<EnterpriseVo>
    {
        @Override
        public EnterpriseVo mapRow(ResultSet rs, int i) throws SQLException
        {
            EnterpriseVo vo = new EnterpriseVo();
            vo.setEnterpriseName(rs.getString("ENT_NAME"));
            vo.setEnterpriseId(rs.getString("ENT_ID"));
            return vo;
        }
    }
}
