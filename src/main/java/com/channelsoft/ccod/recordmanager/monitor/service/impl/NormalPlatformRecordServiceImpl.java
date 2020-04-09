package com.channelsoft.ccod.recordmanager.monitor.service.impl;

import com.channelsoft.ccod.recordmanager.backup.vo.PlatformRecordBackupResultVo;
import com.channelsoft.ccod.recordmanager.config.NormalPlatformCondition;
import com.channelsoft.ccod.recordmanager.monitor.dao.IEnterpriseDao;
import com.channelsoft.ccod.recordmanager.monitor.dao.IRecordDetailDao;
import com.channelsoft.ccod.recordmanager.monitor.service.IPlatformRecordService;
import com.channelsoft.ccod.recordmanager.monitor.vo.EntRecordCheckResultVo;
import com.channelsoft.ccod.recordmanager.monitor.vo.EnterpriseVo;
import com.channelsoft.ccod.recordmanager.monitor.vo.PlatformRecordCheckResultVo;
import com.channelsoft.ccod.recordmanager.monitor.vo.RecordDetailVo;
import com.channelsoft.ccod.recordmanager.notify.service.INotifyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @ClassName: NormalPlatformRecordServiceImpl
 * @Author: lanhb
 * @Description: 普通平台的IPlatformRecordService的接口实现类
 * @Date: 2020/4/8 21:06
 * @Version: 1.0
 */
@Conditional(NormalPlatformCondition.class)
@Service
public class NormalPlatformRecordServiceImpl extends PlatformRecordBaseService {

    private final static Logger logger = LoggerFactory.getLogger(NormalPlatformRecordServiceImpl.class);

    @Autowired
    IEnterpriseDao enterpriseDao;

    @Autowired
    IRecordDetailDao recordDetailDao;

    @Override
    public PlatformRecordCheckResultVo check(Date beginTime, Date endTime) {
        Date startCheckTime = new Date();
        List<EnterpriseVo> enterpriseList;
        try
        {
             enterpriseList = enterpriseDao.select();
        }
        catch (Exception ex)
        {
            logger.error(String.format("查询平台企业异常", ex));
            return PlatformRecordCheckResultVo.fail(this.platformId, this.platformName, ex.getMessage());
        }
        List<EntRecordCheckResultVo> entCheckResultList = new ArrayList<>();
        for(EnterpriseVo enterpriseVo : enterpriseList)
        {
            if(!isEnterpriseChosen(enterpriseVo.getEnterpriseId()))
            {
                logger.debug(String.format("%s(%s) is not been checked", enterpriseVo.getEnterpriseName(), enterpriseVo.getEnterpriseId()));
                continue;
            }
            EntRecordCheckResultVo entRecordCheckResultVo;
            try
            {
                Date checkTime = new Date();
                List<RecordDetailVo> entRecordList = recordDetailDao.select(enterpriseVo.getEnterpriseId(), beginTime, endTime);
                entRecordCheckResultVo = checkEntRecord(enterpriseVo, checkTime, beginTime, endTime, entRecordList);
            }
            catch (Exception ex)
            {
                logger.error(String.format("check %s(%s) record exception", enterpriseVo.getEnterpriseName(), enterpriseVo.getEnterpriseId()), ex);
                entRecordCheckResultVo = EntRecordCheckResultVo.fail(enterpriseVo, ex);
            }
            logger.debug(entRecordCheckResultVo.toString());
            entCheckResultList.add(entRecordCheckResultVo);
        }
        PlatformRecordCheckResultVo checkResultVo = new PlatformRecordCheckResultVo(this.platformId, this.platformName, startCheckTime, beginTime, endTime, entCheckResultList);
        logger.debug(checkResultVo.toString());
        return checkResultVo;
    }

}
