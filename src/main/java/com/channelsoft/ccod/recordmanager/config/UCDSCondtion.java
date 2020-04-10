package com.channelsoft.ccod.recordmanager.config;

import com.channelsoft.ccod.recordmanager.constant.DBType;
import com.channelsoft.ccod.recordmanager.constant.GlsType;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * @ClassName: UCDSCondtion
 * @Author: lanhb
 * @Description: 用来定义用gls库作为企业座席配置库加载条件
 * @Date: 2020/4/10 10:36
 * @Version: 1.0
 */
public class UCDSCondtion implements Condition {

    @Override
    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
        Environment environment = conditionContext.getEnvironment();
        if(!environment.containsProperty("db.gls.type"))
            return false;
        else if(GlsType.UCDS.name.equals(environment.getProperty("db.gls.type")))
            return true;
        return false;
    }
}
