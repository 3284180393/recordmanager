package com.channelsoft.ccod.recordmanager.ontimer;

import com.channelsoft.ccod.recordmanager.config.RecordCheckCondition;
import com.channelsoft.ccod.recordmanager.monitor.dao.INextBackupDateDao;
import com.channelsoft.ccod.recordmanager.monitor.service.IPlatformRecordService;
import com.channelsoft.ccod.recordmanager.monitor.vo.PlatformRecordCheckResultSumVo;
import com.channelsoft.ccod.recordmanager.notify.service.INotifyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Conditional;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Calendar;
import java.util.Date;

/**
 * @ClassName: PlatformRecordCheckOntimer
 * @Author: lanhb
 * @Description: 录音检查定时器
 * @Date: 2020/4/9 20:01
 * @Version: 1.0
 */
@Conditional(RecordCheckCondition.class)
@Component
@EnableScheduling
public class PlatformRecordCheckOntimer {

    public final static Logger logger = LoggerFactory.getLogger(PlatformRecordCheckOntimer.class);

    @Autowired
    IPlatformRecordService platformRecordService;

    @Autowired
    INotifyService notifyService;

    @Value("${ccod.platformId}")
    protected String platformId;

    @Value("${ccod.platformName}")
    protected String platformName;

    @Value("${jobs.recordCheck.delay}")
    private int delay;

    @Value("${jobs.recordCheck.interval}")
    private int interval;

    @Value("${jobs.recordCheck.execute}")
    private boolean isExecute;

    @Value("${debug}")
    private boolean debug;

    @PostConstruct
    private void init() throws Exception
    {
        if(!debug && this.interval < 10)
        {
            logger.error(String.format("min of jobs.recordCheck.interval must be 10 not %d", this.interval));
            throw new Exception(String.format("min of jobs.recordCheck.interval must be 10 not %d", this.interval));
        }
    }

    @Scheduled(cron = "${jobs.recordCheck.cron}")
    private void start()
    {
        Calendar ca = Calendar.getInstance();
        ca.set(Calendar.MILLISECOND, 0);
        ca.set(Calendar.SECOND, 0);
        ca.add(Calendar.MINUTE, (0-delay));
        Date endTime = ca.getTime();
        ca.add(Calendar.MINUTE, (0-interval));
        Date beginTime = ca.getTime();
        PlatformRecordCheckResultSumVo checkResultVo;
        try
        {
            logger.debug(String.format("start platform record check task from %s to %s", beginTime, endTime));
            checkResultVo = platformRecordService.check(beginTime, endTime);
        }
        catch (Exception e)
        {
            logger.error(String.format("check %s(%s) record exception", this.platformName, this.platformId), e);
            checkResultVo = PlatformRecordCheckResultSumVo.fail(this.platformId, this.platformName, e.getMessage());
        }
        logger.info(String.format("platform record check task from %s to %s finish : %s",
                beginTime, endTime, checkResultVo.getComment()));
        notifyService.notify(checkResultVo);
    }
}
