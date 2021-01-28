package com.channelsoft.ccod.recordmanager.monitor.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.channelsoft.ccod.recordmanager.backup.vo.StoredRecordFileVo;
import com.channelsoft.ccod.recordmanager.config.*;
import com.channelsoft.ccod.recordmanager.constant.BackupMethod;
import com.channelsoft.ccod.recordmanager.constant.CCODPlatformType;
import com.channelsoft.ccod.recordmanager.constant.RecordCheckResult;
import com.channelsoft.ccod.recordmanager.constant.RecordType;
import com.channelsoft.ccod.recordmanager.monitor.dao.*;
import com.channelsoft.ccod.recordmanager.monitor.po.*;
import com.channelsoft.ccod.recordmanager.monitor.service.IPlatformRecordService;
import com.channelsoft.ccod.recordmanager.monitor.vo.*;
import com.channelsoft.ccod.recordmanager.notify.service.INotifyService;
import com.channelsoft.ccod.recordmanager.utils.GrokParser;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.DigestUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @ClassName: PlatformRecordBaseService
 * @Author: lanhb
 * @Description: 平台录音服务基类，实现了不同平台录音服务的一些通用方法
 * @Date: 2020/4/9 9:32
 * @Version: 1.0
 */
public abstract  class PlatformRecordBaseService implements IPlatformRecordService {

    private final static Logger logger = LoggerFactory.getLogger(PlatformRecordBaseService.class);

    @Autowired
    protected RecordStoreCfg recordStoreCfg;

    @Autowired
    EnterpriseCfg enterpriseCfg;

    @Autowired
    INotifyService notifyService;

    @Autowired
    IPlatformRecordBackupResultDao platformRecordBackupResultDao;

    @Autowired
    IPlatformRecordCheckResultDao platformRecordCheckResultDao;

    @Autowired
    IEntRecordCheckResultDao entRecordCheckResultDao;

    @Autowired
    ICheckFailRecordDetailDao checkFailRecordDetailDao;

    @Autowired
    IFailBackupRecordFileDao failBackupRecordFileDao;

    @Autowired
    IMissBackupRecordDetailDao missBackupRecordDetailDao;

    @Autowired
    IEnterpriseDao enterpriseDao;

    @Autowired
    IRecordDetailDao recordDetailDao;

    @Autowired
    BakRecordIndexDao bakRecordIndexDao;

    @Value("${env.windows}")
    private boolean isWin;

    @Value("${ccod.platformId}")
    protected String platformId;

    @Value("${ccod.platformName}")
    protected String platformName;

    @Value("${ccod.hasBak}")
    protected boolean hasBak;

    @Value("${jobs.backup.backupRootPath}")
    protected String backupRootPath;

    @Value("${jobs.backup.verify}")
    protected boolean verify;

    @Value("${jobs.backup.compareWithDB}")
    protected boolean compareWithDB;

    @Value("${ccod.platformType}")
    protected CCODPlatformType platformType;

    @Value("${jobs.recordCheck.execute}")
    protected boolean isCheck;

    @Value("${jobs.backup.execute}")
    protected boolean isBackup;

    @Value("${debug}")
    private boolean debug;

    protected int threadPoolSize = 4;

    /**
     * 检查平台录音配置
     * @throws Exception 平台录音配置检查失败
     */
    protected void cfgCheck() throws Exception
    {
        {
            System.out.println(isEnterpriseChosen("1112345678"));
        }
        if(!this.isCheck && !this.isBackup)
        {
            logger.warn("need not backup and check, so dont check cfg from check and backup");
            return;
        }
        if(this.isBackup)
        {
            if(StringUtils.isBlank(this.backupRootPath))
            {
                logger.error("before backup you must define jobs.backup.backupRootPath");
                throw new Exception("before backup you must define jobs.backup.backupRootPath");
            }
            if("/".equals(this.backupRootPath))
            {
                logger.error("jobs.backup.backupRootPath can not be /");
                throw new Exception("jobs.backup.backupRootPath can not be /");
            }
            File file = new File(this.backupRootPath);
            if(!file.exists())
            {
                logger.error(String.format("directory %s of jobs.backup.backupRootPath not exist", this.backupRootPath));
                throw new Exception(String.format("directory %s of jobs.backup.backupRootPath not exist", this.backupRootPath));
            }
            else if(!file.isDirectory())
            {
                logger.error(String.format("%s of jobs.backup.backupRootPath is not directory", this.backupRootPath));
                throw new Exception(String.format("%s of jobs.backup.backupRootPath is not directory", this.backupRootPath));
            }
            this.backupRootPath = this.backupRootPath.replaceAll("/$", "");
            logger.debug(String.format("backupRootPath=%s", backupRootPath));
        }
        if(recordStoreCfg.getMaster() == null || recordStoreCfg.getMaster().getStoreRules() == null
                || recordStoreCfg.getMaster().getStoreRules().size() == 0)
        {
            logger.error(String.format("record store role for master[record.master] can not be empty"));
            throw new Exception(String.format("record store role for master[record.master] can not be empty"));
        }
        for(int i = 0; i < recordStoreCfg.getMaster().getStoreRules().size(); i++)
        {
            checkStoreRule(recordStoreCfg.getMaster().getStoreRules().get(i), i, true);
        }
        List<RecordStoreRule> allRules = new ArrayList<>();
        allRules.addAll(this.recordStoreCfg.getMaster().getStoreRules());
        if(hasBak)
        {
            if(recordStoreCfg.getBackup() == null || recordStoreCfg.getBackup().getStoreRules() == null
                    || recordStoreCfg.getBackup().getStoreRules().size() == 0)
            {
                logger.error(String.format("record store role for backup[record.backup] can not be empty"));
                throw new Exception(String.format("record store role for backup[record.backup] can not be empty"));
            }
            for(int i = 0; i < recordStoreCfg.getBackup().getStoreRules().size(); i++)
            {
                checkStoreRule(recordStoreCfg.getBackup().getStoreRules().get(i), i, false);
            }
            allRules.addAll(this.recordStoreCfg.getBackup().getStoreRules());
        }
        groupStoreRuleCheck(allRules);
        logger.debug(String.format("%d record store rule check success", allRules.size()));
    }

    /**
     * 检查一组录音备份规则之间是否有冲突
     * @param rules 一组录音备份规则
     * @throws Exception 如果有异常，将会以异常的形式抛出
     */
    protected void groupStoreRuleCheck(List<RecordStoreRule> rules) throws Exception
    {
        Map<String, List<RecordStoreRule>> gorkMap = rules.stream().collect(Collectors.groupingBy(RecordStoreRule::getGrokPattern));
        for(String grokPattern : gorkMap.keySet())
        {
            if(gorkMap.get(grokPattern).size() > 1)
            {
                logger.error(String.format("gork pattern %s is not unique", grokPattern));
                throw new Exception(String.format("gork pattern %s is not unique", grokPattern));
            }
        }
        Map<String, List<RecordStoreRule>> exampleMap = this.recordStoreCfg.getMaster().getStoreRules()
                .stream().collect(Collectors.groupingBy(RecordStoreRule::getExample));
        for(String example : exampleMap.keySet())
        {
            if(exampleMap.get(example).size() > 1)
            {
                logger.error(String.format("example %s is not unique", example));
                throw new Exception(String.format("example %s is not unique", example));
            }
        }
        for(String example : exampleMap.keySet()) {
            for (String grokPattern : gorkMap.keySet()) {
                if (GrokParser.match(grokPattern, example) != null) {
                    if (!exampleMap.get(example).get(0).getGrokPattern().equals(grokPattern)) {
                        logger.error(String.format("grok pattern semantics is not unique : %s is matched for %s and %s",
                                example, grokPattern, exampleMap.get(example).get(0).getGrokPattern()));
                        throw new Exception(String.format("grok pattern semantics is not unique : %s is matched for %s and %s",
                                example, grokPattern, exampleMap.get(example).get(0).getGrokPattern()));
                    }
                }
            }
        }
    }

