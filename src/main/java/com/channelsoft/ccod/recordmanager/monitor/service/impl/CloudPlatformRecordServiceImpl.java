package com.channelsoft.ccod.recordmanager.monitor.service.impl;

import com.channelsoft.ccod.recordmanager.config.CloudPlatformCondition;
import com.channelsoft.ccod.recordmanager.monitor.dao.IEnterpriseDao;
import com.channelsoft.ccod.recordmanager.monitor.dao.IRecordDetailDao;
import com.channelsoft.ccod.recordmanager.monitor.service.IPlatformRecordService;
import com.channelsoft.ccod.recordmanager.monitor.vo.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @ClassName: CloudPlatformRecordServiceImpl
 * @Author: lanhb
 * @Description: 云平台IPlatformRecordService接口实现类
 * @Date: 2020/4/8 21:15
 * @Version: 1.0
 */
@Conditional(CloudPlatformCondition.class)
@Service
public class CloudPlatformRecordServiceImpl extends PlatformRecordBaseService {

    private final static Logger logger = LoggerFactory.getLogger(CloudPlatformRecordServiceImpl.class);

    @Autowired
    IEnterpriseDao enterpriseDao;

    public void init() throws Exception
    {
        System.out.println("^^^^^^^^^^^^^^^^^^^66");
    }

    @Override
    protected EntRecordCheckResultSumVo checkEntRecord(EnterpriseVo enterpriseVo, Date checkTime, Date beginTime, Date endTime, List<RecordDetailVo> entRecordList) {
        List<RecordDetailVo> successList = new ArrayList<>();
        List<RecordDetailVo> notIndexList = new ArrayList<>();
        List<RecordDetailVo> notFileList = new ArrayList<>();
        for(RecordDetailVo detailVo : entRecordList)
        {
            if(!StringUtils.isBlank(detailVo.getRecordIndex()) && !StringUtils.isBlank(detailVo.getRecordFileFastDfsUrl()))
            {
                successList.add(detailVo);
            }
            else if(StringUtils.isBlank(detailVo.getRecordIndex()))
            {
                notIndexList.add(detailVo);
            }
            else
                notFileList.add(detailVo);
            EntRecordCheckResultSumVo sumVo = new EntRecordCheckResultSumVo(enterpriseVo, checkTime, beginTime, endTime, successList, notIndexList, notFileList);
            return sumVo;
        }
        return super.checkEntRecord(enterpriseVo, checkTime, beginTime, endTime, entRecordList);
    }
}
