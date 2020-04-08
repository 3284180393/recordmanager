package com.channelsoft.ccod.recordmanager.utils;

import com.channelsoft.ccod.recordmanager.config.EnterpriseCfg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;

/**
 * @ClassName: EnterpriseRecordTool
 * @Author: lanhb
 * @Description: 封装一些和企业录音相关的功能函数
 * @Date: 2020/4/8 18:37
 * @Version: 1.0
 */
public class EnterpriseRecordTool {

    private final static Logger logger = LoggerFactory.getLogger(EnterpriseRecordTool.class);

    @Autowired
    static EnterpriseCfg enterpriseCfg;

    public static boolean isChosen(String enterpriseId)
    {
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
}
