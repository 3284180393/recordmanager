package com.channelsoft.ccod.recordmanager.config;

import com.channelsoft.ccod.recordmanager.constant.CCODPlatformType;
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
        if(!environment.containsProperty("ccod.platformType") || !environment.containsProperty("ccod.buzDBCount"))
            return false;
        else if(CCODPlatformType.BIG_ENT.equals(environment.getProperty("ccod.platformType")) && "2".equals(environment.getProperty("ccod.buzDBCount")))
            return true;
        return false;
    }
}
