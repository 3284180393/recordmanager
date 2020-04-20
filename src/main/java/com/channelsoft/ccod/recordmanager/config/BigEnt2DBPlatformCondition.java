package com.channelsoft.ccod.recordmanager.config;

import com.channelsoft.ccod.recordmanager.constant.CCODPlatformType;
import com.channelsoft.ccod.recordmanager.constant.DBType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * @ClassName: BigEnt2DBPlatformCondition
 * @Author: lanhb
 * @Description: 用来定义有两个业务库的大域平台环境
 * @Date: 2020/4/3 23:24
 * @Version: 1.0
 */
public class BigEnt2DBPlatformCondition implements Condition {

    @Override
    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
        Environment environment = conditionContext.getEnvironment();
        if(!environment.containsProperty("ccod.platformType") || !environment.containsProperty("db.business.count")
                || !environment.containsProperty("db.business.type") || !environment.containsProperty("db.business.db1Name")
                || !environment.containsProperty("db.business.db2Name"))
            return false;
        else if(CCODPlatformType.BIG_ENT.name.equals(environment.getProperty("ccod.platformType"))
                && "2".equals(environment.getProperty("db.business.count"))
                && DBType.ORACLE.name.equals(environment.getProperty("db.business.type"))
                && StringUtils.isNotBlank(environment.getProperty("db.business.db1Name"))
                && StringUtils.isNotBlank(environment.getProperty("db.business.db2Name")))
            return true;
        return false;
    }
}
