package com.channelsoft.ccod.recordmanager.backup.service.impl;

import com.channelsoft.ccod.recordmanager.backup.service.IRecordManagerService;
import com.channelsoft.ccod.recordmanager.config.CallCheckRule;
import com.channelsoft.ccod.recordmanager.config.RecordStoreCfg;
import com.channelsoft.ccod.recordmanager.config.RecordStoreRole;
import com.channelsoft.ccod.recordmanager.monitor.dao.IEnterpriseDao;
import com.channelsoft.ccod.recordmanager.monitor.dao.IRecordDetailDao;
import com.channelsoft.ccod.recordmanager.monitor.vo.*;
import com.channelsoft.ccod.recordmanager.utils.GrokParser;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @ClassName: RecordManagerServiceImpl
 * @Author: lanhb
 * @Description: IRecordManagerService接口实现类
 * @Date: 2020/4/5 13:52
 * @Version: 1.0
 */
@Service
public class RecordManagerServiceImpl implements IRecordManagerService {

    private final static Logger logger = LoggerFactory.getLogger(RecordManagerServiceImpl.class);

    @Autowired
    IEnterpriseDao enterpriseDao;

    @Autowired
    IRecordDetailDao recordDetailDao;

    @Autowired
    RecordStoreCfg recordStoreCfg;

    @Autowired
    CallCheckRule callCheckRule;

    @Value("${ccod.platformId}")
    private String platformId;

    @Value("${ccod.platformName}")
    private String platformName;

    @Value("${ccod.hasBak}")
    private boolean hasBak;

    @Override
    public PlatformRecordCheckResultVo check(Date beginTime, Date endTime) throws Exception {
        Date now = new Date();
        List<EnterpriseVo> entList = enterpriseDao.select();
        if(entList.size() == 0)
        {
            logger.error(String.format("can not find any enterprise for %s[%s]", platformName, platformId));
            throw new Exception(String.format("can not find any enterprise for %s[%s]", platformName, platformId));
        }
        Set<String> notCheckEntSet = new HashSet<>(callCheckRule.getNotCheckeEnts());
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
        List<EntRecordCheckResultVo> entRecordCheckResultList = new ArrayList<>();
        for(EnterpriseVo enterpriseVo : entList)
        {
            String enterpriseId = enterpriseVo.getEnterpriseId();
            String enterpriseName = enterpriseVo.getEnterpriseName();
            if(notCheckEntSet.contains(enterpriseId))
            {
                logger.debug(String.format("%s[%s] need not check", enterpriseId, enterpriseName));
                continue;
            }
            logger.debug(String.format("begin to check %s[%s] record from %s to %s",
                    enterpriseId, enterpriseName, sf.format(beginTime), sf.format(endTime)));
            List<RecordDetailVo> recordDetailList = this.recordDetailDao.select(enterpriseVo.getEnterpriseId(), beginTime, endTime);
            logger.debug(String.format("find %d record for %s[%s]", recordDetailList.size(), enterpriseId, enterpriseName));
        }
        PlatformRecordCheckResultVo resultVo = new PlatformRecordCheckResultVo(platformId, platformName, now, beginTime,
                endTime, entRecordCheckResultList);
        return resultVo;
    }

    private EntRecordCheckResultVo checkEntRecord(EnterpriseVo enterpriseVo, Date startTime, Date endTime)
    {
        Date checkTime = new Date();
        List<RecordDetailVo> recordDetailList = recordDetailDao.select(enterpriseVo.getEnterpriseId(), startTime, endTime);
        List<RecordDetailVo> successList = new ArrayList<>();
        List<RecordDetailVo> notIndexList = new ArrayList<>();
        List<RecordDetailVo> notFileList = new ArrayList<>();
        List<RecordDetailVo> notBakIndexList = new ArrayList<>();
        List<RecordDetailVo> notBakFileList = new ArrayList<>();
        RecordStoreRole storeRole = null;
        RecordStoreRole bkStoreRole = null;
        EntRecordCheckResultVo resultVo = null;
        for(RecordDetailVo detailVo : recordDetailList)
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
            resultVo = new EntRecordCheckResultVo(enterpriseVo, checkTime, startTime, endTime, successList,
                    notIndexList, notFileList, notBakIndexList, notBakFileList);
        }
        else
        {
            resultVo = new EntRecordCheckResultVo(enterpriseVo, checkTime, startTime, endTime, successList,
                    notIndexList, notFileList);
        }
        return resultVo;
    }

    private String searchRecordIndexAbsolutePath(RecordIndexSearch indexSearch)
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

    @Override
    public RecordBackupResultVo backup(Date chosenDate) throws Exception {
        return null;
    }

    class RecordIndexSearch
    {
        public String recordIndex;

        public RecordStoreRole storeRole;
    }
}
