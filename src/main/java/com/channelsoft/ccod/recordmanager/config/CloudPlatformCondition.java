package com.channelsoft.ccod.recordmanager.config;

import com.channelsoft.ccod.recordmanager.constant.CCODPlatformType;
import com.channelsoft.ccod.recordmanager.constant.DBType;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * @ClassName: CloudPlatformCondition
 * @Author: lanhb
 * @Description: 用来定义云平台环境
 * @Date: 2020/4/3 21:55
 * @Version: 1.0
 */
public class CloudPlatformCondition implements Condition {

    @Override
    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
        Environment environment = conditionContext.getEnvironment();
        if(!environment.containsProperty("ccod.platformType") || !environment.containsProperty("db.business.count") || !environment.containsProperty("db.business.type"))
            return false;
        else if(CCODPlatformType.CLOUD.name.equals(environment.getProperty("ccod.platformType")) && "1".equals(environment.getProperty("db.business.count")) && DBType.MONGO.name.equals(environment.getProperty("db.business.type")))
            return true;
        return false;
    }
}
