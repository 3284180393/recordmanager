package com.channelsoft.ccod.recordmanager.constant;

/**
 * @ClassName: DBType
 * @Author: lanhb
 * @Description: 用来定义数据库类型的枚举
 * @Date: 2020/4/10 10:10
 * @Version: 1.0
 */
public enum DBType {

    ORACLE(1, "ORACLE", "oracle数据库"),

    MYSQL(2, "MYSQL", "mysql数据库"),

    MONGO(2, "MONGO", "mongo数据库"),;

    public int id;

    public String name;

    public String desc;

    private DBType(int id, String name, String desc)
    {
        this.id = id;
        this.name = name;
        this.desc = desc;
    }

    public static DBType getEnum(String name)
    {
        if(name == null)
            return null;
        for (DBType type : DBType.values())
        {
            if (type.name.equals(name))
            {
                return type;
            }
        }
        return null;
    }

}
