package com.channelsoft.ccod.recordmanager.config;

import com.channelsoft.ccod.recordmanager.constant.CCODPlatformType;
import com.channelsoft.ccod.recordmanager.constant.DBType;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * @ClassName: NormalPlatformCondition
 * @Author: lanhb
 * @Description: 用来定义普通平台环境
 * @Date: 2020/4/3 21:53
 * @Version: 1.0
 */
@Conditional(Buz1OralceCondition.class)
public class NormalPlatformCondition implements Condition {

    @Override
    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
        Environment environment = conditionContext.getEnvironment();
        if(!environment.containsProperty("ccod.platformType") || !environment.containsProperty("db.business.count") || !environment.containsProperty("db.business.type"))
            return false;
        else if(CCODPlatformType.NORMAL.name.equals(environment.getProperty("ccod.platformType")) && "1".equals(environment.getProperty("db.business.count")) && DBType.ORACLE.name.equals(environment.getProperty("db.business.type")))
            return true;
        return false;
    }
}