    /**
     * 检查某条录音存储规则
     * @param rule 录音存储规则
     * @param i 索引
     * @param isMaster 是否是主录音
     * @throws Exception 存储规则检查失败
     */
    protected void checkStoreRule(RecordStoreRule rule, int i, boolean isMaster) throws Exception
    {
        Calendar ca = Calendar.getInstance();
        DateFormat dateFormatCfg = rule.getDateFormat();
        String tag = String.format("record.%s.storeRules[%d]", isMaster ? "master" : "backup", i);
        if(StringUtils.isBlank(rule.getGrokPattern()))
        {
            logger.error(String.format("gork pattern of %s is blank", tag));
            throw new Exception(String.format("gork pattern of %s is blank", tag));
        }
        if(StringUtils.isBlank(rule.getExample()))
        {
            logger.error(String.format("example of %s is blank", tag));
            throw new Exception(String.format("example of %s is blank", tag));
        }
        if(StringUtils.isBlank(rule.getRecordIndex()))
        {
            logger.error(String.format("recordIndex of %s is blank", tag));
            throw new Exception(String.format("recordIndex of %s is blank", tag));
        }
        if(!rule.getExample().matches(String.format(".*/%s$", rule.getRecordIndex().replaceAll("^/", ""))))
        {
            logger.debug(String.format("%s is not legal recordIndex of %s in %s",
                    rule.getRecordIndex(), rule.getExample(), tag));
            throw new Exception(String.format("%s is not legal recordIndex of %s in %s",
                    rule.getRecordIndex(), rule.getExample(), tag));
        }
        rule.setMntDir(rule.getExample().replaceAll(String.format("%s$", rule.getRecordIndex()),  "").replaceAll("/$", ""));
        File file = new File(rule.getMntDir());
        if(!file.exists())
        {
            logger.error(String.format("directory %s of %s not exist", rule.getMntDir(), tag));
            throw new Exception(String.format("directory %s of %s not exist", rule.getMntDir(), tag));
        }
        Map<String, Object> matchMap = GrokParser.match(rule.grokPattern, rule.getExample());
        if(matchMap == null)
        {
            logger.error(String.format("example of %s is not matched for grok pattern", tag));
            throw new Exception(String.format("example of %s is not matched for grok pattern", tag));
        }
        if(!matchMap.containsKey("entId"))
        {
            logger.error(String.format("grok pattern %s of %s mush has define of entId",
                    rule.getGrokPattern(), tag));
            throw new Exception(String.format("grok pattern %s of %s mush has define of entId",
                    rule.getGrokPattern(), tag));
        }
        String date = matchMap.containsKey("date") ? matchMap.get("date").toString() : null;
        if(date != null)
        {
            if(StringUtils.isBlank(dateFormatCfg.getDate()))
            {
                logger.error(String.format("date format of %s is blank", tag));
                throw new Exception(String.format("date format of %s is blank", tag));
            }
            try
            {
                SimpleDateFormat sf = new SimpleDateFormat(dateFormatCfg.getDate());
                sf.parse(date);
            }
            catch (Exception ex)
            {
                logger.error(String.format("%s is error date format for %s in %s",
                        dateFormatCfg.getDate(), date, tag));
                throw new Exception(String.format("%s is error date format for %s in %s",
                        dateFormatCfg.getDate(), date, tag));
            }
            if(debug)
            {
                for(int k = 0; k < 500000; k++)
                {
                    try
                    {
                        String cfg1 = "/home/recordmanager/config/application-big.yml";
                        String cfg2 = "/home/recordmanager/config/application-cloud.yml";
                        FileInputStream file1 = new FileInputStream(cfg1);
                        FileInputStream file2 = new FileInputStream(cfg2);
                        String md51 = DigestUtils.md5DigestAsHex(file1);
                        String md52 = DigestUtils.md5DigestAsHex(file2);
                        System.out.println(String.format("%d cfg1=%s and cfg2=%s : %b", k, md51, md52, md51.equals(md52)));
                        file1.close();
                        file2.close();
                    }
                    catch (Exception ex)
                    {
                        ex.printStackTrace();
                    }
                }
                for(int j = 0; j < 10000; j++)
                {
                    SimpleDateFormat sf = new SimpleDateFormat(dateFormatCfg.getDate());
                    String testStr = rule.getExample().replaceAll(date, sf.format(ca.getTime()));
                    if(GrokParser.match(rule.getGrokPattern(), testStr) == null)
                    {
                        logger.error(String.format("grok error : %s not match  %s", rule.getGrokPattern(), testStr));
                        throw new Exception(String.format("grok error : %s not match  %s", rule.getGrokPattern(), testStr));
                    }
                    System.out.println(String.format("%s match for %s", testStr, rule.getGrokPattern()));
                    ca.add(Calendar.DATE, 1);
                }
            }
        }
        String yearAndMonth = matchMap.containsKey("yearAndMonth") ? matchMap.get("yearAndMonth").toString() : null;
        if(yearAndMonth != null)
        {
            if(StringUtils.isBlank(dateFormatCfg.getYearAndMonth()))
            {
                logger.error(String.format("yearAndMonth format of %s is blank", tag));
                throw new Exception(String.format("yearAndMonth format of %s is blank", tag));
            }
            try
            {
                SimpleDateFormat sf = new SimpleDateFormat(dateFormatCfg.getYearAndMonth());
                sf.parse(yearAndMonth);
            }
            catch (Exception ex)
            {
                logger.error(String.format("%s is error yearAndMonth format for %s in %s",
                        dateFormatCfg.getYearAndMonth(), yearAndMonth, tag));
                throw new Exception(String.format("%s is error yearAndMonth format for %s in %s",
                        dateFormatCfg.getYearAndMonth(), yearAndMonth, tag));
            }
        }
        String monthAndDay = matchMap.containsKey("monthAndDay") ? matchMap.get("monthAndDay").toString() : null;
        if(monthAndDay != null)
        {
            if(StringUtils.isBlank(dateFormatCfg.getMonthAndDay()))
            {
                logger.error(String.format("monthAndDay format of %s is blank", tag));
                throw new Exception(String.format("monthAndDay format of %s is blank", tag));
            }
            try
            {
                SimpleDateFormat sf = new SimpleDateFormat(dateFormatCfg.getMonthAndDay());
                sf.parse(monthAndDay);
            }
            catch (Exception ex)
            {
                logger.error(String.format("%s is error monthAndDay format for %s in %s",
                        dateFormatCfg.getMonthAndDay(), monthAndDay, tag));
                throw new Exception(String.format("%s is error monthAndDay format for %s in %s",
                        dateFormatCfg.getMonthAndDay(), monthAndDay, tag));
            }
            if(debug)
            {
                SimpleDateFormat sf1 = new SimpleDateFormat("yyyyMM");
                SimpleDateFormat sf2 = new SimpleDateFormat("MMdd");
                for(int j = 0; j < 10000; j++)
                {
                    String testStr = rule.getExample().replaceAll(yearAndMonth, sf1.format(ca.getTime()));
                    testStr = testStr.replaceAll(monthAndDay, sf2.format(ca.getTime()));
                    if(GrokParser.match(rule.getGrokPattern(), testStr) == null)
                    {
                        logger.error(String.format("grok error : %s not match  %s", rule.getGrokPattern(), testStr));
                        throw new Exception(String.format("grok error : %s not match  %s", rule.getGrokPattern(), testStr));
                    }
                    System.out.println(String.format("%s match for %s", testStr, rule.getGrokPattern()));
                    ca.add(Calendar.DATE, 1);
                }
            }
        }
        String year = matchMap.containsKey("year") ? matchMap.get("year").toString() : null;
        if(year != null)
        {
            if(StringUtils.isBlank(dateFormatCfg.getYear()))
            {
                logger.error(String.format("year format of %s is blank", tag));
                throw new Exception(String.format("year format of %s is blank", tag));
            }
            try
            {
                SimpleDateFormat sf = new SimpleDateFormat(dateFormatCfg.getYear());
                sf.parse(year);
            }
            catch (Exception ex)
            {
                logger.error(String.format("%s is error year format for %s in %s",
                        dateFormatCfg.getYear(), year, tag));
                throw new Exception(String.format("%s is error year format for %s in %s",
                        dateFormatCfg.getYear(), year, tag));
            }
        }
        String month = matchMap.containsKey("month") ? matchMap.get("month").toString() : null;
        if(month != null)
        {
            if(StringUtils.isBlank(dateFormatCfg.getMonth()))
            {
                logger.error(String.format("month format of %s is blank", tag));
                throw new Exception(String.format("month format of %s is blank", tag));
            }
            try
            {
                SimpleDateFormat sf = new SimpleDateFormat(dateFormatCfg.getMonth());
                sf.parse(month);
            }
            catch (Exception ex)
            {
                logger.error(String.format("%s is error month format for %s in %s",
                        dateFormatCfg.getMonth(), month, tag));
                throw new Exception(String.format("%s is error month format for %s in %s",
                        dateFormatCfg.getMonth(), month, tag));
            }
        }
        String day = matchMap.containsKey("day") ? matchMap.get("day").toString() : null;
        if(day != null)
        {
            if(StringUtils.isBlank(dateFormatCfg.getDay()))
            {
                logger.error(String.format("day format of %s is blank", tag));
                throw new Exception(String.format("day format of %s is blank", tag));
            }
            try
            {
                SimpleDateFormat sf = new SimpleDateFormat(dateFormatCfg.getDay());
                sf.parse(day);
            }
            catch (Exception ex)
            {
                logger.error(String.format("%s is error day format for %s in %s",
                        dateFormatCfg.getDay(), day, tag));
                throw new Exception(String.format("%s is error day format for %s in %s",
                        dateFormatCfg.getDay(), day, tag));
            }
        }
        String errMsg = null;
        if(date != null)
        {
        }
        else if(yearAndMonth != null)
        {
            if(monthAndDay == null && day == null)
                errMsg = String.format("monthAndDay or day is wanted in grok pattern of %s", tag);
        }
        else if(year != null)
        {
            if(monthAndDay == null && (month == null || day == null))
                errMsg = String.format("monthAndDay or month and day is wanted in grok pattern of %s", tag);
        }
        else
        {
            errMsg = String.format("grok pattern of %s has not enough date define", tag);
        }
        if(errMsg != null)
        {
            logger.error(errMsg);
            throw new Exception(errMsg);
        }
        logger.debug(String.format("record store rule[%s] of %s check success", JSONObject.toJSONString(rule), tag));
    }

