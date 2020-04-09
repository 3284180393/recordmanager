package com.channelsoft.ccod.recordmanager.config;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * @ClassName: Buz2DBCondition
 * @Author: lanhb
 * @Description: 判断有2个业务库
 * @Date: 2020/4/9 15:43
 * @Version: 1.0
 */
public class Buz2DBCondition implements Condition {

    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
        Environment environment = conditionContext.getEnvironment();
        if (!environment.containsProperty("ccod.buzDBCount"))
            return false;
        else if ("2".equals(environment.getProperty("ccod.buzDBCount")))
            return true;
        return false;
    }
}
