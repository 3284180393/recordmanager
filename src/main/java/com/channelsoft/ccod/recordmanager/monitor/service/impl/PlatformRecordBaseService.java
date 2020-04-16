package com.channelsoft.ccod.recordmanager.monitor.service.impl;

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

import javax.annotation.PostConstruct;
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

    @Value("${debug}")
    private boolean debug;

    protected int threadPoolSize = 4;

    @PostConstruct
    public void init() throws Exception
    {
        System.out.println("*********************");
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
            List<StoredRecordFileVo> fileList = scanRecordFile(backupDate, this.recordStoreCfg.getMaster().getStoreRoles());
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

    /**
     * 扫描某个日期的所有录音文件
     * @param scanDate 被扫描的日期
     * @param storeRoles 录音存储规则
     * @return 扫描出的素有录音文件
     */
    protected List<StoredRecordFileVo> scanRecordFile(Date scanDate, List<RecordStoreRole> storeRoles) throws IOException
    {
        List<StoredRecordFileVo> fileList = new ArrayList<>();
        for(RecordStoreRole storeRole : storeRoles)
        {
            List<StoredRecordFileVo> scanList = scanMntDir(storeRole, scanDate);
            logger.debug(String.format("scan mntDir=%s with grokPattern=%s find %d record files",
                    storeRole.getMntDir(), storeRole.getGrokPattern(), scanList.size()));
            fileList.addAll(scanList);
        }
        return fileList;
    }

    protected List<RecordDetailVo> checkMissBackupRecordDetail(List<StoredRecordFileVo> backupList, Date backupDate)
    {
        List<RecordDetailVo> retList = new ArrayList<>();
        PlatformRecordCheckResultSumVo checkResultVo;
        Calendar ca = Calendar.getInstance();
        ca.setTime(backupDate);
        ca.set(Calendar.MILLISECOND, 0);
        ca.set(Calendar.SECOND, 0);
        ca.set(Calendar.MINUTE, 0);
        ca.set(Calendar.HOUR, 0);
        Date beginTime = ca.getTime();
        ca.add(Calendar.DATE, 1);
        Date endTime = ca.getTime();
        try
        {
            checkResultVo = check(beginTime, endTime);
            if(debug)
            {
                generateTestDate(checkResultVo);
            }

        }
        catch (Exception ex)
        {
            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            logger.error(String.format("query record detail form %s to %s exception",
                    sf.format(beginTime), sf.format(endTime)), ex);
            checkResultVo = PlatformRecordCheckResultSumVo.fail(this.platformId, this.platformName, ex.getMessage());
            notifyService.notify(checkResultVo);
            return retList;
        }
        Map<String, List<StoredRecordFileVo>> entRecordFileMap = backupList.stream().collect(Collectors.groupingBy(StoredRecordFileVo::getEnterpriseId));
        for(EntRecordCheckResultSumVo entRecordCheckResultVo : checkResultVo.getEntRecordCheckResultList()) {
            if (!entRecordFileMap.containsKey(entRecordCheckResultVo.getEnterpriseId())) {
                logger.warn(String.format("%s %d record not been backup",
                        entRecordCheckResultVo.getEnterpriseId(), entRecordCheckResultVo.getSuccessList().size()));
                retList.addAll(entRecordCheckResultVo.getSuccessList());
                continue;
            }
            Map<String, StoredRecordFileVo> fileMap = entRecordFileMap.get(entRecordCheckResultVo.getEnterpriseId())
                    .stream().collect(Collectors.toMap(StoredRecordFileVo::getRecordIndex, Function.identity()));
            for (RecordDetailVo detailVo : entRecordCheckResultVo.getSuccessList()) {
                if (!fileMap.containsKey(detailVo.getRecordIndex())) {
                    logger.error(String.format("%s[%s] %s not been backup",
                            detailVo.getEnterpriseId(), detailVo.getSessionId(), detailVo.getRecordIndex()));
                    retList.add(detailVo);
                }
            }
        }
        return retList;
    }

    protected String searchRecordIndexAbsolutePath(RecordIndexSearch indexSearch)
    {
        logger.debug(String.format("search record save path for index=%s, master=%b", indexSearch.recordIndex, indexSearch.isMaster));
        RecordStoreRole storeRole = indexSearch.storeRole;
        String recordIndex = indexSearch.recordIndex;
        String path = null;
        if(storeRole != null)
        {
            path = storeRole.getExample().replace(storeRole.getRecordIndex(), recordIndex);
            if(GrokParser.match(storeRole.grokPattern, path) != null)
            {
                File file = new File(path);
                if(file.exists() && file.isFile())
                {
                    logger.debug(String.format("find %s for index=%s, master=%b", path, recordIndex, indexSearch.isMaster));
                    return path;
                }
                else
                {
                    logger.warn(String.format("%s is match for %s, but not exist", path, storeRole.getGrokPattern()));
                }
            }
            else
            {
                logger.debug(String.format("index=%s not match for %s", recordIndex, storeRole.getGrokPattern()));
            }
        }
        List<RecordStoreRole> roles = indexSearch.isMaster ? this.recordStoreCfg.getMaster().getStoreRoles() : this.recordStoreCfg.getBackup().getStoreRoles();
        Map<String, RecordStoreRole> roleMap = roles.stream().collect(Collectors.toMap(RecordStoreRole::getGrokPattern, Function.identity()));
        if(storeRole != null)
            roleMap.remove(storeRole.getGrokPattern());
        for(RecordStoreRole role : roleMap.values())
        {
            path = role.getExample().replace(role.getRecordIndex(), recordIndex);
            if(GrokParser.match(role.grokPattern, path) != null)
            {
                File file = new File(path);
                if(file.exists() && file.isFile())
                {
                    logger.debug(String.format("find %s for index=%s, master=%b", path, recordIndex, indexSearch.isMaster));
                    return path;
                }
                else
                {
                    logger.warn(String.format("%s is match for %s, but not exist", path, role.getGrokPattern()));
                }
            }
            else
            {
                logger.debug(String.format("index=%s not match for %s", recordIndex, role.getGrokPattern()));
            }
        }
        logger.error(String.format("can not find record file for index=%s", recordIndex));
        indexSearch.storeRole = null;
        return null;
    }

    /**
     * 确认某个企业是否应该检查录音或是备份录音
     * @param enterpriseId 企业id
     * @return 需要检查/备份则返回true否则false
     */
    protected boolean isEnterpriseChosen(String enterpriseId) {
        switch (enterpriseCfg.getChoseMethod())
        {
            case ALL:
                return true;
            case INCLUDE:
                return new HashSet<String>(enterpriseCfg.getList()).contains(enterpriseId);
            case EXCLUdE:
                return !(new HashSet<String>(enterpriseCfg.getList()).contains(enterpriseId));
        }
        return false;
    }

    private List<StoredRecordFileVo> scanMntDir(RecordStoreRole storeRole, Date chosenDate) throws IOException
    {
        DateFormat dateFormatCfg = storeRole.getDateFormat();
        String example = storeRole.getExample();
        String grokPattern = storeRole.getGrokPattern();
        String mntDir = storeRole.getMntDir();
        Map<String, Object> resultMap = GrokParser.match(grokPattern, storeRole.getExample());
        List<String> saveDirs = new ArrayList<>();
        if(resultMap.containsKey("date"))
        {
            String dateStr = resultMap.get("date").toString();
            SimpleDateFormat sf = new SimpleDateFormat(dateFormatCfg.getDate());
            searchForChosenTimeDir(mntDir, example, dateStr, sf.format(chosenDate), saveDirs);
        }
        else if(resultMap.containsKey("yearAndMonth"))
        {
            List<String> yearAndMonthDirList = new ArrayList<>();
            String yearAndMonth = resultMap.get("yearAndMonth").toString();
            SimpleDateFormat sf = new SimpleDateFormat(dateFormatCfg.getYearAndMonth());
            searchForChosenTimeDir(mntDir, example, yearAndMonth, sf.format(chosenDate), yearAndMonthDirList);
            if(resultMap.containsKey("monthAndDay"))
            {
                String monthAndDay = resultMap.get("monthAndDay").toString();
                sf = new SimpleDateFormat(dateFormatCfg.getMonthAndDay());
                for(String searchDir : yearAndMonthDirList)
                {
                    searchForChosenTimeDir(searchDir, example, monthAndDay, sf.format(chosenDate), saveDirs);
                }
            }
            else
            {
                String dayStr = resultMap.get("dayStr").toString();
                sf = new SimpleDateFormat(dateFormatCfg.getDay());
                for(String searchDir : yearAndMonthDirList)
                {
                    searchForChosenTimeDir(searchDir, example, dayStr, sf.format(chosenDate), saveDirs);
                }
            }
        }
        else
        {
            List<String> yearList = new ArrayList<>();
            String year = resultMap.get("year").toString();
            SimpleDateFormat sf = new SimpleDateFormat(dateFormatCfg.getYear());
            searchForChosenTimeDir(mntDir, example, year, sf.format(chosenDate), yearList);
            if(resultMap.containsKey("monthAndDay"))
            {
                String monthAndDay = resultMap.get("monthAndDay").toString();
                sf = new SimpleDateFormat(dateFormatCfg.getMonthAndDay());
                for(String searchDir : yearList)
                {
                    searchForChosenTimeDir(searchDir, example, monthAndDay, sf.format(chosenDate), saveDirs);
                }
            }
            else
            {
                String month = resultMap.get("dayStr").toString();
                sf = new SimpleDateFormat(dateFormatCfg.getMonth());
                List<String> monthDirList = new ArrayList<>();
                for(String searchDir : yearList)
                {
                    searchForChosenTimeDir(searchDir, example, month, sf.format(chosenDate), monthDirList);
                }
                String dayStr = resultMap.get("dayStr").toString();
                sf = new SimpleDateFormat(dateFormatCfg.getDay());
                for(String searchDir : monthDirList)
                {
                    searchForChosenTimeDir(searchDir, example, dayStr, sf.format(chosenDate), saveDirs);
                }
            }
        }
        int dept = example.split("/").length;
        List<String> allFileList = new ArrayList<>();
        for(String saveDir : saveDirs)
        {
            scan(saveDir, dept, false, ".*", allFileList);
        }
        int indexLen = storeRole.getRecordIndex().split("/").length;
        boolean escape = example.matches("^/") ? true : false;
        List<StoredRecordFileVo> retList = new ArrayList<>();
        for(String filePath : allFileList)
        {
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
                StoredRecordFileVo fileVo = new StoredRecordFileVo(enterpriseId, chosenDate, filePath, index, grokPattern);
                retList.add(fileVo);
            }
        }
        return retList;
    }

    private void searchForChosenTimeDir(String searchPath, String srcPath, String srcTime, String dstTime, List<String> resultList) throws IOException
    {
        int dept = srcPath.split(String.format("/%s/", srcTime))[0].split("/").length + 1;
        String regex = String.format("^%s$", dstTime);
        scan(searchPath, dept, true, regex, resultList);
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

    protected EntRecordCheckResultSumVo checkEntRecord(EnterpriseVo enterpriseVo, Date checkTime, Date beginTime, Date endTime, List<RecordDetailVo> entRecordList) {

        List<RecordDetailVo> successList = new ArrayList<>();
        List<RecordDetailVo> notIndexList = new ArrayList<>();
        List<RecordDetailVo> notFileList = new ArrayList<>();
        List<RecordDetailVo> notBakIndexList = new ArrayList<>();
        List<RecordDetailVo> notBakFileList = new ArrayList<>();
        RecordStoreRole storeRole = null;
        RecordStoreRole bkStoreRole = null;
        EntRecordCheckResultSumVo resultVo = null;
        for(RecordDetailVo detailVo : entRecordList)
        {
            //检查录音索引
            if(StringUtils.isBlank(detailVo.getRecordIndex()))
            {
                notIndexList.add(detailVo);
                continue;
            }
            //检查录音文件
            RecordIndexSearch indexSearch = new RecordIndexSearch();
            indexSearch.storeRole = storeRole;
            indexSearch.recordIndex = detailVo.getRecordIndex();
            indexSearch.isMaster = true;
            String fileSavePath = searchRecordIndexAbsolutePath(indexSearch);
            if(StringUtils.isBlank(fileSavePath))
            {
                notFileList.add(detailVo);
                continue;
            }
            storeRole = indexSearch.storeRole;
            if(isCheckBak(enterpriseVo.getEnterpriseId()))
            {
                if(StringUtils.isBlank(detailVo.getBakRecordIndex()))
                {
                    notBakIndexList.add(detailVo);
                    continue;
                }
                RecordIndexSearch bakIndexSearch = new RecordIndexSearch();
                bakIndexSearch.recordIndex = detailVo.getBakRecordIndex();
                bakIndexSearch.storeRole = bkStoreRole;
                bakIndexSearch.isMaster = false;
                String bkFileSavePath = searchRecordIndexAbsolutePath(bakIndexSearch);
                if(StringUtils.isBlank(bkFileSavePath))
                {
                    notBakFileList.add(detailVo);
                    continue;
                }
                bkStoreRole = bakIndexSearch.storeRole;
            }
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
     * 获取某个目录下指定深度的且文件名或是最后一级目录满足正则表达式的所有文件(目录)
     * @param pathName 被扫描的目录
     * @param dept 深度
     * @param isDir 目录还是文件
     * @param regex 用来匹配最后一级目录或是文件名的正则表达式
     * @param resultList 扫描结果
     */
    private void scan(String pathName, int dept, boolean isDir, String regex, List<String> resultList) throws IOException
    {
        File dirFile = new File(pathName);
        //判断该文件或目录是否存在，不存在时在控制台输出提醒
        if (!dirFile.exists()) {
            logger.error(String.format("%s not exist", pathName));
            return;
        }
        else if(!dirFile.isDirectory() && !dirFile.isFile())
            return;
        String[] arr = pathName.split("/");
        int currentDept = arr.length;
        if(currentDept > dept)
            return;
        else if(currentDept == dept)
        {
            if((dirFile.isFile() && !isDir) || (dirFile.isDirectory() && isDir))
            {
                if(Pattern.matches(regex, arr[currentDept - 1])) {
                    System.out.println(pathName);
                    resultList.add(pathName);
                }
            }
            return;
        }
        else if(dirFile.isDirectory())
        {
            String[] fileList = dirFile.list();
            for(int i = 0; i < fileList.length; i++)
            {
                scan(pathName + "/" + fileList[i], dept, isDir, regex, resultList);
            }
        }
    }

    protected List<FailBackupRecordFilePo> backupByCopyDirectory(List<StoredRecordFileVo> storedRecordFileList, String backupRootDirectory, boolean isVerify, Date recordDate) throws IOException, Exception
    {
        List<FailBackupRecordFilePo> failList = new ArrayList<>();
        Map<String, List<StoredRecordFileVo>> dirFileMap = storedRecordFileList.stream().collect(Collectors.groupingBy(StoredRecordFileVo::getStoreDir));
        final ExecutorService executor = Executors.newFixedThreadPool(threadPoolSize);
        List<FutureTask<List<FailBackupRecordFilePo>>> taskList = new ArrayList<>();
        for(String storeDir : dirFileMap.keySet())
        {
            FutureTask<List<FailBackupRecordFilePo>> task = new FutureTask<>(new Callable<List<FailBackupRecordFilePo>>() {
                @Override
                public List<FailBackupRecordFilePo> call() throws Exception {
                    List<StoredRecordFileVo> fileList =  dirFileMap.get(storeDir);
                    List<FailBackupRecordFilePo> failList;
                    try
                    {
                        failList = backupDirByCopy(storeDir, fileList, backupRootDirectory, isVerify, recordDate);
                    }
                    catch (Exception ex)
                    {
                        failList = new ArrayList<>();
                        logger.error(String.format("copy files from %s to %s%s exception", storeDir, storeDir, backupRootDirectory), ex);
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
                    String dstMd5 = DigestUtils.md5DigestAsHex(new FileInputStream(fileVo.getBackupSavePath()));
                    String srcMd5 = DigestUtils.md5DigestAsHex(new FileInputStream(fileVo.getFileSavePath()));
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

    protected boolean isCheckBak(String enterpriseId)
    {
        if(!this.hasBak)
            return false;
        if(this.enterpriseCfg.getNotCheckBakList() == null || this.enterpriseCfg.getNotCheckBakList().size() == 0)
            return true;
        Set<String> entSet = new HashSet<>(this.enterpriseCfg.getNotCheckBakList());
        if(entSet.contains(enterpriseId))
            return false;
        return true;
    }

    protected boolean addNewPlatformCheckResult(PlatformRecordCheckResultSumVo platformRecordCheckResultSumVo)
    {
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
        return true;
    }

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

    protected class RecordIndexSearch
    {
        public String recordIndex;

        public RecordStoreRole storeRole;

        public boolean isMaster;
    }
}
