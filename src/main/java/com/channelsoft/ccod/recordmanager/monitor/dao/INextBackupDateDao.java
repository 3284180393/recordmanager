package com.channelsoft.ccod.recordmanager.monitor.dao;

import com.channelsoft.ccod.recordmanager.monitor.po.NextBackupDatePo;

/**
 * @ClassName: INextBackupDateDao
 * @Author: lanhb
 * @Description: NextBackupDatePo的dao接口
 * @Date: 2020/4/12 22:12
 * @Version: 1.0
 */
public interface INextBackupDateDao {

    int insert(NextBackupDatePo nextBackupDatePo);

    NextBackupDatePo selectLast();

    void delete();
}
