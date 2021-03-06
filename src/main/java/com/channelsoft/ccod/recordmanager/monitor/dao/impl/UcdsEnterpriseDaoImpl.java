package com.channelsoft.ccod.recordmanager.monitor.dao.impl;

import com.channelsoft.ccod.recordmanager.config.UCDSCondtion;
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

import javax.annotation.PostConstruct;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: UcdsEnterpriseDaoImpl
 * @Author: lanhb
 * @Description: ucds版IEnterpriseDao实现类
 * @Date: 2020/4/4 20:32
 * @Version: 1.0
 */
@Conditional(UCDSCondtion.class)
@Component(value = "enterpriseDao")
public class UcdsEnterpriseDaoImpl implements IEnterpriseDao {

    private final static Logger logger = LoggerFactory.getLogger(UcdsEnterpriseDaoImpl.class);

    @Autowired
    JdbcTemplate ucdsJdbcTemplate;

    @Value("${db.table.enterprise}")
    private String enterpriseTable;

    @Value("${db.table.agent}")
    private String enterpriseAgentTable;

    @Value("${debug}")
    private boolean debug;

    @PostConstruct
    public void init() throws Exception
    {
        System.out.println("3333333333333333333333333333333333333333333333333333333333333333333333");
    }

    @Override
    public List<EnterpriseVo> select() {
        String sql = String.format("select ueai.enterprise_id as enterprise_id, uei.enterprise_name as enterprise_name from (select enterprise_id from %s GROUP BY enterprise_id) ueai INNER JOIN %s uei ON ueai.enterprise_id=uei.enterprise_id",
                this.enterpriseAgentTable, this.enterpriseTable);
        logger.debug(String.format("begin to query all enterprise, sql=%s", sql));
        List<EnterpriseVo> list = this.ucdsJdbcTemplate.query(sql, new MapRow());
        if(debug)
        {
            list = new ArrayList<>();
            EnterpriseVo enterpriseVo = new EnterpriseVo();
            enterpriseVo.setEnterpriseId("20190423");
            enterpriseVo.setEnterpriseName("20190423");
            list.add(enterpriseVo);
        }
        logger.debug(String.format("find %d enterprise in platform", list.size()));
        return list;
    }

    class MapRow implements RowMapper<EnterpriseVo>
    {
        @Override
        public EnterpriseVo mapRow(ResultSet rs, int i) throws SQLException
        {
            EnterpriseVo vo = new EnterpriseVo();
            vo.setEnterpriseName(rs.getString("enterprise_name"));
            vo.setEnterpriseId(rs.getString("enterprise_id"));
            return vo;
        }
    }
}
