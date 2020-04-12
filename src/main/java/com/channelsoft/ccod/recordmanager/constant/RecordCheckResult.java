package com.channelsoft.ccod.recordmanager.constant;

/**
 * @ClassName: RecordCheckResult
 * @Author: lanhb
 * @Description: 用来定义录音检查结果的枚举类
 * @Date: 2020/4/12 18:19
 * @Version: 1.0
 */
public enum RecordCheckResult {

    UNKNOWN(0, "UNKNOWN", "未知"),

    SUCCESS(1, "SUCCESS", "录音索引以及文件检查成功"),

    INDEX_NOT_EXIST(2, "INDEX_NOT_EXIST", "录音索引不存在"),

    FILE_NOT_EXIST(3, "FILE_NOT_EXIST", "录音文件不存在"),

    BAK_INDEX_NOT_EXIST(4, "BAK_INDEX_NOT_EXIST", "备份录音索引不存在"),

    BAK_FILE_NOT_EXIST(5, "BAK_FILE_NOT_EXIST", "备份录音文件不存在"),;

    public int id;

    public String name;

    public String desc;

    private RecordCheckResult(int id, String name, String desc)
    {
        this.id = id;
        this.name = name;
        this.desc = desc;
    }

    public static RecordCheckResult getEnum(String name)
    {
        if(name == null)
            return null;
        for (RecordCheckResult type : RecordCheckResult.values())
        {
            if (type.name.equals(name))
            {
                return type;
            }
        }
        return null;
    }
}