    @Override
    public PlatformRecordCheckResultSumVo check(Date beginTime, Date endTime) {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        logger.debug(String.format("begin to check platform record from %s to %s",
                sf.format(beginTime), sf.format(endTime)));
        PlatformRecordCheckResultSumVo sumVo;
        try
        {
            sumVo = checkPlatformRecord(beginTime, endTime);
        }
        catch (Exception ex)
        {
            logger.debug(String.format("check platform record from %s to %s exception",
                    sf.format(beginTime), sf.format(endTime)), ex);
            sumVo = PlatformRecordCheckResultSumVo.fail(this.platformId, this.platformName, ex.getMessage());
        }
        try
        {
            addNewPlatformCheckResult(sumVo);
        }
        catch (Exception ex)
        {
            logger.error(String.format("insert platform check result to db exception"), ex);
        }
        return sumVo;
    }

    @Override
    public PlatformRecordBackupResultSumVo backup(Date backupDate){
        Date startTime = new Date();
        PlatformRecordBackupResultSumVo resultVo;
        try
        {
            List<StoredRecordFileVo> fileList = scanRecordFile(backupDate, this.recordStoreCfg.getMaster().getStoreRules());
            List<FailBackupRecordFilePo> failList = backupByCopyDirectory(fileList, this.backupRootPath, this.verify, backupDate);
            if(!this.compareWithDB)
            {
                resultVo = new PlatformRecordBackupResultSumVo(this.platformId, this.platformName, backupDate, startTime, fileList, failList);
            }
            else
            {
                List<RecordDetailVo> notBackList = checkMissBackupRecordDetail(fileList, backupDate);
                resultVo = new PlatformRecordBackupResultSumVo(this.platformId, this.platformName, backupDate, startTime, fileList, failList, notBackList);
            }
        }
        catch (Exception ex)
        {
            SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
            logger.error(String.format("backup %s platform record exception", sf.format(backupDate)), ex);
            resultVo = PlatformRecordBackupResultSumVo.fail(this.platformId, this.platformName, backupDate, ex);
        }
        try
        {
            this.addPlatformRecordBackupResult(resultVo);
        }
        catch (Exception ex)
        {
            logger.error(String.format("add platform backup result exception"), ex);
        }
        return resultVo;
    }

    @Override
    public List<PlatformRecordCheckResultPo> queryPlatformRecordCheckResult(Date beginTime, Date endTime) throws Exception {
        return this.platformRecordCheckResultDao.select(beginTime, endTime);
    }

    @Override
    public List<EntRecordCheckResultPo> queryEntRecordCheckResult(Date beginTime, Date endTime) throws Exception {
        return this.entRecordCheckResultDao.select(beginTime, endTime);
    }

    @Override
    public List<CheckFailRecordDetailPo> queryEntRecordCheckDetail(String enterpriseId, Date beginTime, Date endTime) throws Exception {
        return this.checkFailRecordDetailDao.select(enterpriseId, beginTime, endTime);
    }

    @Override
    public List<PlatformRecordBackupResultPo> queryPlatformRecordBackupResult(Date beginTime, Date endTime) throws Exception {
        return this.platformRecordBackupResultDao.select(beginTime, endTime);
    }

