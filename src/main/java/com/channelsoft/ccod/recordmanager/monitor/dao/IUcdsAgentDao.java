package com.channelsoft.ccod.recordmanager.monitor.dao;

import com.channelsoft.ccod.recordmanager.monitor.vo.GlsAgentVo;

import java.util.List;

/**
 * @ClassName: IUcdsAgentDao
 * @Author: lanhb
 * @Description: 用来实现ucds座席查询dao接口
 * @Date: 2021/1/29 17:48
 * @Version: 1.0
 */
public interface IUcdsAgentDao {
    List<GlsAgentVo> select();
}
