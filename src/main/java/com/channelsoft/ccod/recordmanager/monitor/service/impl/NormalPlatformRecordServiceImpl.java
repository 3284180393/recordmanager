package com.channelsoft.ccod.recordmanager.monitor.service.impl;

import com.channelsoft.ccod.recordmanager.config.NormalPlatformCondition;
import com.channelsoft.ccod.recordmanager.monitor.dao.IEnterpriseDao;
import com.channelsoft.ccod.recordmanager.monitor.dao.IRecordDetailDao;
import com.channelsoft.ccod.recordmanager.monitor.vo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;
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

    @PostConstruct
    public void init()
    {
        PlatformRecordBackupResultSumVo resultVo;
        Date now = new Date();
        try
        {
            String dateStr = "20200401";
            SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
            Date chosenDate = sf.parse(dateStr);
            now = chosenDate;
            resultVo = backup(chosenDate);
            generateTestDate(resultVo);
            addPlatformRecordBackupResult(resultVo);
        }
        catch (Exception ex)
        {
            resultVo = PlatformRecordBackupResultSumVo.fail(this.platformId, this.platformName, now, ex);
            ex.printStackTrace();
        }
        notifyService.notify(resultVo);
        System.out.println("1111111111111111111111111111111111111111111111111111111111111111111111");
    }

    protected PlatformRecordCheckResultSumVo checkPlatformRecord(Date beginTime, Date endTime) throws Exception
    {
        Date startCheckTime = new Date();
        List<EnterpriseVo> enterpriseList = enterpriseDao.select();
        List<EntRecordCheckResultSumVo> entCheckResultList = new ArrayList<>();
        for(EnterpriseVo enterpriseVo : enterpriseList)
        {
            if(!isEnterpriseChosen(enterpriseVo.getEnterpriseId()))
            {
                logger.debug(String.format("%s(%s) is not been checked", enterpriseVo.getEnterpriseName(), enterpriseVo.getEnterpriseId()));
                continue;
            }
            EntRecordCheckResultSumVo entRecordCheckResultVo;
            try
            {
                Date checkTime = new Date();
                List<RecordDetailVo> entRecordList = recordDetailDao.select(enterpriseVo.getEnterpriseId(), beginTime, endTime);
                entRecordCheckResultVo = checkEntRecord(enterpriseVo, checkTime, beginTime, endTime, entRecordList);
            }
            catch (Exception ex)
            {
                logger.error(String.format("check %s(%s) record exception", enterpriseVo.getEnterpriseName(), enterpriseVo.getEnterpriseId()), ex);
                entRecordCheckResultVo = EntRecordCheckResultSumVo.fail(enterpriseVo, beginTime, endTime, ex);
            }
            logger.debug(entRecordCheckResultVo.toString());
            entCheckResultList.add(entRecordCheckResultVo);
        }
        PlatformRecordCheckResultSumVo checkResultVo = new PlatformRecordCheckResultSumVo(this.platformId, this.platformName, startCheckTime, beginTime, endTime, entCheckResultList);
        logger.debug(checkResultVo.toString());
        return checkResultVo;
    }

}
