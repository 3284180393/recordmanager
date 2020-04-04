package com.channelsoft.ccod.recordmanager.monitor.dao;

import com.channelsoft.ccod.recordmanager.monitor.vo.EnterpriseVo;

import java.util.List;

/**
 * @ClassName: IEnterpriseDao
 * @Author: lanhb
 * @Description: 定义Enterprise的dao接口
 * @Date: 2020/4/4 19:41
 * @Version: 1.0
 */
public interface IEnterpriseDao {

    /**
     * 查询平台所有的企业
     * @return 平台所有企业
     */
    List<EnterpriseVo> select();
}
