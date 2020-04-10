package com.channelsoft.ccod.recordmanager.constant;

/**
 * @ClassName: GlsType
 * @Author: lanhb
 * @Description: 定义企业和座席配置库的类型枚举
 * @Date: 2020/4/10 10:05
 * @Version: 1.0
 */
public enum  GlsType {

    GLS(1, "GLS", "采用GLS库作为企业座席配置鼓励"),

    UCDS(2, "UCDS", "采用UCDS库作为企业座席配置鼓励"),;

    public int id;

    public String name;

    public String desc;

    private GlsType(int id, String name, String desc)
    {
        this.id = id;
        this.name = name;
        this.desc = desc;
    }

    public static GlsType getEnum(String name)
    {
        if(name == null)
            return null;
        for (GlsType type : GlsType.values())
        {
            if (type.name.equals(name))
            {
                return type;
            }
        }
        return null;
    }

}
