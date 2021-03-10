package com.channelsoft.ccod.recordmanager.monitor.dao.impl;

import com.channelsoft.ccod.recordmanager.config.Buz2OracleCondition;
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
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName: RecordDetailDao2Impl
 * @Author: lanhb
 * @Description: 对于2个业务库的大域平台这是业务库2的IRecordDetailDao接口实现类
 * @Date: 2020/4/9 10:53
 * @Version: 1.0
 */
@Conditional(Buz2OracleCondition.class)
@Component(value = "recordDetail2Dao")
public class RecordDetailDao2Impl extends RecordDetailJdbcBase implements IRecordDetailDao {

   protected Logger logger = LoggerFactory.getLogger(RecordDetailDao2Impl.class);

    @Autowired
    JdbcTemplate business2JdbcTemplate;

    @Override
    public List<RecordDetailVo> select(String schemaName, Date beginTime, Date endTime) {
        return this.queryRecordDetailByJdbc(business2JdbcTemplate, schemaName, beginTime, endTime);
    }

    @Override
    public List<BakRecordIndex> select(String entId, List<String> sessionIds) {
        return this.queryBakRecordDetailByJdbc(business2JdbcTemplate, entId, sessionIds);
    }
}
