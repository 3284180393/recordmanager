package com.channelsoft.ccod.recordmanager.constant;

/**
 * @ClassName: RecordType
 * @Author: lanhb
 * @Description: 用来定义录音类型的枚举
 * @Date: 2020/4/1 20:29
 * @Version: 1.0
 */
public enum RecordType {

    MIX(1, "MIX", "混音"),

    COMBINATION(2, "COMBINATION", "并线"),;

    public int id;

    public String name;

    public String desc;

    private RecordType(int id, String name, String desc)
    {
        this.id = id;
        this.name = name;
        this.desc = desc;
    }

    public static RecordType getEnum(String name)
    {
        if(name == null)
            return null;
        for (RecordType type : RecordType.values())
        {
            if (type.name.equals(name))
            {
                return type;
            }
        }
        return null;
    }
}
