package com.channelsoft.ccod.recordmanager.monitor.service.impl;

import com.channelsoft.ccod.recordmanager.backup.vo.PlatformRecordBackupResultVo;
import com.channelsoft.ccod.recordmanager.config.EnterpriseCfg;
import com.channelsoft.ccod.recordmanager.config.RecordStoreCfg;
import com.channelsoft.ccod.recordmanager.config.RecordStoreRole;
import com.channelsoft.ccod.recordmanager.monitor.service.IPlatformRecordService;
import com.channelsoft.ccod.recordmanager.monitor.vo.EntRecordCheckResultVo;
import com.channelsoft.ccod.recordmanager.monitor.vo.EnterpriseVo;
import com.channelsoft.ccod.recordmanager.monitor.vo.GlsAgentVo;
import com.channelsoft.ccod.recordmanager.monitor.vo.RecordDetailVo;
import com.channelsoft.ccod.recordmanager.utils.GrokParser;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.util.*;
import java.util.function.Function;
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

    @Value("${ccod.platformId}")
    protected String platformId;

    @Value("${ccod.platformName}")
    protected String platformName;

    @Value("${ccod.hasBak}")
    protected boolean hasBak;

    @Override
    public PlatformRecordBackupResultVo backup(Date backupDate) {
        return null;
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

    protected class RecordIndexSearch
    {
        public String recordIndex;

        public RecordStoreRole storeRole;
    }
}
