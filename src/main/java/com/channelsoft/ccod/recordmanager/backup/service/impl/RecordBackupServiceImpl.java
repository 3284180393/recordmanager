package com.channelsoft.ccod.recordmanager.backup.service.impl;

import com.channelsoft.ccod.recordmanager.backup.service.IRecordBackupService;
import com.channelsoft.ccod.recordmanager.backup.vo.StoredRecordFileVo;
import com.channelsoft.ccod.recordmanager.config.DateFormatCfg;
import com.channelsoft.ccod.recordmanager.config.DiskScanRole;
import com.channelsoft.ccod.recordmanager.config.MixRecordCfg;
import com.channelsoft.ccod.recordmanager.exception.ParamException;
import com.channelsoft.ccod.recordmanager.utils.GrokParser;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @ClassName: RecordBackupServiceImpl
 * @Author: lanhb
 * @Description: IRecordBackupService的实现类
 * @Date: 2020/4/1 11:43
 * @Version: 1.0
 */
@Service
public class RecordBackupServiceImpl implements IRecordBackupService {

    @Autowired
    MixRecordCfg mixRecordCfg;

    private DateFormatCfg dateFormatCfg = new DateFormatCfg();

    private final static Logger logger = LoggerFactory.getLogger(RecordBackupServiceImpl.class);



    @PostConstruct
    public void init() throws Exception
    {
        System.out.println("hello world");
    }

    public List<StoredRecordFileVo> scanMntDir(DiskScanRole scanRole, Date chosenDate) throws IOException
    {
        Map<String, Object> resultMap = GrokParser.match(scanRole.getGrokPattern(), scanRole.getExample());
        List<String> saveDirs = new ArrayList<>();
        if(resultMap.containsKey("date"))
        {
            String dateStr = resultMap.get("date").toString();
            SimpleDateFormat sf = new SimpleDateFormat(dateFormatCfg.getDate());
            searchForChosenTimeDir(scanRole.getMntDir(), scanRole.getExample(), dateStr, sf.format(chosenDate), saveDirs);
        }
        else if(resultMap.containsKey("yearAndMonth"))
        {
            List<String> yearAndMonthDirList = new ArrayList<>();
            String yearAndMonth = resultMap.get("yearAndMonth").toString();
            SimpleDateFormat sf = new SimpleDateFormat(dateFormatCfg.getYearAndMonth());
            searchForChosenTimeDir(scanRole.getMntDir(), scanRole.getExample(), yearAndMonth, sf.format(chosenDate), yearAndMonthDirList);
            if(resultMap.containsKey("monthAndDay"))
            {
                String monthAndDay = resultMap.get("monthAndDay").toString();
                sf = new SimpleDateFormat(dateFormatCfg.getMonthAndDay());
                for(String searchDir : yearAndMonthDirList)
                {
                    searchForChosenTimeDir(searchDir, scanRole.getExample(), monthAndDay, sf.format(chosenDate), saveDirs);
                }
            }
            else
            {
                String dayStr = resultMap.get("dayStr").toString();
                sf = new SimpleDateFormat(dateFormatCfg.getDay());
                for(String searchDir : yearAndMonthDirList)
                {
                    searchForChosenTimeDir(searchDir, scanRole.getExample(), dayStr, sf.format(chosenDate), saveDirs);
                }
            }
        }
        else
        {
            List<String> yearList = new ArrayList<>();
            String year = resultMap.get("year").toString();
            SimpleDateFormat sf = new SimpleDateFormat(dateFormatCfg.getYear());
            searchForChosenTimeDir(scanRole.getMntDir(), scanRole.getExample(), year, sf.format(chosenDate), yearList);
            if(resultMap.containsKey("monthAndDay"))
            {
                String monthAndDay = resultMap.get("monthAndDay").toString();
                sf = new SimpleDateFormat(dateFormatCfg.getMonthAndDay());
                for(String searchDir : yearList)
                {
                    searchForChosenTimeDir(searchDir, scanRole.getExample(), monthAndDay, sf.format(chosenDate), saveDirs);
                }
            }
            else
            {
                String month = resultMap.get("dayStr").toString();
                sf = new SimpleDateFormat(dateFormatCfg.getMonth());
                List<String> monthDirList = new ArrayList<>();
                for(String searchDir : yearList)
                {
                    searchForChosenTimeDir(searchDir, scanRole.getExample(), month, sf.format(chosenDate), monthDirList);
                }
                String dayStr = resultMap.get("dayStr").toString();
                sf = new SimpleDateFormat(dateFormatCfg.getDay());
                for(String searchDir : monthDirList)
                {
                    searchForChosenTimeDir(searchDir, scanRole.getExample(), dayStr, sf.format(chosenDate), saveDirs);
                }
            }
        }
        int dept = scanRole.getExample().replaceAll("\\\\", "/").split("/").length;
        List<String> allFileList = new ArrayList<>();
        for(String saveDir : saveDirs)
        {
            scan(saveDir, dept, false, ".*", allFileList);
        }
        int indexLen = scanRole.getRecordIndex().replaceAll("\\\\", "/").split("/").length;
        boolean escape = scanRole.getRecordIndex().matches("^/") ? true : false;
        List<StoredRecordFileVo> retList = new ArrayList<>();
        for(String filePath : allFileList)
        {
            Map<String, Object> parseResultMap = GrokParser.match(scanRole.getGrokPattern(), filePath);
            if(parseResultMap != null)
            {
                String enterpriseId = parseResultMap.get("entId").toString();
                String[] arr = filePath.replaceAll("\\\\", "/").split("/");
                String index = "";
                for(int i = indexLen; i >=1; i--)
                {
                    index += "/" + arr[arr.length - i];
                }
                if(!escape)
                {
                    index = index.replaceAll("^/", "");
                }
                StoredRecordFileVo fileVo = new StoredRecordFileVo(enterpriseId, chosenDate, filePath, index);
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
        String[] arr = pathName.replaceAll("\\\\", "/").split("/");
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
                System.out.println(pathName + "/" + fileList[i]);
                scan(pathName + "/" + fileList[i], dept, isDir, regex, resultList);
            }
        }
    }

