package com.channelsoft.ccod.recordmanager.config;

import com.channelsoft.ccod.recordmanager.constant.DBType;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * @ClassName: RecordCheckCondition
 * @Author: lanhb
 * @Description: 用来定义是否执行录音检查定时任务
 * @Date: 2020/4/19 13:46
 * @Version: 1.0
 */
public class RecordCheckCondition implements Condition {

    @Override
    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
        Environment environment = conditionContext.getEnvironment();
        if (!environment.containsProperty("jobs.recordCheck.execute"))
            return false;
        else if ("true".equals(environment.getProperty("jobs.recordCheck.execute")))
            return true;
        return false;
    }
}
