package com.channelsoft.ccod.recordmanager.backup.service;

import com.channelsoft.ccod.recordmanager.backup.vo.StoredRecordFileVo;

import java.util.Date;
import java.util.List;

/**
 * @ClassName: IRecordBackupService
 * @Author: lanhb
 * @Description: 用来定义和录音备份相关的接口类
 * @Date: 2020/4/1 11:41
 * @Version: 1.0
 */
public interface IRecordBackupService {

    /**
     * 扫描date这一天内保存在storeRootDirectory目录下的满足条件的录音文件信息
     * @param storeRootDirectory 被扫描的用来存储录音文件的根目录，通常是一个挂载目录
     * @param date 生成录音文件的日期
     * @return 指定目录下的该日所生成的满足条件的录音文件
     * @throws Exception
     */
    List<StoredRecordFileVo> scanStoreRootDirectory(String storeRootDirectory, Date date) throws Exception;
}
