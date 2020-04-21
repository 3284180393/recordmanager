package com.channelsoft.ccod.recordmanager.notify.service.impl;

import com.channelsoft.ccod.recordmanager.config.DingDingGroup;
import com.channelsoft.ccod.recordmanager.config.RecordBackupNotifyCfg;
import com.channelsoft.ccod.recordmanager.config.RecordCheckNotifyCfg;
import com.channelsoft.ccod.recordmanager.monitor.vo.EntRecordCheckResultSumVo;
import com.channelsoft.ccod.recordmanager.monitor.vo.PlatformRecordBackupResultSumVo;
import com.channelsoft.ccod.recordmanager.monitor.vo.PlatformRecordCheckResultSumVo;
import com.channelsoft.ccod.recordmanager.notify.service.INotifyService;
import com.channelsoft.ccod.recordmanager.notify.vo.RobotClient;
import com.channelsoft.ccod.recordmanager.notify.vo.SendResult;
import com.channelsoft.ccod.recordmanager.notify.vo.TextMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * @ClassName: NotifyServiceImpl
 * @Author: lanhb
 * @Description: 消息通知接口实现类
 * @Date: 2020/4/6 18:28
 * @Version: 1.0
 */
@Service
public class NotifyServiceImpl implements INotifyService {

    private final static Logger logger = LoggerFactory.getLogger(NotifyServiceImpl.class);

    @Autowired
    RecordCheckNotifyCfg recordCheckNotifyCfg;

    @Autowired
    RecordBackupNotifyCfg recordBackupNotifyCfg;

    @Value("${notify.record-check.indexLostCount}")
    private int indexLostCount;

    @Value("${notify.record-check.indexLostRate}")
    private int indexLostRate;

    @Value("${notify.record-check.fileLostCount}")
    private int fileLostCount;

    @Value("${notify.record-check.fileLostRate}")
    private int fileLostRate;

    @Value("${notify.record-check.bakIndexLostCount}")
    private int bakIndexLostCount;

    @Value("${notify.record-check.bakIndexLostRate}")
    private int bakIndexLostRate;

    @Value("${notify.record-check.bakFileLostCount}")
    private int bakFileLostCount;

    @Value("${notify.record-check.bakFileLostRate}")
    private int bakFileLostRate;

    @PostConstruct
    public void init()
    {
        System.out.println("$$$$$$$$$$$$$$$$$$$$");
//        PlatformRecordCheckResultVo resultVo = PlatformRecordCheckResultVo.fail("shltPA", "上海联通平安", "无法连接数据库");
//        notify(resultVo);
    }

