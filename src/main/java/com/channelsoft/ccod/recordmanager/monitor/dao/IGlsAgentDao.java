package com.channelsoft.ccod.recordmanager.monitor.dao;

import com.channelsoft.ccod.recordmanager.monitor.vo.GlsAgentVo;

import java.util.List;

/**
 * @ClassName: IGlsAgentDao
 * @Author: lanhb
 * @Description: GlsAgent的dao接口类
 * @Date: 2020/4/8 19:01
 * @Version: 1.0
 */
public interface IGlsAgentDao {

    List<GlsAgentVo> select();
}
