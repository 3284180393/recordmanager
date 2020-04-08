package com.channelsoft.ccod.recordmanager.monitor.dao;

import com.channelsoft.ccod.recordmanager.monitor.vo.DBSchemaVo;

import java.util.List;

/**
 * @ClassName: IDBSchemaDao
 * @Author: lanhb
 * @Description: 用来查询db和schema关系的dao接口
 * @Date: 2020/4/8 15:56
 * @Version: 1.0
 */
public interface IDBSchemaDao {

    /**
     * 查询数据库中db和schema的关系
     * @return 查询结果
     */
    List<DBSchemaVo> select();

}
