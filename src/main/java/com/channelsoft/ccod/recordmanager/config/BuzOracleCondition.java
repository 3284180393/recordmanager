package com.channelsoft.ccod.recordmanager.config;

import com.channelsoft.ccod.recordmanager.constant.DBType;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;


/**
 * @ClassName: BuzOracleCondition
 * @Author: lanhb
 * @Description: 用来判断业务库的类型是否是oracle
 * @Date: 2020/4/10 11:44
 * @Version: 1.0
 */
public class BuzOracleCondition implements Condition {

    @Override
    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
        Environment environment = conditionContext.getEnvironment();
        if (!environment.containsProperty("db.business.type"))
            return false;
        else if (DBType.ORACLE.name.equals(environment.getProperty("db.business.type")))
            return true;
        return false;
    }
}