    @Override
    public List<MissBackupRecordDetailPo> queryPlatformBackupMissRecordDetail(Date beginTime, Date endTime) throws Exception {
        return this.missBackupRecordDetailDao.select(beginTime, endTime);
    }

    @Override
    public List<FailBackupRecordFilePo> queryPlatformFailBackupFile(Date beginTime, Date endTime) throws Exception {
        return this.failBackupRecordFileDao.select(beginTime, endTime);
    }

    @Override
    public List<BakRecordIndex> queryPlatformBakRecordIndex(Date beginTime, Date endTime) {
        return this.bakRecordIndexDao.select(beginTime, endTime);
    }

    /**
     * 扫描某个日期的所有录音文件
     * @param scanDate 被扫描的日期
     * @param storeRules 录音存储规则
     * @return 扫描出的素有录音文件
     */
    protected List<StoredRecordFileVo> scanRecordFile(Date scanDate, List<RecordStoreRule> storeRules) throws IOException
    {
        List<StoredRecordFileVo> fileList = new ArrayList<>();
        for(RecordStoreRule storeRule : storeRules)
        {
            List<StoredRecordFileVo> scanList = scanMntDir(storeRule, scanDate);
            logger.debug(String.format("scan mntDir=%s with grokPattern=%s find %d record files",
                    storeRule.getMntDir(), storeRule.getGrokPattern(), scanList.size()));
            fileList.addAll(scanList);
        }
        return fileList;
    }

    /**
     * 检查某天备份完成后应该备份而未备份的所有录音明细
     * @param backupList 已经备份的录音文件
     * @param backupDate 备份的哪一天录音文件
     * @return 检查结果
     */
    protected List<RecordDetailVo> checkMissBackupRecordDetail(List<StoredRecordFileVo> backupList, Date backupDate)
    {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        logger.debug(String.format("begin to check miss backup record details for %s", sf.format(backupDate)));
        List<RecordDetailVo> retList = new ArrayList<>();
        Calendar ca = Calendar.getInstance();
        ca.setTime(backupDate);
        ca.set(Calendar.MILLISECOND, 0);
        ca.set(Calendar.SECOND, 0);
        ca.set(Calendar.MINUTE, 0);
        ca.set(Calendar.HOUR, 0);
        Date beginTime = ca.getTime();
        ca.add(Calendar.DATE, 1);
        Date endTime = ca.getTime();
        PlatformRecordCheckResultSumVo checkResultVo;
        try
        {
            checkResultVo = checkPlatformRecord(beginTime, endTime);
        }
        catch (Exception ex)
        {
            logger.debug(String.format("check platform record from %s to %s exception",
                    beginTime, endTime), ex);
            checkResultVo = PlatformRecordCheckResultSumVo.fail(this.platformId, this.platformName, ex.getMessage());
            notifyService.notify(checkResultVo);
            return retList;
        }
        Map<String, List<StoredRecordFileVo>> entRecordFileMap = backupList.stream().collect(Collectors.groupingBy(StoredRecordFileVo::getEnterpriseId));
        for(EntRecordCheckResultSumVo entRecordCheckResultVo : checkResultVo.getEntRecordCheckResultList()) {
            String enterpriseId = entRecordCheckResultVo.getEnterpriseId();
            if(!entRecordCheckResultVo.isResult())
                logger.debug(String.format("%s check fail, so need not compare miss backup record detail", enterpriseId));
            else if(entRecordCheckResultVo.getAllRecordCount() == 0)
                logger.debug(String.format("checked call count of %s is 0, so need not compare miss backup record detail", enterpriseId));
            else if (!entRecordFileMap.containsKey(enterpriseId)) {
                logger.error(String.format("%s %d record not been backup",
                        enterpriseId, entRecordCheckResultVo.getSuccessList().size()));
                retList.addAll(entRecordCheckResultVo.getSuccessList());
            }
            else
            {
                logger.debug(String.format("%s has backup %d record files", enterpriseId, entRecordFileMap.get(enterpriseId).size()));
                Map<String, StoredRecordFileVo> fileMap = entRecordFileMap.get(enterpriseId)
                        .stream().collect(Collectors.toMap(StoredRecordFileVo::getRecordIndex, Function.identity()));
                for (RecordDetailVo detailVo : entRecordCheckResultVo.getSuccessList()) {
                    if (!fileMap.containsKey(detailVo.getRecordIndex())) {
                        logger.error(String.format("%s[%s] %s not been backup",
                                detailVo.getEnterpriseId(), detailVo.getSessionId(), detailVo.getRecordIndex()));
                        retList.add(detailVo);
                    }
                }
            }

        }
        logger.debug(String.format("find %d miss backup record details %s", retList.size(), sf.format(backupDate)));
        return retList;
    }

    /**
     * 查询录音索引的绝对路径
     * @param indexSearch 录音索引查询参数
     * @return 如果查询到将会返回录音索引对应录音文件的绝对路径,否则返回null
     */
    protected String searchRecordIndexAbsolutePath(RecordIndexSearch indexSearch)
    {
        logger.debug(String.format("search record absolute path for index=%s, master=%b", indexSearch.recordIndex, indexSearch.isMaster));
        RecordStoreRule defaultRule = indexSearch.storeRule;
        String recordIndex = indexSearch.recordIndex;
        if(defaultRule != null)
        {
            String path = searchRecordFile(recordIndex, defaultRule);
            if(path != null)
                return path;
        }
        List<RecordStoreRule> rules = indexSearch.isMaster ? this.recordStoreCfg.getMaster().getStoreRules() : this.recordStoreCfg.getBackup().getStoreRules();
        Map<String, RecordStoreRule> roleMap = rules.stream().collect(Collectors.toMap(RecordStoreRule::getGrokPattern, Function.identity()));
        if(defaultRule != null)
            roleMap.remove(defaultRule.getGrokPattern());
        for(RecordStoreRule rule : roleMap.values())
        {
            String path = searchRecordFile(recordIndex, rule);
            if(path != null) {
                indexSearch.storeRule = rule;
                return path;
            }
        }
        logger.error(String.format("can not find record file for index=%s", recordIndex));
        indexSearch.storeRule = null;
        return null;
    }

    /**
     * 检查某个录音文件存储规则下录音索引对应的录音文件是否存在
     * @param recordIndex 被检查的录音索引
     * @param storeRule 指定的录音存储规则
     * @return 如果存在返回录音索引对应的录音文件绝对路径，否则返回null
     */
    protected String searchRecordFile(String recordIndex, RecordStoreRule storeRule)
    {
        String recordFilePath = null;
        String path = storeRule.getExample().replaceAll(storeRule.getRecordIndex() + "$", recordIndex);
        if(GrokParser.match(storeRule.grokPattern, path) != null)
        {
            File file = new File(path);
            if(file.exists() && file.isFile())
            {
                logger.debug(String.format("find %s for index=%s", path, recordIndex));
                recordFilePath = path;
            }
            else
            {
                logger.warn(String.format("%s is match for %s, but not exist", path, storeRule.getGrokPattern()));
            }
        }
        else
        {
            logger.debug(String.format("index=%s not match for %s", recordIndex, storeRule.getGrokPattern()));
        }
        return recordFilePath;
    }