    @Override
    public void notify(PlatformRecordCheckResultSumVo checkResultVo) {
        if(!checkResultVo.isResult())
        {
            logger.debug(String.format("platform record check fail, need notify"));
            notifyRecordCheckMsg(checkResultVo.getComment());
        }
        else
        {

            for(EntRecordCheckResultSumVo entRecordCheckResultVo : checkResultVo.getEntRecordCheckResultList())
            {
                int recordCount = entRecordCheckResultVo.getAllRecordCount();
                boolean needNotify = false;
                String tag = String.format("%s(%s)", entRecordCheckResultVo.getEnterpriseId(), entRecordCheckResultVo.getEnterpriseName());
                if(!entRecordCheckResultVo.isResult()) {
                    logger.debug(String.format("%s record check fail, need notify", tag));
                    needNotify = true;
                }
                else if(recordCount > 0)
                {
                    if(entRecordCheckResultVo.getNotIndexList().size() >= this.indexLostCount)
                    {
                        logger.debug(String.format("%s record index loss count %d beyond threshold %d, need notify",
                                tag, entRecordCheckResultVo.getNotIndexList().size(), this.indexLostCount));
                        needNotify = true;
                    }
                    else if((entRecordCheckResultVo.getNotIndexList().size() * 100 /recordCount) >= this.indexLostRate)
                    {
                        logger.debug(String.format("%s record index loss rate %d beyond threshold %d, need notify",
                                tag, (entRecordCheckResultVo.getNotIndexList().size() * 100 /recordCount), this.indexLostRate));
                        needNotify = true;
                    }
                    else if(entRecordCheckResultVo.getNotFileList().size() >= this.fileLostCount)
                    {
                        logger.debug(String.format("%s record file loss count %d beyond threshold %d, need notify",
                                tag, entRecordCheckResultVo.getNotFileList().size(), this.fileLostCount));
                        needNotify = true;
                    }
                    else if((entRecordCheckResultVo.getNotFileList().size() * 100 /recordCount) >= this.fileLostRate)
                    {
                        logger.debug(String.format("%s record file loss rate %d beyond threshold %d, need notify",
                                tag, (entRecordCheckResultVo.getNotFileList().size() * 100 /recordCount), this.fileLostRate));
                        needNotify = true;
                    }
                    else if(entRecordCheckResultVo.isHasBak())
                    {
                        if(entRecordCheckResultVo.getNotBakIndexList().size() >= this.bakIndexLostCount)
                        {
                            logger.debug(String.format("%s record bak index loss count %d beyond threshold %d, need notify",
                                    tag, entRecordCheckResultVo.getNotBakIndexList().size(), this.bakIndexLostCount));
                            needNotify = true;
                        }
                        else if((entRecordCheckResultVo.getNotBakIndexList().size() * 100 /recordCount) >= this.bakIndexLostRate)
                        {
                            logger.debug(String.format("%s record bak index loss rate %d beyond threshold %d, need notify",
                                    tag, (entRecordCheckResultVo.getNotBakIndexList().size() * 100 /recordCount), this.bakIndexLostRate));
                            needNotify = true;
                        }
                        else if(entRecordCheckResultVo.getNotBakFileList().size() >= this.bakFileLostCount)
                        {
                            logger.debug(String.format("%s record bak file loss count %d beyond threshold %d, need notify",
                                    tag, entRecordCheckResultVo.getNotBakFileList().size(), this.bakFileLostCount));
                            needNotify = true;
                        }
                        else if((entRecordCheckResultVo.getNotBakFileList().size() * 100 /recordCount) >= this.bakFileLostRate)
                        {
                            logger.debug(String.format("%s record file bak loss rate %d beyond threshold %d, need notify",
                                    tag, (entRecordCheckResultVo.getNotBakFileList().size() * 100 /recordCount), this.bakFileLostRate));
                            needNotify = true;
                        }
                    }
                }
                if(needNotify)
                {
                    notifyRecordCheckMsg(entRecordCheckResultVo.getComment());
                }
                else
                    logger.debug(String.format("%s record check result need not notify", tag));

            }
        }
    }

    private void notifyRecordCheckMsg(String msg)
    {
        logger.debug(String.format("need notify record check msg : %s", msg));
        for(DingDingGroup group : this.recordCheckNotifyCfg.getDingding().getGroup())
        {
            notifyByDingding(msg, group);
        }
        if(this.recordCheckNotifyCfg.getSysLog() != null && this.recordCheckNotifyCfg.getSysLog().isWrite())
            writeToSysLog(msg, recordCheckNotifyCfg.getSysLog().getTag());
    }

    @Override
    public void notify(PlatformRecordBackupResultSumVo backupResultVo) {
        for(DingDingGroup group : this.recordBackupNotifyCfg.getDingding().getGroup())
        {
            String msg = String.format("%s %s", group.getTag(), backupResultVo.getComment());
            notifyByDingding(msg, group);
        }
        if(this.recordBackupNotifyCfg.getSysLog() != null && this.recordBackupNotifyCfg.getSysLog().isWrite())
            writeToSysLog(backupResultVo.getComment(), recordCheckNotifyCfg.getSysLog().getTag());
    }

    private void notifyByDingding(String noticeMsg, DingDingGroup group)
    {
        String msg = String.format("%s %s", group.getTag(), noticeMsg);
        logger.debug(String.format("send message[%s] to %s", msg, group.getWebHookToken() ));
        TextMessage message = new TextMessage(msg, group.isAtAll(), group.getAtList());
        try
        {
            SendResult sendResult = RobotClient.send(group.getWebHookToken(), message);
            if(!sendResult.isSuccess())
            {
                logger.error(String.format("send [%s] to %s fail : errorCode=%s and errorMsg=%s",
                        noticeMsg, group.getWebHookToken(), sendResult.getErrorCode(), sendResult.getErrorMsg()));
            }
        }
        catch (Exception ex)
        {
            logger.error(String.format("send [%s] to %s exception", noticeMsg, group.getWebHookToken()), ex);
        }
    }

    private void writeToSysLog(String msg, String tag)
    {
        logger.debug(String.format("write %s to sysLog with tag=%s", msg, tag));
        try
        {
            Runtime runtime = Runtime.getRuntime();
            String command = String.format("/bin/logger -p local0.crit \"%s:%s\"", tag, msg);
            logger.debug(String.format("begin to exec %s", command));
            runtime.exec(command);
            logger.debug("write msg to sysLog success");
        }
        catch (Exception ex)
        {
            logger.error(String.format("write %s to sysLog exception", msg), ex);
        }
    }
}
