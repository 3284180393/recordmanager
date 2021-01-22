package com.channelsoft.ccod.recordmanager.notify.service.impl;

import com.channelsoft.ccod.recordmanager.config.DingDingGroup;
import com.channelsoft.ccod.recordmanager.config.RecordBackupNotifyCfg;
import com.channelsoft.ccod.recordmanager.config.RecordCheckNotifyCfg;
import com.channelsoft.ccod.recordmanager.monitor.vo.EntRecordCheckResultSumVo;
import com.channelsoft.ccod.recordmanager.monitor.vo.PlatformRecordBackupResultSumVo;
import com.channelsoft.ccod.recordmanager.monitor.vo.PlatformRecordCheckResultSumVo;
import com.channelsoft.ccod.recordmanager.notify.service.INotifyService;
import com.channelsoft.ccod.recordmanager.notify.vo.TextMessage;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiRobotSendRequest;
import com.dingtalk.api.response.OapiRobotSendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Base64;

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

    @Autowired
    private Environment env;

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

    private boolean isReportNormalCheckResult = false;

    private boolean reportDingdingByScript = false;

    private String reportDingdingScriptPath = null;

    private boolean reportWechat = false;

    private String reportWechatScriptPath = null;

    private String wechatScriptFileName = null;

    private String wechatLogFileName = null;

    private String wechatTag = null;

    @PostConstruct
    public void init()
    {
        System.out.println("$$$$$$$$$$$$$$$$$$$$");
        if(env.containsProperty("notify.record-check.report-normal-result") && "true".equals(env.getProperty("notify.record-check.report-normal-result")))
            this.isReportNormalCheckResult = true;
        if(env.containsProperty("notify.record-check.dingding.by-script") && "true".equals(env.getProperty("notify.record-check.dingding.by-script")))
        {
            this.reportDingdingByScript = true;
            this.reportDingdingScriptPath = env.getProperty("notify.record-check.dingding.script-path");
            logger.debug(String.format("need to notify to dingding by script %s", reportDingdingScriptPath));
        }
        else{
            logger.debug("not need to notify to dingding by script");
        }
        System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
        System.out.println(env.containsProperty("notify.record-check.wechat.script-path"));
        if(env.containsProperty("notify.record-check.wechat.script-path"))
        {
            this.reportWechat = true;
            this.reportWechatScriptPath = env.getProperty("notify.record-check.wechat.script-path");
            this.wechatScriptFileName =  env.getProperty("notify.record-check.wechat.script-name");
            this.wechatLogFileName = env.getProperty("notify.record-check.wechat.log-file");
            this.wechatTag = env.getProperty("notify.record-check.wechat.wechat-tag");
            logger.debug(String.format("need to notify to wechat by %s/%s, log=%s and tag=%s",
                    reportWechatScriptPath, wechatLogFileName, wechatLogFileName, wechatTag));
        }
        else{
            logger.debug("not need notify to wechat");
        }
        if(reportWechat){
            //            notifyToWechat("这是录音检查结果微信推送测试，\"\"\"\"请勿处理");
            String testMsg = "监控 这是录音检查结果微信推送测试，请勿处理 1 AND RD.END_TIME >= to_date('2020-07-31 17:10:00','yyyy-MM-dd HH24:mi:ss') AND RD.END_TIME < to_date('2020-07-31 17:20:00','yyyy-MM-dd HH24:mi:ss') AND (RD.CALLTYPE=1 OR RD.CALLTYPE=6) AND (RD.END_TYPE=254 OR RD.END_TYPE=255)]; nested exception is java.sql.SQLSyntaxErrorException: ORA-00942:[Syslog]\" target=\"_blank\">root: \"ccod:[20200731 17:31:11]检查shltPA(上海联通平安)录音异常:org.springframework.jdbc.BadSqlGrammarException: StatementCallback; bad SQL grammar [SELECT RD.SESSION_ID AS SESSION_ID,RD.START_TIME AS START_TIME,RD.END_TIME AS END_TIME,RD.AGENT_ID AS AGENT_ID,RD.TALK_DURATION AS TALK_DURATION,RD.END_TYPE AS END_TYPE,RD.CALLTYPE AS CALLTYPE,ERBT.RECORD_NAME AS RECORD_INDEX,BRT.RECORD_NAME AS RECORD_INDEX_BAK FROM \"0000050360\".R_DETAIL RD LEFT JOIN \"0000050360\".ENT_RECORD_BX_TABLE ERBT ON RD.SESSION_ID = ERBT.SESSION_ID AND RD.AGENT_ID = ERBT.AGENT_ID LEFT JOIN \"0000050360\".ENT_RECORD_BX_TABLE_BAK BRT ON RD.SESSION_ID = BRT.SESSION_ID AND RD.AGENT_ID = BRT.AGENT_ID WHERE 1=1 AND RD.TALK_DURATION > 1 AND RD.END_TIME >= to_date('2020-07-31 17:10:00','yyyy-MM-dd HH24:mi:ss') AND RD.END_TIME < to_date('2020-07-31 17:20:00','yyyy-MM-dd HH24:mi:ss') AND (RD.CALLTYPE=1 OR RD.CALLTYPE=6) AND (RD.END_TYPE=254 OR RD.END_TYPE=255)]; nested exception is java.sql.SQLSyntaxErrorException: ORA-00942:[Syslog] ";
//            String testMsg = "这是录音检查结果微信推送测试，请勿处理";
            notifyToWechat(testMsg);
        }
//        PlatformRecordCheckResultVo resultVo = PlatformRecordCheckResultVo.fail("shltPA", "上海联通平安", "无法连接数据库");
//        notify(resultVo);
    }

    @Override
    public void notify(PlatformRecordCheckResultSumVo checkResultVo) {
        if(!checkResultVo.isResult())
        {
            logger.debug(String.format("platform record check fail, need notify"));
            notifyRecordCheckMsg(checkResultVo.getComment(), false);
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
                    notifyRecordCheckMsg(entRecordCheckResultVo.getComment(), checkResultVo.isResult());
                }
                else
                    logger.debug(String.format("%s record check result need not notify", tag));

            }
            if(this.isReportNormalCheckResult)
            {
                logger.debug("notify sum of platform record check result to dingding");
                String msg = getPlatformCheckResultSum(checkResultVo);
                notifyRecordCheckMsg(msg, true);
            }
        }
    }

    private void notifyRecordCheckMsg(String msg, boolean isCheckResultOk)
    {
        logger.debug(String.format("need notify record check msg : %s", msg));
        if(this.reportWechat){
            notifyToWechat(msg);
        }
        if(this.reportDingdingByScript)
            notifyByScript(msg);
        else
            for(DingDingGroup group : this.recordCheckNotifyCfg.getDingding().getGroup())
                notifyByDingding(msg, group);
        if(!isCheckResultOk && this.recordCheckNotifyCfg.getSysLog() != null && this.recordCheckNotifyCfg.getSysLog().isWrite())
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
        sendTextMsgToDingding(group.getWebHookToken(), message);
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

    private void notifyToWechat(String msg){
        logger.debug(String.format("notify %s to wechat by script %s/%s", msg, this.reportWechatScriptPath, this.wechatScriptFileName));
        try
        {
            String sendMsg = msg.replace("\"", "\\\"");
            sendMsg = String.format("%s %s", wechatTag, sendMsg);
            File file = new File(String.format("%s/%s", reportWechatScriptPath, wechatLogFileName));
            file.createNewFile();
            FileWriter writer = new FileWriter(file);
            BufferedWriter out = new BufferedWriter(writer);
            out.write(sendMsg);
            out.close();
            writer.close();
            Thread.sleep(1000);
            Runtime runtime = Runtime.getRuntime();
            String[] cmd = new String[]{"/bin/sh", "-c", String.format(" cd %s && ./%s", reportWechatScriptPath, wechatScriptFileName)};
            logger.debug(String.format("begin to exec %s", String.join(",", cmd)));
            Process process = runtime.exec(cmd);
            InputStream is = process.getErrorStream();
            InputStreamReader isr = new InputStreamReader(is, "GBK");
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
                logger.debug(line);
            }
            logger.debug("notify to wechat by secript success");
        }
        catch (Exception ex)
        {
            logger.error(String.format("notify %s to wechat  exception", msg), ex);
        }
    }

    private void notifyByScript(String msg)
    {
        logger.debug(String.format("notify %s to by script %s", msg, this.reportDingdingScriptPath));
        try
        {
            Runtime runtime = Runtime.getRuntime();
            String notifyMsg = Base64.getEncoder().encodeToString(msg.getBytes("utf-8"));
            String command = String.format("python %s \"%s\"", this.reportDingdingScriptPath, notifyMsg);
            logger.debug(String.format("begin to exec %s", command));
            runtime.exec(command);
            logger.debug("notify success");
        }
        catch (Exception ex)
        {
            logger.error(String.format("write %s to sysLog exception", msg), ex);
        }
    }

    private String getPlatformCheckResultSum(PlatformRecordCheckResultSumVo sumVo)
    {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String msg = String.format("%s(%s)平台(%s--%s)录音检查结果:", sumVo.getPlatformName(), sumVo.getPlatformId(), sf.format(sumVo.getStartTime()), sf.format(sumVo.getEndTime()));
        for(EntRecordCheckResultSumVo entResultVo : sumVo.getEntRecordCheckResultList())
            msg = String.format("%s%s", msg, entResultVo.getCheckDesc());
        return msg;
    }

    private void sendTextMsgToDingding(String webhook, TextMessage message)
    {
        logger.debug(String.format("send %s to %s,  atMobiles=%s and atAll=%b", message.getText(), webhook, String.join(",", message.getAtMobiles()), message.isAtAll()));
        DingTalkClient client = new DefaultDingTalkClient(webhook);
        OapiRobotSendRequest request = new OapiRobotSendRequest();
        request.setMsgtype("text");
        OapiRobotSendRequest.Text text = new OapiRobotSendRequest.Text();
        text.setContent(message.getText());
        request.setText(text);
        OapiRobotSendRequest.At at = new OapiRobotSendRequest.At();
        at.setAtMobiles(message.getAtMobiles());
        at.setIsAtAll(message.isAtAll());
        request.setAt(at);
        try
        {
            OapiRobotSendResponse response = client.execute(request);
            if(response.isSuccess())
                logger.info(String.format("send text message to dingding success"));
            else
                logger.error(String.format("send text message to dingding fail : errorCode=%d and errorMsg=%s",
                        response.getErrcode(), response.getErrmsg()));
        }
        catch (Exception ex)
        {
            logger.error(String.format("send text message to dingding exception : %s", ex.getMessage()), ex);
        }
    }
}