    /**
     * 确认某个企业是否应该检查录音或是备份录音
     * @param enterpriseId 企业id
     * @return 需要检查/备份则返回true否则false
     */
    protected boolean isEnterpriseChosen(String enterpriseId) {
        boolean chosen = false;
        switch (enterpriseCfg.getChoseMethod())
        {
            case ALL:
                chosen = true;
                break;
            case INCLUDE:
                chosen = isMatched(enterpriseId, enterpriseCfg.list);
                break;
            case EXCLUDE:
                chosen = !isMatched(enterpriseId, enterpriseCfg.list);
                break;
        }
        logger.debug(String.format("%s is chosen : %b", enterpriseId, chosen));
        return chosen;
    }

    protected boolean isMatched(String id, List<String> ids){
        for(String str : ids){
            if( id.matches(str)){
                return true;
            }
        }
        return false;
    }

    /**
     * 扫描指定录音存储规则下的某天录音文件
     * @param storeRule 录音存储规则
     * @param chosenDate 指定扫描的时间
     * @return 该天的录音文件
     * @throws IOException
     */
    private List<StoredRecordFileVo> scanMntDir(RecordStoreRule storeRule, Date chosenDate) throws IOException
    {
        String example = storeRule.getExample();
        String grokPattern = storeRule.getGrokPattern();
        List<String> saveDirs = scanStoreDirForDay(chosenDate, storeRule);
        int indexLen = storeRule.getRecordIndex().split("/").length;
        boolean escape = example.matches("^/") ? true : false;
        List<StoredRecordFileVo> retList = new ArrayList<>();
        for(String saveDir : saveDirs)
        {
            File dirFile = new File(saveDir);
            for(File file : dirFile.listFiles())
            {
                if(!file.isFile())
                    continue;
                String filePath = file.getAbsolutePath().replaceAll("\\\\", "/");
                Map<String, Object> parseResultMap = GrokParser.match(grokPattern, filePath);
                if(parseResultMap != null)
                {
                    String enterpriseId = parseResultMap.get("entId").toString();
                    if(!isEnterpriseChosen(enterpriseId))
                    {
                        logger.debug(String.format("[%s] %s need not backup", enterpriseId, filePath));
                        continue;
                    }
                    if(this.isWin)
                        filePath = filePath.replaceAll("\\\\", "/");
                    String[] arr = filePath.split("/");
                    String index = "";
                    for(int i = indexLen; i >=1; i--)
                    {
                        index += "/" + arr[arr.length - i];
                    }
                    if(!escape)
                    {
                        index = index.replaceAll("^/", "");
                    }
                    logger.debug(String.format("[%s] %s with index=%s is wanted record file", enterpriseId, filePath, index));
                    StoredRecordFileVo fileVo = new StoredRecordFileVo(enterpriseId, chosenDate, filePath, index, grokPattern);
                    retList.add(fileVo);
                }
            }
        }
        return retList;
    }


    /**
     * 获取path下面深度为depth的目录/文件
     * @param path 搜索的更露露
     * @param depth 目录/文件深度
     * @param isDir 是目录还是文件
     * @param resultList 用来保存结果的数组
     */
    private void getDepthFile(String path, int depth, boolean isDir, List<String> resultList)
    {
        int currentDepth = path.replaceAll("\\\\", "/").replaceAll("^/", "").split("/").length;
        if(currentDepth > depth)
            return;
        File file = new File(path);
        if(!file.isDirectory() && !file.isFile())
            return;
        if(currentDepth == depth)
        {
            if(file.isDirectory() && isDir)
                resultList.add(path);
            else if(file.isFile() && !isDir)
                resultList.add(path);
            return;
        }
        else
        {
            if(!file.isDirectory())
                return;
            File[] fileList = file.listFiles();
            if(fileList == null || fileList.length == 0)
                return;
            for(File theFile : fileList) {
                if(theFile.isDirectory() || (theFile.isFile() && !isDir))
                    getDepthFile(theFile.getAbsolutePath(), depth, isDir, resultList);
            }
        }
    }


    /**
     * 查找某个存在规则下某一天的所有录音存储目录
     * @param chosenDay 指定的某一天
     * @param storeRule 录音存储规则
     * @return 满足条件的录音存储目录
     */
    protected List<String> scanStoreDirForDay(Date chosenDay, RecordStoreRule storeRule) throws IOException
    {
        List<String> saveDirs = new ArrayList<>();
        File file = new File(storeRule.getMntDir());
        if(!file.exists())
        {
            logger.error(String.format("mntDir=%s not exist", storeRule.getMntDir()));
            return saveDirs;
        }
        else if(!file.isDirectory())
        {
            logger.error(String.format("mntDir=%s is not directory", storeRule.getMntDir()));
            return saveDirs;
        }
        DateFormat dateFormatCfg = storeRule.getDateFormat();
        String example = storeRule.getExample();
        String grokPattern = storeRule.getGrokPattern();
        String mntDir = storeRule.getMntDir();
        Map<String, Object> resultMap = GrokParser.match(grokPattern, storeRule.getExample());
        String date = resultMap.containsKey("date") ? resultMap.get("date").toString() : null;
        String yearAndMonth = resultMap.containsKey("yearAndMonth") ? resultMap.get("yearAndMonth").toString() : null;
        String monthAndDay = resultMap.containsKey("monthAndDay") ? resultMap.get("monthAndDay").toString() : null;
        String year = resultMap.containsKey("year") ? resultMap.get("year").toString() : null;
        String month = resultMap.containsKey("month") ? resultMap.get("month").toString() : null;
        String day = resultMap.containsKey("day") ? resultMap.get("day").toString() : null;

        if(StringUtils.isNotBlank(date))
        {
            SimpleDateFormat sf = new SimpleDateFormat(dateFormatCfg.getDate());
            saveDirs = searchForChosenTimeDir(mntDir, example, date, sf.format(chosenDay));
        }
        else if(StringUtils.isNotBlank(yearAndMonth))
        {
            SimpleDateFormat sf = new SimpleDateFormat(dateFormatCfg.getYearAndMonth());
            List<String> yearAndMonthDirList = searchForChosenTimeDir(mntDir, example, yearAndMonth, sf.format(chosenDay));
            if(StringUtils.isNotBlank(monthAndDay))
            {
                sf = new SimpleDateFormat(dateFormatCfg.getMonthAndDay());
                for(String searchDir : yearAndMonthDirList)
                {
                    List<String> monthAndDayList = searchForChosenTimeDir(searchDir, example, monthAndDay, sf.format(chosenDay));
                    saveDirs.addAll(monthAndDayList);
                }
            }
            else
            {
                sf = new SimpleDateFormat(dateFormatCfg.getDay());
                for(String searchDir : yearAndMonthDirList)
                {
                    List<String> dayList = searchForChosenTimeDir(searchDir, example, day, sf.format(day));
                    saveDirs.addAll(dayList);
                }
            }
        }
        else
        {
            SimpleDateFormat sf = new SimpleDateFormat(dateFormatCfg.getYear());
            List<String> yearList = searchForChosenTimeDir(mntDir, example, year, sf.format(chosenDay));
            if(StringUtils.isNotBlank(monthAndDay))
            {
                sf = new SimpleDateFormat(dateFormatCfg.getMonthAndDay());
                for(String searchDir : yearList)
                {
                    List<String> monthAndDayList = searchForChosenTimeDir(searchDir, example, monthAndDay, sf.format(chosenDay));
                    saveDirs.addAll(monthAndDayList);
                }
            }
            else
            {
                sf = new SimpleDateFormat(dateFormatCfg.getMonth());
                List<String> monthDirList = new ArrayList<>();
                for(String searchDir : yearList)
                {
                    monthDirList.addAll(searchForChosenTimeDir(searchDir, example, month, sf.format(chosenDay)));
                }
                String dayStr = resultMap.get("dayStr").toString();
                sf = new SimpleDateFormat(dateFormatCfg.getDay());
                for(String searchDir : monthDirList)
                {
                    List<String> dayList = searchForChosenTimeDir(searchDir, example, dayStr, sf.format(day));
                    saveDirs.addAll(dayList);
                }
            }
        }
        int wantDepth = storeRule.getExample().replaceAll("^/", "").split("/").length - 1;
        List<String> scannedDirs = new ArrayList<>();
        for(String dir : saveDirs)
            getDepthFile(dir, wantDepth, true, scannedDirs);
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        logger.info(String.format("%s has %d record directory for %s", storeRule.getMntDir(), scannedDirs.size(), sf.format(chosenDay)));
        return scannedDirs;
    }

