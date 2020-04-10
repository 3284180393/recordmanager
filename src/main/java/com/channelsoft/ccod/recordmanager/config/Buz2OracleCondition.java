package com.channelsoft.ccod.recordmanager.config;

import com.channelsoft.ccod.recordmanager.constant.DBType;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * @ClassName: Buz2OracleCondition
 * @Author: lanhb
 * @Description: 用来判断是否有两个oracle业务库
 * @Date: 2020/4/10 11:51
 * @Version: 1.0
 */
public class Buz2OracleCondition implements Condition {

    @Override
    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
        Environment environment = conditionContext.getEnvironment();
        if(!environment.containsProperty("db.business.count") || !environment.containsProperty("db.business.type"))
            return false;
        else if("2".equals(environment.getProperty("db.business.count")) && DBType.ORACLE.name.equals(environment.getProperty("db.business.type")))
            return true;
        return false;
    }
}
