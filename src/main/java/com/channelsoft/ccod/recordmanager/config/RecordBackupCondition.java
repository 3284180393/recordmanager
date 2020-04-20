package com.channelsoft.ccod.recordmanager.config;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * @ClassName: RecordBackupCondition
 * @Author: lanhb
 * @Description: 用来定义执行录音备份任务的条件
 * @Date: 2020/4/19 14:24
 * @Version: 1.0
 */
public class RecordBackupCondition implements Condition {

    @Override
    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
        Environment environment = conditionContext.getEnvironment();
        if (!environment.containsProperty("jobs.backup.execute"))
            return false;
        else if ("true".equals(environment.getProperty("jobs.backup.execute")))
            return true;
        return false;
    }
}
