package com.channelsoft.ccod.recordmanager.monitor.dao.impl;

import com.channelsoft.ccod.recordmanager.monitor.dao.IGlsAgentDao;
import com.channelsoft.ccod.recordmanager.monitor.vo.GlsAgentVo;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @ClassName: GlsAgentDaoImpl
 * @Author: lanhb
 * @Description: IGlsAgentDao接口的实现类
 * @Date: 2020/4/8 19:02
 * @Version: 1.0
 */
@Component(value = "glsAgentDao")
public class GlsAgentDaoImpl implements IGlsAgentDao {

    @Override
    public List<GlsAgentVo> select() {
        return null;
    }
}
