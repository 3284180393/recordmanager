package com.channelsoft.ccod.recordmanager.monitor.dao.impl;

import com.channelsoft.ccod.recordmanager.monitor.dao.IGlsAgentDao;
import com.channelsoft.ccod.recordmanager.monitor.vo.GlsAgentVo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @ClassName: UcdsAgentDaoImpl
 * @Author: lanhb
 * @Description: 通过ucds库实现座席查询
 * @Date: 2021/1/27 20:14
 * @Version: 1.0
 */
@Component(value = "ucdsAgentDao")
public class UcdsAgentDaoImpl implements IGlsAgentDao {

    @Value("${db.table.enterprise}")
    private String enterpriseTable;

    @Override
    public List<GlsAgentVo> select() {
        return null;
    }
}
