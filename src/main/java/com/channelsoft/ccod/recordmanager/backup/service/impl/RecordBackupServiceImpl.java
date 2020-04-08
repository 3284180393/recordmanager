package com.channelsoft.ccod.recordmanager.backup.service.impl;

import com.channelsoft.ccod.recordmanager.backup.service.IRecordBackupService;
import com.channelsoft.ccod.recordmanager.backup.vo.StoredRecordFileVo;
import com.channelsoft.ccod.recordmanager.config.*;
import com.channelsoft.ccod.recordmanager.constant.BackupMethod;
import com.channelsoft.ccod.recordmanager.monitor.dao.IEnterpriseDao;
import com.channelsoft.ccod.recordmanager.monitor.vo.EnterpriseVo;
import com.channelsoft.ccod.recordmanager.utils.GrokParser;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @ClassName: RecordBackupServiceImpl
 * @Author: lanhb
 * @Description: IRecordBackupService的实现类
 * @Date: 2020/4/1 11:43
 * @Version: 1.0
 */
@Service
public class RecordBackupServiceImpl implements IRecordBackupService {

    private boolean isWin = true;

    @Autowired
    MixRecordCfg mixRecordCfg;

    @Autowired
    RecordStoreCfg recordStoreCfg;

    @Autowired
    IEnterpriseDao enterpriseDao;

    private DateFormatCfg dateFormatCfg = new DateFormatCfg();

    private int threadPoolSize = 2;

    private final static Logger logger = LoggerFactory.getLogger(RecordBackupServiceImpl.class);



    @PostConstruct
    public void init() throws Exception
    {
        System.out.println("hello world");
        List<EnterpriseVo> entList = this.enterpriseDao.select();
        System.out.println(String.format("platform has %d enterprises", entList.size()));
    }

