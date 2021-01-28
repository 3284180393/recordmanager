package com.channelsoft.ccod.recordmanager.constant;

/**
 * @ClassName: EnterpriseChoseMethod
 * @Author: lanhb
 * @Description: 用来定义选择企业方式的枚举
 * @Date: 2020/4/8 1:20
 * @Version: 1.0
 */
public enum EnterpriseChoseMethod {

    ALL(1, "ALL", "检查/备份全部企业"),

    INCLUDE(2, "INCLUDE", "只检查/备份包含在列表里面的企业"),

    EXCLUDE(3, "EXCLUDE", "不检查/备份包含在列表里面的企业"),;

    public int id;

    public String name;

    public String desc;

    private EnterpriseChoseMethod(int id, String name, String desc)
    {
        this.id = id;
        this.name = name;
        this.desc = desc;
    }

    public static EnterpriseChoseMethod getEnum(String name)
    {
        if(name == null)
            return null;
        for (EnterpriseChoseMethod type : EnterpriseChoseMethod.values())
        {
            if (type.name.equals(name))
            {
                return type;
            }
        }
        return null;
    }
}
