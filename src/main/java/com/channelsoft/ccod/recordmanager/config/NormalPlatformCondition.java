package com.channelsoft.ccod.recordmanager.config;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * @ClassName: NormalPlatformCondition
 * @Author: lanhb
 * @Description: 用来定义普通平台环境
 * @Date: 2020/4/3 21:53
 * @Version: 1.0
 */
public class NormalPlatformCondition implements Condition {

    @Override
    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
        Environment environment = conditionContext.getEnvironment();
        if(!environment.containsProperty("ccod.platformType"))
            return false;
        else if("normal".equals(environment.getProperty("ccod.platformType")))
            return true;
        return false;
    }
}
