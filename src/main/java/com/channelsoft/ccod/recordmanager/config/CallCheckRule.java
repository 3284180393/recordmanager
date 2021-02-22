package com.channelsoft.ccod.recordmanager.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @ClassName: CallCheckRule
 * @Author: lanhb
 * @Description: 用来定义呼叫检查规则的配置
 * @Date: 2020/4/4 21:57
 * @Version: 1.0
 */
@Configuration
@ConfigurationProperties(prefix = "call")
public class CallCheckRule {

    private List<Integer> callTypes;    //需要检查的呼叫类型，如果为空则检查所有呼叫类型

    private List<Integer> endTypes;  //需要检查的呼叫结束类型，如果为空则检查所有呼叫结束类型

    private int minTalkDuration; //只检查通话时长大于等于此值的通话

    private List<String> notCheckeEnts; //该数组里面的企业将不被检查

    private List<String> notCheckBakEnts; //该数组里面的企业不会检查备份录音

    private boolean agentIdCanBeNull; //是否支持无工号坐席

    public List<Integer> getCallTypes() {
        return callTypes;
    }

    public void setCallTypes(List<Integer> callTypes) {
        this.callTypes = callTypes;
    }

    public List<Integer> getEndTypes() {
        return endTypes;
    }

    public void setEndTypes(List<Integer> endTypes) {
        this.endTypes = endTypes;
    }

    public int getMinTalkDuration() {
        return minTalkDuration;
    }

    public void setMinTalkDuration(int minTalkDuration) {
        this.minTalkDuration = minTalkDuration;
    }

    public List<String> getNotCheckeEnts() {
        return notCheckeEnts;
    }

    public void setNotCheckeEnts(List<String> notCheckeEnts) {
        this.notCheckeEnts = notCheckeEnts;
    }

    public List<String> getNotCheckBakEnts() {
        return notCheckBakEnts;
    }

    public void setNotCheckBakEnts(List<String> notCheckBakEnts) {
        this.notCheckBakEnts = notCheckBakEnts;
    }

    public boolean isAgentIdCanBeNull() {
        return agentIdCanBeNull;
    }

    public void setAgentIdCanBeNull(boolean agentIdCanBeNull) {
        this.agentIdCanBeNull = agentIdCanBeNull;
    }
}
