package com.channelsoft.ccod.recordmanager.config;

import com.channelsoft.ccod.recordmanager.constant.CCODPlatformType;
import com.channelsoft.ccod.recordmanager.constant.DBType;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * @program: recordmanager
 * @ClassName: CloudNewPlatformCondition
 * @author: lanhb
 * @description: 用来定义加载CLOUD_NEW类型平台服务类的条件
 * @create: 2021-03-10 14:22
 **/
public class CloudNewPlatformCondition implements Condition {

    @Override
    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
        Environment environment = conditionContext.getEnvironment();
        if(!environment.containsProperty("ccod.platformType"))
            return false;
        else if(CCODPlatformType.CLOUD_NEW.name.equals(environment.getProperty("ccod.platformType")))
            return true;
        return false;
    }
}
