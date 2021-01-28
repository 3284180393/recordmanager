package com.channelsoft.ccod.recordmanager.config;

/**
 * @ClassName: DBTableCfg
 * @Author: lanhb
 * @Description: 用来定义数据库相关表名配置
 * @Date: 2021/1/27 20:40
 * @Version: 1.0
 */
public class DBTableCfg {

    public String detail;  //r_detail表名

    public String enterprise;  //企业表名

    public String mix;  //混音索引表名

    public String combination; //并线索引表名

    public String bak; //被索引表名

    public String dbAgentRelate; //数据库座席关系表名

    public String dbEntRelate;  //数据库企业关系表名

    public String ucdsAgent; //ucds座席表名

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getEnterprise() {
        return enterprise;
    }

    public void setEnterprise(String enterprise) {
        this.enterprise = enterprise;
    }

    public String getMix() {
        return mix;
    }

    public void setMix(String mix) {
        this.mix = mix;
    }

    public String getCombination() {
        return combination;
    }

    public void setCombination(String combination) {
        this.combination = combination;
    }

    public String getBak() {
        return bak;
    }

    public void setBak(String bak) {
        this.bak = bak;
    }

    public String getDbAgentRelate() {
        return dbAgentRelate;
    }

    public void setDbAgentRelate(String dbAgentRelate) {
        this.dbAgentRelate = dbAgentRelate;
    }

    public String getDbEntRelate() {
        return dbEntRelate;
    }

    public void setDbEntRelate(String dbEntRelate) {
        this.dbEntRelate = dbEntRelate;
    }

    public String getUcdsAgent() {
        return ucdsAgent;
    }

    public void setUcdsAgent(String ucdsAgent) {
        this.ucdsAgent = ucdsAgent;
    }
}