    private void find(String pathName, int depth) throws IOException {
        int filecount=0;
        //获取pathName的File对象
        File dirFile = new File(pathName);
        //判断该文件或目录是否存在，不存在时在控制台输出提醒
        if (!dirFile.exists()) {
            System.out.println("do not exit");
            return ;
        }
        //判断如果不是一个目录，就判断是不是一个文件，时文件则输出文件路径
        if (!dirFile.isDirectory()) {
            if (dirFile.isFile()) {
                System.out.println(dirFile.getCanonicalFile());
            }
            return ;
        }

        for (int j = 0; j < depth; j++) {
            System.out.print("  ");
        }
        System.out.print("|--");
        System.out.println(dirFile.getName());
        //获取此目录下的所有文件名与目录名
        String[] fileList = dirFile.list();
        int currentDepth=depth+1;
        for (int i = 0; i < fileList.length; i++) {
            //遍历文件目录
            String string = fileList[i];
            //File("documentName","fileName")是File的另一个构造器
            File file = new File(dirFile.getPath(),string);
            String name = file.getName();
            //如果是一个目录，搜索深度depth++，输出目录名后，进行递归
            if (file.isDirectory()) {
                //递归
                find(file.getCanonicalPath(),currentDepth);
            }else{
                //如果是文件，则直接输出文件名
                for (int j = 0; j < currentDepth; j++) {
                    System.out.print("   ");
                }
                System.out.print("|--");
                System.out.println(name);
            }
        }
    }

    private Map<String, Object> findFirstRecordFile()
    {
        return null;
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
        DiskScanRole scanRole = new DiskScanRole();
//        scanRole.setExample("D:/mnt/Data/record/0000057733/Agent/20200313/SIP-400503_5860042291_20200317113441.wav");
//        scanRole.setGrokPattern("^D:/mnt/Data/record/(?<entId>\\d+)/Agent/(?<date>20\\d{2}(0[1-9]|1[0-2])(0[1-9]|[1-2][0-9]|3[0-1]))/(?<type>[A-Z]+)-(?<AgentDn>\\d+)_(?<agentId>\\d+)_(?<remoteUrl>\\d+).wav$");
//        scanRole.setRecordIndex("0000057733/Agent/20200313/SIP-400503_5860042291_20200317113441.wav");
        scanRole.setExample("D:/mnt/Date1/0000050111/202003/60750230/0301/SIP-400603_5860042292_20200317123441.wav");
        scanRole.setGrokPattern("^D:/mnt/Date1/(?<entId>\\d+)/(?<yearAndMonth>20\\d{2}(0[1-9]|1[0-2]))/(?<AgentDn1>\\d+)/(?<monthAndDay>(0[1-9]|1[0-2])(0[1-9]|[1-2][0-9]|3[0-1]))/(?<type>[A-Z]+)-(?<localUrl>\\d+)_(?<agentId>\\d+)_(?<remoteUrl>\\d+).wav$");
        scanRole.setRecordIndex("0000050111/202003/60750230/0301/SIP-400603_5860042292_20200317123441.wav");
        scanRole.setMntDir("D:/mnt");

        String chosenData = "20200303";
        try
        {
            SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
            List<StoredRecordFileVo> list = scanMntDir(scanRole, sf.parse(chosenData));
            System.out.println(list.size());
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
