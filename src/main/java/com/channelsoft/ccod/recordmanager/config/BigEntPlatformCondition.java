package com.channelsoft.ccod.recordmanager.config;


import com.channelsoft.ccod.recordmanager.constant.CCODPlatformType;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * @ClassName: BigEntPlatformCondition
 * @Author: lanhb
 * @Description: 用来定义大域企业环境
 * @Date: 2020/4/3 21:53
 * @Version: 1.0
 */
public class BigEntPlatformCondition implements Condition {

    @Override
    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
        Environment environment = conditionContext.getEnvironment();
        if(!environment.containsProperty("ccod.platformType"))
            return false;
        else if(CCODPlatformType.BIG_ENT.name.equals(environment.getProperty("ccod.platformType")))
            return true;
        return false;
    }
}
