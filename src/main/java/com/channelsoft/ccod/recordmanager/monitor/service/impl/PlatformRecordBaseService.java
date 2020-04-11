package com.channelsoft.ccod.recordmanager.monitor.service.impl;

import com.channelsoft.ccod.recordmanager.backup.vo.PlatformRecordBackupResultVo;
import com.channelsoft.ccod.recordmanager.backup.vo.StoredRecordFileVo;
import com.channelsoft.ccod.recordmanager.config.*;
import com.channelsoft.ccod.recordmanager.constant.BackupMethod;
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

    protected int threadPoolSize = 4;

    @Override
    public PlatformRecordBackupResultVo backup(Date backupDate) throws Exception{
        Date startTime = new Date();
        List<StoredRecordFileVo> fileList = new ArrayList<>();
        for(RecordStoreRole storeRole : this.recordStoreCfg.getStoreRoles())
        {
            List<StoredRecordFileVo> scanList = scanMntDir(storeRole, backupDate);
            logger.debug(String.format("scan mntDir=%s with grokPattern=%s find %d record files",
                    storeRole.getMntDir(), storeRole.getGrokPattern(), scanList.size()));
            fileList.addAll(scanList);
        }
        backupByCopyDirectory(fileList, this.backupRootPath, this.verify);
        List<StoredRecordFileVo> backupList = new ArrayList<>();
        List<StoredRecordFileVo> failList = new ArrayList<>();
        for(StoredRecordFileVo fileVo : fileList)
        {
            if(fileVo.isResult())
                backupList.add(fileVo);
            else
                failList.add(fileVo);
        }
        PlatformRecordBackupResultVo resultVo;
        if(!this.compareWithDB)
        {
            resultVo = new PlatformRecordBackupResultVo(this.platformId, this.platformName, backupDate, startTime, backupList, failList);
        }
        else
        {
            PlatformRecordCheckResultVo checkResultVo;
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
            }
            catch (Exception ex)
            {
                checkResultVo = PlatformRecordCheckResultVo.fail(this.platformId, this.platformName, ex.getMessage());
                notifyService.notify(checkResultVo);
            }
            List<RecordDetailVo> notBackList = new ArrayList<>();
            Map<String, List<StoredRecordFileVo>> entRecordFileMap = backupList.stream().collect(Collectors.groupingBy(StoredRecordFileVo::getEnterpriseId));
            for(EntRecordCheckResultVo entRecordCheckResultVo : checkResultVo.getEntRecordCheckResultList())
            {
                if(!entRecordFileMap.containsKey(entRecordCheckResultVo.getEnterpriseId()))
                {
                    logger.warn(String.format("%s %d record not been backup",
                            entRecordCheckResultVo.getEnterpriseId(), entRecordCheckResultVo.getSuccessList().size()));
                    notBackList.addAll(entRecordCheckResultVo.getSuccessList());
                    continue;
                }
                Map<String, StoredRecordFileVo> fileMap = entRecordFileMap.get(entRecordCheckResultVo.getEnterpriseId())
                        .stream().collect(Collectors.toMap(StoredRecordFileVo::getRecordIndex, Function.identity()));
                for(RecordDetailVo detailVo : entRecordCheckResultVo.getSuccessList())
                {
                    if(!fileMap.containsKey(detailVo.getRecordIndex()))
                    {
                        logger.error(String.format("%s[%s] %s not been backup",
                                detailVo.getEnterpriseId(), detailVo.getSessionId(), detailVo.getRecordIndex()));
                        notBackList.add(detailVo);
                    }
                }
            }
            resultVo = new PlatformRecordBackupResultVo(this.platformId, this.platformName, backupDate, startTime, backupList, failList, notBackList);
        }
        return resultVo;
    }


    protected EntRecordCheckResultVo checkEntRecord(EnterpriseVo enterpriseVo, Date checkTime, Date beginTime, Date endTime, List<RecordDetailVo> entRecordList) {

        List<RecordDetailVo> successList = new ArrayList<>();
        List<RecordDetailVo> notIndexList = new ArrayList<>();
        List<RecordDetailVo> notFileList = new ArrayList<>();
        List<RecordDetailVo> notBakIndexList = new ArrayList<>();
        List<RecordDetailVo> notBakFileList = new ArrayList<>();
        RecordStoreRole storeRole = null;
        RecordStoreRole bkStoreRole = null;
        EntRecordCheckResultVo resultVo = null;
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
            String fileSavePath = searchRecordIndexAbsolutePath(indexSearch);
            if(StringUtils.isBlank(fileSavePath))
            {
                notFileList.add(detailVo);
                continue;
            }
            storeRole = indexSearch.storeRole;
            if(this.hasBak)
            {
                if(StringUtils.isBlank(detailVo.getBakRecordIndex()))
                {
                    notBakIndexList.add(detailVo);
                    continue;
                }
                else
                {
                    RecordIndexSearch bakIndexSearch = new RecordIndexSearch();
                    bakIndexSearch.recordIndex = detailVo.getBakRecordIndex();
                    bakIndexSearch.storeRole = bkStoreRole;
                    String bkFileSavePath = searchRecordIndexAbsolutePath(bakIndexSearch);
                    if(StringUtils.isNotBlank(bkFileSavePath))
                    {
                        notBakFileList.add(detailVo);
                        continue;
                    }
                    bkStoreRole = bakIndexSearch.storeRole;
                }
            }
            successList.add(detailVo);
        }
        if(hasBak)
        {
            resultVo = new EntRecordCheckResultVo(enterpriseVo, checkTime, beginTime, endTime, successList,
                    notIndexList, notFileList, notBakIndexList, notBakFileList);
        }
        else
        {
            resultVo = new EntRecordCheckResultVo(enterpriseVo, checkTime, endTime, endTime, successList,
                    notIndexList, notFileList);
        }
        return resultVo;
    }

    protected String searchRecordIndexAbsolutePath(RecordIndexSearch indexSearch)
    {
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
                    logger.debug(String.format("find %s for index=%s", path, recordIndex));
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
        Map<String, RecordStoreRole> roleMap = this.recordStoreCfg.getStoreRoles().stream().collect(Collectors.toMap(RecordStoreRole::getGrokPattern, Function.identity()));
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
                    logger.debug(String.format("find %s for index=%s", path, recordIndex));
                    return path;
                }
                else
                {
                    logger.warn(String.format("%s is match for %s, but not exist", path, role.getGrokPattern()));
                }
            }
            else
            {
                logger.debug(String.format("index=%s not match for %s", recordIndex, storeRole.getGrokPattern()));
            }
        }
        logger.error(String.format("can not find record file for index=%s", recordIndex));
        indexSearch.storeRole = null;
        return null;
    }

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

    protected List<EntRecordCheckResultVo> checkBigEntPlatformRecord(List<RecordDetailVo> recordList, Date checkTime, Date beginTime, Date endTime, List<GlsAgentVo> agentList)
    {
        Map<String, List<RecordDetailVo>> entRecordMap = recordList.stream().collect(Collectors.groupingBy(RecordDetailVo::getEnterpriseId));
        Map<String, List<GlsAgentVo>> entAgentMap = agentList.stream().collect(Collectors.groupingBy(GlsAgentVo::getEntId));
        List<com.channelsoft.ccod.recordmanager.monitor.vo.EntRecordCheckResultVo> entRecordCheckResultList = new ArrayList<>();
        for(String enterpriseId : entAgentMap.keySet())
        {
            EnterpriseVo enterpriseVo = new EnterpriseVo();
            enterpriseVo.setEnterpriseId(enterpriseId);
            enterpriseVo.setEnterpriseName(entAgentMap.get(enterpriseId).get(0).getEntName());
            List<RecordDetailVo> entRecordList = entRecordMap.containsKey(enterpriseId) ? entRecordMap.get(enterpriseId) : new ArrayList<>();
            com.channelsoft.ccod.recordmanager.monitor.vo.EntRecordCheckResultVo entRecordCheckResultVo = checkEntRecord(enterpriseVo, checkTime, beginTime, endTime, entRecordList);
            entRecordCheckResultList.add(entRecordCheckResultVo);
        }
        return entRecordCheckResultList;
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

    public void backupByCopyDirectory(List<StoredRecordFileVo> storedRecordFileList, String backupRootDirectory, boolean isVerify) throws IOException, Exception
    {

        Map<String, List<StoredRecordFileVo>> dirFileMap = storedRecordFileList.stream().collect(Collectors.groupingBy(StoredRecordFileVo::getStoreDir));
        final ExecutorService executor = Executors.newFixedThreadPool(threadPoolSize);
        List<FutureTask<List<StoredRecordFileVo>>> taskList = new ArrayList<>();
        for(String storeDir : dirFileMap.keySet())
        {
            FutureTask<List<StoredRecordFileVo>> task = new FutureTask<>(new Callable<List<StoredRecordFileVo>>() {
                @Override
                public List<StoredRecordFileVo> call() throws Exception {
                    List<StoredRecordFileVo> list = backupDirByCopy(storeDir, dirFileMap.get(storeDir), backupRootDirectory, isVerify);
                    return list;
                }
            });
            taskList.add(task);
            executor.execute(task);
        }
        executor.shutdown();
        for(FutureTask<List<StoredRecordFileVo>> task : taskList)
        {
            task.get();
        }
    }

    private List<StoredRecordFileVo> backupDirByCopy(String storeDir, List<StoredRecordFileVo> recordFileList, String backupRootDirectory, boolean isVerify) throws IOException
    {
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
        }
        for(StoredRecordFileVo fileVo : recordFileList)
        {
            if(isVerify)
            {
                File dstFile = new File(fileVo.getBackupSavePath());
                if(!dstFile.exists())
                {
                    logger.error(String.format("backup file verify fail : %s not exist", fileVo.getBackupSavePath()));
                    fileVo.setVerifyResult(false);
                    fileVo.setVerifyComment(String.format("%s not exist", fileVo.getBackupSavePath()));
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
        return recordFileList;
    }

    protected class RecordIndexSearch
    {
        public String recordIndex;

        public RecordStoreRole storeRole;
    }
}
