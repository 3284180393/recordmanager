package com.channelsoft.ccod.recordmanager.config;

import com.channelsoft.ccod.recordmanager.constant.GlsType;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * @ClassName: GLSCondition
 * @Author: lanhb
 * @Description: 用来定义gls库作为企业座席加载配置库条件
 * @Date: 2020/4/10 10:39
 * @Version: 1.0
 */
public class GLSCondition implements Condition {

    @Override
    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
        Environment environment = conditionContext.getEnvironment();
        if(!environment.containsProperty("db.gls.type"))
            return false;
        else if(GlsType.GLS.name.equals(environment.getProperty("db.gls.type")))
            return true;
        return false;
    }
}
