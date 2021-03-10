package com.channelsoft.ccod.recordmanager.monitor.dao.impl;

import com.channelsoft.ccod.recordmanager.config.BuzOracleCondition;
import com.channelsoft.ccod.recordmanager.config.CallCheckRule;
import com.channelsoft.ccod.recordmanager.constant.RecordType;
import com.channelsoft.ccod.recordmanager.monitor.dao.IRecordDetailDao;
import com.channelsoft.ccod.recordmanager.monitor.po.BakRecordIndex;
import com.channelsoft.ccod.recordmanager.monitor.vo.RecordDetailVo;
import org.apache.commons.lang3.StringUtils;
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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName: RecordDetailDaoImpl
 * @Author: lanhb
 * @Description: 普通平台的IRecordlDetailDao接口实现类，适用ucds库为mysql，业务库为单一oracle场景
 * @Date: 2020/4/4 13:15
 * @Version: 1.0
 */
@Conditional(BuzOracleCondition.class)
@Component(value = "recordDetailDao")
public class RecordDetailDaoImpl extends RecordDetailJdbcBase implements IRecordDetailDao {

    protected Logger logger = LoggerFactory.getLogger(RecordDetailDaoImpl.class);

    @Autowired
    JdbcTemplate businessJdbcTemplate;

    @Override
    public List<RecordDetailVo> select(String schemaName, Date beginTime, Date endTime) {
        return this.queryRecordDetailByJdbc(businessJdbcTemplate, schemaName, beginTime, endTime);
    }

    @Override
    public List<BakRecordIndex> select(String entId, List<String> sessionIds) {
        return this.queryBakRecordDetailByJdbc(businessJdbcTemplate, entId, sessionIds);
    }
}
