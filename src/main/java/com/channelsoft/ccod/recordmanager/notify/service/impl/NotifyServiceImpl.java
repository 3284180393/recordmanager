package com.channelsoft.ccod.recordmanager.notify.service.impl;

import com.channelsoft.ccod.recordmanager.backup.vo.PlatformRecordBackupResultVo;
import com.channelsoft.ccod.recordmanager.config.DingDingGroup;
import com.channelsoft.ccod.recordmanager.config.RecordBackupNotifyCfg;
import com.channelsoft.ccod.recordmanager.config.RecordCheckNotifyCfg;
import com.channelsoft.ccod.recordmanager.monitor.vo.EntRecordCheckResultVo;
import com.channelsoft.ccod.recordmanager.monitor.vo.PlatformRecordCheckResultVo;
import com.channelsoft.ccod.recordmanager.notify.service.INotifyService;
import com.channelsoft.ccod.recordmanager.notify.vo.RobotClient;
import com.channelsoft.ccod.recordmanager.notify.vo.TextMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    @PostConstruct
    public void init()
    {
        System.out.println("$$$$$$$$$$$$$$$$$$$$");
//        PlatformRecordCheckResultVo resultVo = PlatformRecordCheckResultVo.fail("shltPA", "上海联通平安", "无法连接数据库");
//        notify(resultVo);
    }

    @Override
    public void notify(PlatformRecordCheckResultVo checkResultVo) {
        if(!checkResultVo.isResult())
        {
            for(DingDingGroup group : this.recordCheckNotifyCfg.getDingding().getGroup())
            {
                String msg = String.format("%s %s", group.getTag(), checkResultVo.getComment());
                logger.debug(String.format("send message[%s] to %s", msg, group.getWebHookToken() ));
                TextMessage message = new TextMessage(msg, group.isAtAll(), group.getAtList());
                try
                {
                    RobotClient.send(group.getWebHookToken(), message);
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }
        }
        else
        {
            for(EntRecordCheckResultVo entRecordCheckResultVo : checkResultVo.getEntRecordCheckResultList())
            {
                if(!entRecordCheckResultVo.isResult())
                {
                    for(DingDingGroup group : this.recordCheckNotifyCfg.getDingding().getGroup())
                    {
                        String msg = String.format("%s %s", group.getTag(), entRecordCheckResultVo.getComment());
                        logger.debug(String.format("send message[%s] to %s", msg, group.getWebHookToken() ));
                        TextMessage message = new TextMessage(msg, group.isAtAll(), group.getAtList());
                        try
                        {
                            RobotClient.send(group.getWebHookToken(), message);
                        }
                        catch (Exception ex)
                        {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    @Override
    public void notify(PlatformRecordBackupResultVo backupResultVo) {
        for(DingDingGroup group : this.recordCheckNotifyCfg.getDingding().getGroup())
        {
            String msg = String.format("%s %s", group.getTag(), backupResultVo.getComment());
            logger.debug(String.format("send message[%s] to %s", msg, group.getWebHookToken() ));
            TextMessage message = new TextMessage(msg, group.isAtAll(), group.getAtList());
            try
            {
                RobotClient.send(group.getWebHookToken(), message);
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
    }
}
