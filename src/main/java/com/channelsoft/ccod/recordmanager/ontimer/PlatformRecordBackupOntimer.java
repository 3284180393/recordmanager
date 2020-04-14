package com.channelsoft.ccod.recordmanager.ontimer;

import com.channelsoft.ccod.recordmanager.monitor.dao.INextBackupDateDao;
import com.channelsoft.ccod.recordmanager.monitor.po.NextBackupDatePo;
import com.channelsoft.ccod.recordmanager.monitor.service.IPlatformRecordService;
import com.channelsoft.ccod.recordmanager.monitor.vo.PlatformRecordBackupResultSumVo;
import com.channelsoft.ccod.recordmanager.notify.service.INotifyService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @ClassName: PlatformRecordBackupOntimer
 * @Author: lanhb
 * @Description: 用来定义录音备份任务的定时器
 * @Date: 2020/4/14 15:30
 * @Version: 1.0
 */
@Component
@EnableScheduling
public class PlatformRecordBackupOntimer {

    public final static Logger logger = LoggerFactory.getLogger(PlatformRecordCheckOntimer.class);

    @Autowired
    IPlatformRecordService platformRecordService;

    @Autowired
    INotifyService notifyService;

    @Autowired
    INextBackupDateDao nextBackupDateDao;

    @Value("${ccod.platformId}")
    protected String platformId;

    @Value("${ccod.platformName}")
    protected String platformName;

    @Value("${jobs.backup.startDate}")
    private String startDate;

    @Value("${jobs.backup.stopTime}")
    private String stopBackupTimeStr;

    @Value("${jobs.backup.businessEndTime}")
    private String businessEndTimeStr;

    @Scheduled(cron = "${jobs.backup.cron}")
    private void start() throws Exception{
        Date now = new Date();
        logger.debug("platform record backup task start");
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = String.format("%s %s:00", sf.format(now), this.stopBackupTimeStr);
        SimpleDateFormat sf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date stopTime = sf1.parse(dateStr);
        Calendar ca = Calendar.getInstance();
        if(stopTime.getTime() < now.getTime())
        {
            ca.setTime(stopTime);
            ca.add(Calendar.DATE, 1);
            stopTime = ca.getTime();
        }
        Date backupDate;
        NextBackupDatePo nextBackupDatePo = nextBackupDateDao.selectLast();
        if(nextBackupDatePo != null)
        {
            backupDate = nextBackupDatePo.getNextBackupDate();
        }
        else
        {
            backupDate = sf.parse(this.startDate);
        }
        while(true)
        {
            now = new Date();
            if(now.getTime() > stopTime.getTime())
            {
                logger.debug(String.format("now is %s beyond stop time %s, so stop backup", sf1.format(now), sf1.format(stopTime)));
                break;
            }
            long nowDay = sf.parse(sf.format(now)).getTime();
            long backupDay = sf.parse(sf.format(backupDate)).getTime();
            if(backupDay > nowDay)
            {
                logger.debug(String.format("next backup date is %s, now is %s, so stop backup",sf.format(backupDate), sf.format(now)));
                break;
            }
            else if(backupDay == nowDay)
            {
                dateStr = String.format("%s %s:00", sf.format(now), this.businessEndTimeStr);
                Date businessEndTime = sf1.parse(dateStr);
                if(businessEndTime.getTime() > now.getTime())
                {
                    logger.debug(String.format("now is %s and business end time is %s, so stop record backup",
                            sf1.format(now), sf1.format(businessEndTime)));
                    break;
                }
            }
            PlatformRecordBackupResultSumVo sumVo = this.platformRecordService.backup(backupDate);
            this.notifyService.notify(sumVo);
            ca.setTime(backupDate);
            ca.add(Calendar.DATE, 1);
            backupDate = ca.getTime();
            nextBackupDatePo = new NextBackupDatePo();
            nextBackupDatePo.setUpdateTime(new Date());
            nextBackupDatePo.setNextBackupDate(backupDate);
            nextBackupDateDao.insert(nextBackupDatePo);
        }
    }
}
