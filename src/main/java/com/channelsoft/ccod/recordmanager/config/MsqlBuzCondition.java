package com.channelsoft.ccod.recordmanager.config;

import com.channelsoft.ccod.recordmanager.constant.DBType;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.context.annotation.Condition;


/**
 * @program: recordmanager
 * @ClassName: MsqlBuzCondition
 * @author: lanhb
 * @description: 用来定义加载mysql业务条件
 * @create: 2021-03-10 11:02
 **/
public class MsqlBuzCondition implements Condition {

    @Override
    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
        Environment environment = conditionContext.getEnvironment();
        if(!environment.containsProperty("db.business.type"))
            return false;
        else if(DBType.MYSQL.name.equals(environment.getProperty("db.business.type")))
            return true;
        return false;
    }
}
