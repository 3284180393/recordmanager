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
            for(DingDingGroup group : this.recordCheckNotifyCfg.getDingding().getGroup())
            {
                notifyByDingding(checkResultVo.getComment(), group);
            }
        }
        else
        {
            for(EntRecordCheckResultSumVo entRecordCheckResultVo : checkResultVo.getEntRecordCheckResultList())
            {
                int recordCount = entRecordCheckResultVo.getAllRecordCount();
                if(entRecordCheckResultVo.isResult() && recordCount == 0) {
                    logger.debug(String.format("%s need not notify", entRecordCheckResultVo.getComment()));
                    continue;
                }
                if(!entRecordCheckResultVo.isResult()
                        || entRecordCheckResultVo.getNotIndexList().size() >= this.indexLostCount
                        || (entRecordCheckResultVo.getNotIndexList().size() * 100 /recordCount) >= this.indexLostRate
                        || entRecordCheckResultVo.getNotFileList().size() >= this.fileLostCount
                        || (entRecordCheckResultVo.getNotFileList().size() * 100 /recordCount) >= this.fileLostRate
                        || entRecordCheckResultVo.getNotBakIndexList().size() >= this.bakIndexLostCount
                        || (entRecordCheckResultVo.getNotBakIndexList().size() * 100 /recordCount) >= this.bakIndexLostRate
                        || entRecordCheckResultVo.getNotBakFileList().size() >= this.bakFileLostCount
                        || (entRecordCheckResultVo.getNotBakFileList().size() * 100 /recordCount) >= this.bakFileLostRate)
                {
                    for(DingDingGroup group : this.recordCheckNotifyCfg.getDingding().getGroup())
                    {
                        notifyByDingding(entRecordCheckResultVo.getComment(), group);
                    }
                    if(this.recordCheckNotifyCfg.getSysLog() != null && this.recordCheckNotifyCfg.getSysLog().isWrite())
                        writeToSysLog(entRecordCheckResultVo.getComment(), recordCheckNotifyCfg.getSysLog().getTag());
                }
                else
                    logger.debug(String.format("%s need not notify", entRecordCheckResultVo.getComment()));
            }
        }
    }

    @Override
    public void notify(PlatformRecordBackupResultSumVo backupResultVo) {
        for(DingDingGroup group : this.recordBackupNotifyCfg.getDingding().getGroup())
        {
            String msg = String.format("%s %s", group.getTag(), backupResultVo.getComment());
            notifyByDingding(msg, group);
        }
        if(this.recordBackupNotifyCfg.getSysLog() != null && this.recordCheckNotifyCfg.getSysLog().isWrite())
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