    @Override
    public List<StoredRecordFileVo> scanMntDir(RecordStoreRole storeRole, Date chosenDate, List<String> excludeEntIds) throws IOException
    {
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
        Set<String> entIdSet = new HashSet(excludeEntIds);
        for(String filePath : allFileList)
        {
            Map<String, Object> parseResultMap = GrokParser.match(grokPattern, filePath);
            if(parseResultMap != null)
            {
                String enterpriseId = parseResultMap.get("entId").toString();
                if(entIdSet.contains(enterpriseId))
                {
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

    @Override
    public List<StoredRecordFileVo> scanStoreRootDirectory(String storeRootDirectory, Date date) throws Exception {

//        if(recordIndexCfg.getGrokPatterns() == null || recordIndexCfg.getGrokPatterns().size() == 0)
//        {
//            logger.error("scan record files fail : not define grok pattern for record index");
//            throw new CFGException("scan record files fail : not define grok pattern for record index");
//        }
//        File rootDir = new File(storeRootDirectory);
//        if(!rootDir.exists())
//        {
//            logger.error(String.format("scan record files fail, %s not exist", storeRootDirectory));
//            throw new ParamException(String.format("scan record files fail, %s not exist", storeRootDirectory));
//        }
//        else if(!rootDir.isDirectory())
//        {
//            logger.error(String.format("scan record files fail, %s is not directory", storeRootDirectory));
//            throw new ParamException(String.format("scan record files fail, %s is not directory", storeRootDirectory));
//        }
//        List<StoredRecordFileVo> list = new ArrayList<>();
        return null;
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

//    public void backupByCopyDirectory(List<StoredRecordFileVo> storedRecordFileList, String backupRootDirectory, boolean isVerify) throws IOException
//    {
//        Map<String, List<StoredRecordFileVo>> dirFileMap = storedRecordFileList.stream().collect(Collectors.groupingBy(StoredRecordFileVo::getStoreDir));
//        for(String storeDir : dirFileMap.keySet())
//        {
//            String backupDir = String.format("%s%s", backupRootDirectory, storeDir);
//            if(this.isWin)
//                backupDir = String.format("%s%s", backupRootDirectory, storeDir.replaceAll("^[A-Z]:", ""));
//            logger.debug(String.format("begin to copy all file from %s to %s", storeDir, backupDir));
//            File srcDir = new File(storeDir);
//            File dstDir = new File(backupDir);
//            if(!dstDir.exists())
//                dstDir.mkdirs();
//            FileUtils.copyDirectory(srcDir, dstDir);
//            for(StoredRecordFileVo fileVo : dirFileMap.get(storeDir))
//            {
//                fileVo.setBackupMethod(BackupMethod.COPY);
//                fileVo.setBackupSavePath(String.format("%s/%s", backupDir, fileVo.getFileName()));
//            }
//            if(isVerify)
//            {
//                for(StoredRecordFileVo fileVo : dirFileMap.get(storeDir))
//                {
//                    File dstFile = new File(fileVo.getBackupSavePath());
//                    if(!dstFile.exists())
//                    {
//                        logger.error(String.format("backup file verify fail : %s not exist", fileVo.getBackupSavePath()));
//                        fileVo.setVerifyResult(false);
//                        fileVo.setVerifyComment(String.format("%s not exist", fileVo.getBackupSavePath()));
//                    }
//                    else
//                    {
//                        String dstMd5 = DigestUtils.md5DigestAsHex(new FileInputStream(fileVo.getBackupSavePath()));
//                        String srcMd5 = DigestUtils.md5DigestAsHex(new FileInputStream(fileVo.getFileSavePath()));
//                        if(!dstMd5.equals(srcMd5))
//                        {
//                            logger.error(String.format("backup file verify fail : %s md5=%s but %s md5=%s ", fileVo.getFileSavePath(), srcMd5, fileVo.getBackupSavePath(), dstMd5));
//                            fileVo.setVerifyResult(false);
//                            fileVo.setVerifyComment(String.format("%s md5=%s but %s md5=%s ", fileVo.getFileSavePath(), srcMd5, fileVo.getBackupSavePath(), dstMd5));
//                        }
//                        else
//                        {
//                            logger.debug(String.format("backup file %s verify success", fileVo.getBackupSavePath()));
//                            fileVo.setVerifyResult(true);
//                            fileVo.setVerifyComment(String.format("backup file %s verify success", fileVo.getFileSavePath()));
//                        }
//                    }
//                }
//            }
//        }
//    }

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
        if(isVerify)
        {
            for(StoredRecordFileVo fileVo : recordFileList)
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
                        logger.debug(String.format("backup file %s verify success", fileVo.getBackupSavePath()));
                        fileVo.setVerifyResult(true);
                        fileVo.setVerifyComment(String.format("backup file %s verify success", fileVo.getFileSavePath()));
                    }
                }
            }
        }
        return recordFileList;
    }

//    @Test
//    public void grokMatchTest()
//    {
//        String pattern = "^/mnt/Data/record/(?<entId>\\d+)/Agent/(?<date>20\\d{2}(0[1-9]|1[0-2])(0[1-9]|[1-2][0-9]|3[0-1]))/(?<type>[A-Z]+)-(?<AgentDn>\\d+)_(?<agentId>\\d+)_(?<remoteUrl>\\d+).wav$";
//        String text = "/mnt/Data/record/5361410004/Agent/20200317/TEL-42070_5860179694_20200317131234.wav";
//        Map<String, Object> resultMap = GrokParser.match(pattern, text);
//        if(resultMap == null)
//        {
//            System.out.println("match fail");
//        }
//        else
//        {
//            System.out.println(JSONObject.toJSONString(resultMap));
//        }
//    }

    @Test
    public void scanTest()
    {
//        String pathName = "D:\\tmp".replaceAll("\\\\", "/");
//        int depth = 8;
//        boolean isDir = true;
//        String regex = "^CCODServices$";
//        List<String> resultList = new ArrayList<>();
//        try
//        {
//            scan(pathName, depth, isDir, regex, resultList);
//            System.out.println("%%%%%%%%" + resultList.size() + "%%%%%%%");
//        }
//        catch (Exception ex)
//        {
//            ex.printStackTrace();
//        }
        RecordStoreRole storeRole = new RecordStoreRole();
//        scanRole.setExample("D:/mnt/Data/record/0000057733/Agent/20200313/SIP-400503_5860042291_20200317113441.wav");
//        scanRole.setGrokPattern("^D:/mnt/Data/record/(?<entId>\\d+)/Agent/(?<date>20\\d{2}(0[1-9]|1[0-2])(0[1-9]|[1-2][0-9]|3[0-1]))/(?<type>[A-Z]+)-(?<AgentDn>\\d+)_(?<agentId>\\d+)_(?<remoteUrl>\\d+).wav$");
//        scanRole.setRecordIndex("0000057733/Agent/20200313/SIP-400503_5860042291_20200317113441.wav");
        storeRole.setExample("D:/mnt/Date1/0000050111/202003/60750230/0301/SIP-400603_5860042292_20200317123441.wav");
        storeRole.setGrokPattern("^D:/mnt/Date1/(?<entId>\\d+)/(?<yearAndMonth>20\\d{2}(0[1-9]|1[0-2]))/(?<AgentDn1>\\d+)/(?<monthAndDay>(0[1-9]|1[0-2])(0[1-9]|[1-2][0-9]|3[0-1]))/(?<type>[A-Z]+)-(?<localUrl>\\d+)_(?<agentId>\\d+)_(?<remoteUrl>\\d+).wav$");
        storeRole.setRecordIndex("0000050111/202003/60750230/0301/SIP-400603_5860042292_20200317123441.wav");
        storeRole.setMntDir("D:/mnt");
        DateFormat dateFormat = new DateFormat();
        dateFormat.setDate("yyyyMMdd");
        dateFormat.setDay("dd");
        dateFormat.setMonth("MM");
        dateFormat.setMonthAndDay("MMdd");
        dateFormat.setYear("yyyy");
        dateFormat.setYearAndMonth("yyyyMM");
        storeRole.setDateFormat(dateFormat);
        String chosenData = "20200303";
        String backupRootDir = "D:/mnt/backup";
        try
        {
            SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
            List<StoredRecordFileVo> list = scanMntDir(storeRole, sf.parse(chosenData), new ArrayList<>());
            System.out.println(list.size());
            backupByCopyDirectory(list, backupRootDir, true);
            for(StoredRecordFileVo fileVo : list)
            {
                System.out.println(String.format("%s verify result %b, comment=%s", fileVo.getFileSavePath(), fileVo.isVerifyResult(), fileVo.getVerifyComment()));
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
