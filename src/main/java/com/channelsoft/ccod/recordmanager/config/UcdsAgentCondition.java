package com.channelsoft.ccod.recordmanager.config;

import com.channelsoft.ccod.recordmanager.constant.CCODPlatformType;
import com.channelsoft.ccod.recordmanager.constant.GlsType;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * @ClassName: UcdsAgentCondition
 * @Author: lanhb
 * @Description: 用来定义是否是需要通过ucds查询座席
 * @Date: 2021/1/29 18:15
 * @Version: 1.0
 */
public class UcdsAgentCondition  implements Condition {

    @Override
    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
        Environment environment = conditionContext.getEnvironment();
        if(!environment.containsProperty("ccod.platformType"))
            return false;
        else if(CCODPlatformType.EX_BIG_ENT.name.equals(environment.getProperty("ccod.platformType")))
            return true;
        return false;
    }
}