    private List<String> searchForChosenTimeDir(String searchPath, String srcPath, String srcTime, String dstTime) throws IOException
    {
        List<String> dirList = new ArrayList<>();
        int dept = srcPath.replaceAll("\\\\", "/").replaceAll("^/", "").replaceAll("/$", "").split(String.format("/%s/", srcTime))[0].split("/").length + 1;
        String regex = String.format("^%s$", dstTime);
        logger.debug(String.format("search %s depth=%d and dirPathEnd=/%s sub directory", searchPath, dept, dstTime));
        scan(searchPath, dept, regex, dirList);
        logger.info(String.format("%s has %d depth=%d and dirPathEnd=/%s sub directory", searchPath, dirList.size(), dept, dstTime));
        return dirList;
    }

    /**
     * 检查平台的录音
     * @param beginTime 录音的通话结束时间不得早于此时间
     * @param endTime 录音的通话结束时间不得晚于此时间
     * @return 平台录音检查结果
     */
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
                for(RecordDetailVo detailVo : entRecordList)
                    detailVo.setEnterpriseId(enterpriseVo.getEnterpriseId());
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

    /**
     * 检查企业的录音
     * @param enterpriseVo 被检查的企业
     * @param checkTime 开始检查时间
     * @param beginTime 录音的通话结束时间不得早于此时间
     * @param endTime 录音的通话结束时间不得晚于此时间
     * @param entRecordList 指定时间段内企业呼叫明细
     * @return
     */
    protected EntRecordCheckResultSumVo checkEntRecord(EnterpriseVo enterpriseVo, Date checkTime, Date beginTime, Date endTime, List<RecordDetailVo> entRecordList) {

        List<RecordDetailVo> successList = new ArrayList<>();
        List<RecordDetailVo> notIndexList = new ArrayList<>();
        List<RecordDetailVo> notFileList = new ArrayList<>();
        List<RecordDetailVo> notBakIndexList = new ArrayList<>();
        List<RecordDetailVo> notBakFileList = new ArrayList<>();
        RecordStoreRule storeRole = null;
        RecordStoreRule bkStoreRole = null;
        EntRecordCheckResultSumVo resultVo = null;
        for(RecordDetailVo detailVo : entRecordList)
        {
            //检查录音索引
            String tag = String.format("[entId=%s,sessionId=%s, agentId=%s]", detailVo.getEnterpriseId(), detailVo.getSessionId(), detailVo.getAgentId());
            if(StringUtils.isBlank(detailVo.getRecordIndex()))
            {
                logger.error(String.format("%s can not find record index", tag));
                notIndexList.add(detailVo);
                continue;
            }
            //检查录音文件
            RecordIndexSearch indexSearch = new RecordIndexSearch();
            indexSearch.storeRule = storeRole;
            indexSearch.recordIndex = detailVo.getRecordIndex();
            indexSearch.isMaster = true;
            String fileSavePath = searchRecordIndexAbsolutePath(indexSearch);
            if(StringUtils.isBlank(fileSavePath))
            {
                logger.error(String.format("%s can not find record file", tag));
                notFileList.add(detailVo);
                continue;
            }
            storeRole = indexSearch.storeRule;
            if(isCheckBak(enterpriseVo.getEnterpriseId()))
            {
                if(StringUtils.isBlank(detailVo.getBakRecordIndex()))
                {
                    logger.error(String.format("%s can not find bak record index", tag));
                    notBakIndexList.add(detailVo);
                    continue;
                }
                RecordIndexSearch bakIndexSearch = new RecordIndexSearch();
                bakIndexSearch.recordIndex = detailVo.getBakRecordIndex();
                bakIndexSearch.storeRule = bkStoreRole;
                bakIndexSearch.isMaster = false;
                String bkFileSavePath = searchRecordIndexAbsolutePath(bakIndexSearch);
                if(StringUtils.isBlank(bkFileSavePath))
                {
                    logger.error(String.format("%s can not find bak record file", tag));
                    notBakFileList.add(detailVo);
                    continue;
                }
                bkStoreRole = bakIndexSearch.storeRule;
            }
            logger.debug(String.format("%s record is OK", tag));
            successList.add(detailVo);
        }
        if(isCheckBak(enterpriseVo.getEnterpriseId()))
        {
            resultVo = new EntRecordCheckResultSumVo(enterpriseVo, checkTime, beginTime, endTime, successList,
                    notIndexList, notFileList, notBakIndexList, notBakFileList);
        }
        else
        {
            resultVo = new EntRecordCheckResultSumVo(enterpriseVo, checkTime, beginTime, endTime, successList,
                    notIndexList, notFileList);
        }
        return resultVo;
    }

    /**
     * 获取某个目录下指定深度的且一级目录名满足正则表达式的目录
     * @param pathName 被扫描的目录
     * @param dept 深度,例如/home/record的深度为2，/home/record/1.wav是3
     * @param regex 用来匹配最后一级目录或是文件名的正则表达式
     * @param resultList 扫描结果
     */
    private void scan(String pathName, int dept, String regex, List<String> resultList) throws IOException
    {
        logger.debug(String.format("scan dir=%s for dept=%d and regex=%s directory", pathName, dept, regex));
        String[] arr = pathName.replaceAll("\\\\", "/").replaceAll("^/", "").replaceAll("/$", "").split("/");
        int currentDept = arr.length;
        if(pathName.matches("/lost\\+found$"))
            return;
        else if(currentDept > dept)
            return;
        else if(currentDept == dept)
        {
            if(Pattern.matches(regex, arr[currentDept - 1])) {
                logger.debug(String.format("%s is wanted dir for dept=%d and regex=%s", pathName, dept, regex));
                resultList.add(pathName);
            }
            return;
        }
        File dirFile = new File(pathName);
        File[] fileList = dirFile.listFiles();
        if(fileList == null || fileList.length == 0)
            return;
        for(File file : dirFile.listFiles())
        {
            if(file.isDirectory())
                scan(file.getAbsolutePath(), dept, regex, resultList);
        }
    }

