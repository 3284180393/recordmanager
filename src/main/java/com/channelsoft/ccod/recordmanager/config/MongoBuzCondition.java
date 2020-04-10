package com.channelsoft.ccod.recordmanager.config;

import com.channelsoft.ccod.recordmanager.constant.DBType;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * @ClassName: MongoBuzCondition
 * @Author: lanhb
 * @Description: 用来定义加载mongo业务库条件
 * @Date: 2020/4/10 10:30
 * @Version: 1.0
 */
public class MongoBuzCondition implements Condition {

    @Override
    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
        Environment environment = conditionContext.getEnvironment();
        if(!environment.containsProperty("db.business.type"))
            return false;
        else if(DBType.MONGO.name.equals(environment.getProperty("db.business.type")))
            return true;
        return false;
    }
}
