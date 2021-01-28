package com.channelsoft.ccod.recordmanager.config;

import com.channelsoft.ccod.recordmanager.constant.CCODPlatformType;
import com.channelsoft.ccod.recordmanager.constant.DBType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * @ClassName: BigExEntPlatformCondition
 * @Author: lanhb
 * @Description: 用来定义国寿等非标准大域企业平台加载条件
 * @Date: 2021/1/28 15:01
 * @Version: 1.0
 */
public class BigExEntPlatformCondition implements Condition {

    @Override
    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
        Environment environment = conditionContext.getEnvironment();
        if(!environment.containsProperty("ccod.platformType") || !environment.containsProperty("db.business.count")
                || !environment.containsProperty("db.business.type") || !environment.containsProperty("spring.datasource.business.dbName"))
            return false;
        else if(CCODPlatformType.EX_BIG_ENT.name.equals(environment.getProperty("ccod.platformType"))
                && "1".equals(environment.getProperty("db.business.count"))
                && DBType.ORACLE.name.equals(environment.getProperty("db.business.type"))
                && StringUtils.isNotBlank(environment.getProperty("spring.datasource.business.dbName")))
            return true;
        return false;
    }
}
