package com.channelsoft.ccod.recordmanager.monitor.vo;

/**
 * @ClassName: GlsEntVo
 * @Author: lanhb
 * @Description: 用来定义gls企业的类
 * @Date: 2020/4/4 19:29
 * @Version: 1.0
 */
public class GlsEntVo {

    private String enterpriseName;  //企业名

    private String enterpriseId; //企业id

    public String getEnterpriseName() {
        return enterpriseName;
    }

    public void setEnterpriseName(String enterpriseName) {
        this.enterpriseName = enterpriseName;
    }

    public String getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(String enterpriseId) {
        this.enterpriseId = enterpriseId;
    }
}