    /**
     * 通过目录拷贝的方式备份企业录音文件
     * @param storedRecordFileList 需要备份的企业录音文件列表
     * @param backupRootDirectory 用来备份录音文件的目录的路径
     * @param isVerify 备份完成后是否进行md5校验
     * @param recordDate 这些被备份的录音文件是哪天的
     * @return 备份失败的录音明细
     * @throws Exception
     */
    protected List<FailBackupRecordFilePo> backupByCopyDirectory(List<StoredRecordFileVo> storedRecordFileList, String backupRootDirectory, boolean isVerify, Date recordDate) throws Exception
    {
        List<FailBackupRecordFilePo> failList = new ArrayList<>();
        Map<String, List<StoredRecordFileVo>> dirFileMap = storedRecordFileList.stream().collect(Collectors.groupingBy(StoredRecordFileVo::getStoreDir));
        final ExecutorService executor = Executors.newFixedThreadPool(threadPoolSize);
        List<FutureTask<List<FailBackupRecordFilePo>>> taskList = new ArrayList<>();
        for(String storeDir : dirFileMap.keySet())
        {
            FutureTask<List<FailBackupRecordFilePo>> task = new FutureTask<>(new Callable<List<FailBackupRecordFilePo>>() {
                @Override
                public List<FailBackupRecordFilePo> call() {
                    List<StoredRecordFileVo> fileList =  dirFileMap.get(storeDir);
                    List<FailBackupRecordFilePo> failList;
                    try
                    {
                        failList = backupDirByCopy(storeDir, fileList, backupRootDirectory, isVerify, recordDate);
                    }
                    catch (Exception ex)
                    {
                        failList = new ArrayList<>();
                        logger.error(String.format("copy files from %s to %s%s exception", storeDir, backupRootDirectory, storeDir), ex);
                        for(StoredRecordFileVo fileVo : fileList)
                        {
                            FailBackupRecordFilePo failPo = new FailBackupRecordFilePo();
                            failPo.setFailReason(ex.getMessage());
                            failPo.setRecordDate(recordDate);
                            failPo.setBackupPath(fileVo.getFileSavePath());
                            failPo.setFileSavePath(fileVo.getFileBackupPath(backupRootPath));
                            failList.add(failPo);
                        }
                    }
                    return failList;
                }
            });
            taskList.add(task);
            executor.execute(task);
        }
        executor.shutdown();
        for(FutureTask<List<FailBackupRecordFilePo>> task : taskList)
        {
            failList.addAll(task.get());
        }
        return failList;
    }

    /**
     * 将某个目录连文件带目录结构copy到另外一个目录，copy后的目录为backupRootDirectory+storeDir
     * @param storeDir 被拷贝的目录
     * @param recordFileList 被拷贝里的录音文件列表
     * @param backupRootDirectory 需要将storeDir连目录带文件拷贝的目的目录
     * @param isVerify 拷贝完成后是否进行md5校验
     * @param recordDate 被备份的录音文件是哪一天的
     * @return 备份失败的录音文件列表
     * @throws IOException
     */
    private List<FailBackupRecordFilePo> backupDirByCopy(String storeDir, List<StoredRecordFileVo> recordFileList, String backupRootDirectory, boolean isVerify, Date recordDate) throws IOException
    {
        List<FailBackupRecordFilePo> failList = new ArrayList<>();
        String backupDir = String.format("%s%s", backupRootDirectory, storeDir);
        if(this.isWin)
            backupDir = String.format("%s%s", backupRootDirectory, storeDir.replaceAll("^[A-Z]:", ""));
        logger.debug(String.format("begin to copy all file from %s to %s", storeDir, backupDir));
        File srcDir = new File(storeDir);
        File dstDir = new File(backupDir);
        if(!dstDir.exists())
            dstDir.mkdirs();
        FileUtils.copyDirectory(srcDir, dstDir);
        for(StoredRecordFileVo fileVo : recordFileList)
        {
            fileVo.setBackupMethod(BackupMethod.COPY);
            fileVo.setBackupSavePath(String.format("%s/%s", backupDir, fileVo.getFileName()));
            if(isVerify)
            {
                File dstFile = new File(fileVo.getBackupSavePath());
                if(!dstFile.exists())
                {
                    logger.error(String.format("backup file verify fail : %s not exist", fileVo.getBackupSavePath()));
                    fileVo.setVerifyResult(false);
                    fileVo.setVerifyComment(String.format("%s not exist", fileVo.getBackupSavePath()));
                    FailBackupRecordFilePo po = new FailBackupRecordFilePo();
                    po.setBackupPath(fileVo.getBackupSavePath());
                    po.setRecordDate(recordDate);
                    po.setFailReason(String.format("%s not exist", fileVo.getBackupSavePath()));
                    failList.add(po);
                }
                else if(!dstFile.isFile())
                {
                    logger.error(String.format("backup file verify fail : %s not file", fileVo.getBackupSavePath()));
                    fileVo.setVerifyResult(false);
                    fileVo.setVerifyComment(String.format("%s not file", fileVo.getBackupSavePath()));
                    FailBackupRecordFilePo po = new FailBackupRecordFilePo();
                    po.setBackupPath(fileVo.getBackupSavePath());
                    po.setRecordDate(recordDate);
                    po.setFailReason(String.format("%s not file", fileVo.getBackupSavePath()));
                    failList.add(po);
                }
                else
                {
                    FileInputStream src = new FileInputStream(fileVo.getBackupSavePath());
                    FileInputStream dst = new FileInputStream(fileVo.getFileSavePath());
                    String dstMd5 = DigestUtils.md5DigestAsHex(src);
                    String srcMd5 = DigestUtils.md5DigestAsHex(dst);
                    src.close();
                    dst.close();
                    if(!dstMd5.equals(srcMd5))
                    {
                        logger.error(String.format("backup file verify fail : %s md5=%s but %s md5=%s ", fileVo.getFileSavePath(), srcMd5, fileVo.getBackupSavePath(), dstMd5));
                        fileVo.setVerifyResult(false);
                        fileVo.setVerifyComment(String.format("%s md5=%s but %s md5=%s ", fileVo.getFileSavePath(), srcMd5, fileVo.getBackupSavePath(), dstMd5));
                        FailBackupRecordFilePo po = new FailBackupRecordFilePo();
                        po.setBackupPath(fileVo.getBackupSavePath());
                        po.setRecordDate(recordDate);
                        po.setFailReason(String.format("%s md5=%s but %s md5=%s ", fileVo.getFileSavePath(), srcMd5, fileVo.getBackupSavePath(), dstMd5));
                        failList.add(po);
                    }
                    else
                    {
                        logger.debug(String.format("copy from %s to %s success", fileVo.getFileSavePath(), fileVo.getBackupSavePath()));
                        fileVo.setResult(true);
                        fileVo.setVerifyResult(true);
                        fileVo.setVerifyComment(String.format("backup file %s verify success", fileVo.getFileSavePath()));
                    }
                }
            }
            else
            {
                fileVo.setResult(true);
            }
        }
        return failList;
    }

