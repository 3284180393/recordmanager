package com.channelsoft.ccod.recordmanager.monitor.dao;

import com.channelsoft.ccod.recordmanager.monitor.vo.GlsEntVo;

import java.util.List;

/**
 * @ClassName: IGlsEntDao
 * @Author: lanhb
 * @Description: 定义GlsEnt的dao接口类
 * @Date: 2020/4/4 19:28
 * @Version: 1.0
 */
public interface IGlsEntDao {

    /**
     * 查询所有的企业
     * @return 查询到的所有企业
     */
    List<GlsEntVo> select();
}
