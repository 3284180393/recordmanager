package com.channelsoft.ccod.recordmanager.config;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
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
        if(!environment.containsProperty("ccod.platformType"))
            return false;
        else if("bigEnt2DB".equals(environment.getProperty("ccod.platformType")))
            return true;
        return false;
    }
}