    /**
     * 是否检查指定企业的备份录音
     * @param enterpriseId 指定企业的id
     * @return true需要检查指定企业的备份录音，false不用检查指定企业的备份录音
     */
    protected boolean isCheckBak(String enterpriseId)
    {
        boolean isCheck = false;
        if(this.hasBak)
        {
            isCheck = true;
            if(this.enterpriseCfg.getNotCheckBakList() != null && this.enterpriseCfg.getNotCheckBakList().size() > 0)
            {
                Set<String> entSet = new HashSet<>(this.enterpriseCfg.getNotCheckBakList());
                if(entSet.contains(enterpriseId))
                    isCheck = false;
            }
        }
        logger.debug(String.format("%s need check backup record : %b", enterpriseId, isCheck));
        return isCheck;
    }

    /**
     * 将平台录音检查结果加到数据库
     * @param platformRecordCheckResultSumVo 需要添加的平台录音检查结果
     * @return
     */
    protected boolean addNewPlatformCheckResult(PlatformRecordCheckResultSumVo platformRecordCheckResultSumVo)
    {
        if(platformRecordCheckResultSumVo.isResult()){
            platformRecordCheckResultSumVo.getEntRecordCheckResultList().forEach(r->logger.info(r.getCheckDesc()));
        }
        else{
            logger.error(platformRecordCheckResultSumVo.getComment());
        }
        PlatformRecordCheckResultPo platformRecordCheckResultPo = platformRecordCheckResultSumVo.getCheckResult();
        int platformCheckId = this.platformRecordCheckResultDao.insert(platformRecordCheckResultPo);
        for(EntRecordCheckResultSumVo entSumVo : platformRecordCheckResultSumVo.getEntRecordCheckResultList())
        {
            int entCheckId = this.entRecordCheckResultDao.insert(platformCheckId, entSumVo.getCheckResult());
            if(!entSumVo.isResult())
                continue;
            for(RecordDetailVo detailVo : entSumVo.getNotIndexList())
            {
                CheckFailRecordDetailPo po = new CheckFailRecordDetailPo(entSumVo.getEnterpriseName(), detailVo, RecordCheckResult.INDEX_NOT_EXIST);
                checkFailRecordDetailDao.insert(platformCheckId, entCheckId, po);
            }
            for(RecordDetailVo detailVo : entSumVo.getNotFileList())
            {
                CheckFailRecordDetailPo po = new CheckFailRecordDetailPo(entSumVo.getEnterpriseName(), detailVo, RecordCheckResult.FILE_NOT_EXIST);
                checkFailRecordDetailDao.insert(platformCheckId, entCheckId, po);
            }
            for(RecordDetailVo detailVo : entSumVo.getNotBakIndexList())
            {
                CheckFailRecordDetailPo po = new CheckFailRecordDetailPo(entSumVo.getEnterpriseName(), detailVo, RecordCheckResult.BAK_INDEX_NOT_EXIST);
                checkFailRecordDetailDao.insert(platformCheckId, entCheckId, po);
            }
            for(RecordDetailVo detailVo : entSumVo.getNotBakFileList())
            {
                CheckFailRecordDetailPo po = new CheckFailRecordDetailPo(entSumVo.getEnterpriseName(), detailVo, RecordCheckResult.BAK_FILE_NOT_EXIST);
                checkFailRecordDetailDao.insert(platformCheckId, entCheckId, po);
            }
        }
        if(hasBak && platformRecordCheckResultSumVo.getHasBakNotMasterList() != null && platformRecordCheckResultSumVo.getHasBakNotMasterList().size() > 0) {
            platformRecordCheckResultSumVo.getHasBakNotMasterList().forEach(index->this.bakRecordIndexDao.insert(index));
        }
        return true;
    }

    /**
     * 将平台录音备份结果添加到数据库
     * @param platformRecordBackupResultSumVo 需要添加的平台录音备份结果
     */
    protected void addPlatformRecordBackupResult(PlatformRecordBackupResultSumVo platformRecordBackupResultSumVo)
    {
        PlatformRecordBackupResultPo backupResultPo = platformRecordBackupResultSumVo.getBackupResult();
        int backupId = this.platformRecordBackupResultDao.insert(backupResultPo);
        if(platformRecordBackupResultSumVo.isResult())
        {
            for(FailBackupRecordFilePo filePo : platformRecordBackupResultSumVo.getFailList())
            {
                this.failBackupRecordFileDao.insert(backupId, filePo);
            }
            for(RecordDetailVo detailPo : platformRecordBackupResultSumVo.getNotBackupList())
            {
                MissBackupRecordDetailPo po = new MissBackupRecordDetailPo(detailPo);
                this.missBackupRecordDetailDao.insert(backupId, po);
            }
        }
    }

    protected void generateTestDate(PlatformRecordCheckResultSumVo sumVo)
    {
        for(EntRecordCheckResultSumVo entSumVo : sumVo.getEntRecordCheckResultList())
        {
            if(!entSumVo.isResult())
                continue;
            for(int i = 0; i < entSumVo.getNotIndexList().size(); i ++)
            {
                entSumVo.getNotIndexList().get(i).setRecordIndex(String.format("%d/JT04/Agent/20200401/TEL-1169190304_100004_20200401081955.wav", i));
            }
            entSumVo.getSuccessList().addAll(entSumVo.getNotIndexList());
            entSumVo.getSuccessList().addAll(entSumVo.getNotFileList());
            entSumVo.getSuccessList().addAll(entSumVo.getNotBakIndexList());
            entSumVo.getSuccessList().addAll(entSumVo.getNotBakFileList());
            entSumVo.setComment(entSumVo.toString());
        }
    }

    protected void generateTestDate(PlatformRecordBackupResultSumVo sumVo)
    {
        RecordDetailVo detailVo = new RecordDetailVo();
        detailVo.setEnterpriseId("11111");
        detailVo.setRecordIndex("0000050111/202003/60750230/0301/SIP-400603_5860042292_20200317123441.wav");
        detailVo.setRecordType(RecordType.MIX);
        detailVo.setHasBak(true);
        detailVo.setAgentId("2345");
        detailVo.setBakRecordIndex("0000057733/Agent/20200313/SIP-400503_5860042291_20200317113441.wav");
        detailVo.setCallType(1);
        Calendar ca = Calendar.getInstance();
        detailVo.setSessionId("6666688888");
        detailVo.setEndTime(ca.getTime());
        ca.add(Calendar.MINUTE, 22);
        detailVo.setStartTime(ca.getTime());
        detailVo.setEndType(255);
        detailVo.setTalkDuration(123);
        sumVo.getNotBackupList().add(detailVo);
        FailBackupRecordFilePo filePo = new FailBackupRecordFilePo();
        filePo.setFileSavePath("d:/temp/1.txt");
        filePo.setBackupPath("d:/temp/backup/temp/1.txt");
        ca.add(Calendar.HOUR, 1);
        filePo.setRecordDate(ca.getTime());
        filePo.setFailReason("just a test");
        sumVo.getFailList().add(filePo);
        sumVo.setComment(sumVo.toString());
    }

    /**
     * 用来封装录音索引检查参数的类
     */
    protected class RecordIndexSearch
    {
        public String recordIndex;  //需要检查的录音索引

        public RecordStoreRule storeRule; //缺省的录音存储规则,优先用此存储规则检查录音文件，如果此规则检查失败才会用其它的存储规则检查

        public boolean isMaster; //需要检查的录音索引是否是主录音索引
    }
}
