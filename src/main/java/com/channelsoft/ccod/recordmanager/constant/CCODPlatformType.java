package com.channelsoft.ccod.recordmanager.constant;

/**
 * @ClassName: CCODPlatformType
 * @Author: lanhb
 * @Description: 用来定义CCOD平台类型的枚举类
 * @Date: 2020/4/6 18:31
 * @Version: 1.0
 */
public enum CCODPlatformType {

    NORMAL(1, "NORMAL", "普通平台"),

    BIG_ENT(2, "BIG_ENT", "大域平台"),

    BIG_ENT_WITH2BIZ(3, "BIG_ENT_WITH2BIZ", "拥有两个业务库大域平台"),

    CLOUD(4, "CLOUD", "云平台"),

    EX_BIG_ENT(5, "EX_BIG_ENT", "该类大域平台只有一个业务库，但座席不能从gls库的GLS_DB_AGENT_RELATE表获取只能从ucds中获取"),;

    public int id;

    public String name;

    public String desc;

    private CCODPlatformType(int id, String name, String desc)
    {
        this.id = id;
        this.name = name;
        this.desc = desc;
    }

    public static CCODPlatformType getEnum(String name)
    {
        if(name == null)
            return null;
        for (CCODPlatformType type : CCODPlatformType.values())
        {
            if (type.name.equals(name))
            {
                return type;
            }
        }
        return null;
    }

}
