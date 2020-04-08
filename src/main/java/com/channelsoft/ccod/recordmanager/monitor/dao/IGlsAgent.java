package com.channelsoft.ccod.recordmanager.monitor.dao;

import com.channelsoft.ccod.recordmanager.monitor.vo.GlsAgentVo;

import java.util.List;

/**
 * @ClassName: IGlsAgent
 * @Author: lanhb
 * @Description: GlsAgent的dao接口类
 * @Date: 2020/4/8 11:33
 * @Version: 1.0
 */
public interface IGlsAgent {

    /**
     * 查询平台下所有座席
     * @return
     */
    List<GlsAgentVo> select();
}
