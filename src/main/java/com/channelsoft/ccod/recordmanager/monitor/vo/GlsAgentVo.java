package com.channelsoft.ccod.recordmanager.monitor.vo;

/**
 * @ClassName: GlsAgentVo
 * @Author: lanhb
 * @Description: 用来定义gls库中的座席信息
 * @Date: 2020/4/8 11:38
 * @Version: 1.0
 */
public class GlsAgentVo {

    private String agentId; //座席工号

    private String dbName; //座席呼叫所在db

    private String schemaName; //座席呼叫所在的schema

    private String entId; //座席归属的企业id

    private String entName; //座席归属的企业名称

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    public String getEntId() {
        return entId;
    }

    public void setEntId(String entId) {
        this.entId = entId;
    }

    public String getEntName() {
        return entName;
    }

    public void setEntName(String entName) {
        this.entName = entName;
    }

    public EnterpriseVo getEnterprise()
    {
        EnterpriseVo enterpriseVo = new EnterpriseVo();
        enterpriseVo.setEnterpriseId(this.entId);
        enterpriseVo.setEnterpriseName(this.entName);
        return enterpriseVo;
    }
}
