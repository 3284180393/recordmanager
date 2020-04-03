package com.channelsoft.ccod.recordmanager.constant;

/**
 * @ClassName: BackupMethod
 * @Author: lanhb
 * @Description: 用来定义录音备份方式的枚举
 * @Date: 2020/4/3 10:18
 * @Version: 1.0
 */
public enum BackupMethod {

    COPY(1, "COPY", "直接磁盘拷贝"),

    FTP(2, "FTP", "通过ftp备份"),;

    public int id;

    public String name;

    public String desc;

    private BackupMethod(int id, String name, String desc)
    {
        this.id = id;
        this.name = name;
        this.desc = desc;
    }

    public static BackupMethod getEnum(String name)
    {
        if(name == null)
            return null;
        for (BackupMethod type : BackupMethod.values())
        {
            if (type.name.equals(name))
            {
                return type;
            }
        }
        return null;
    }
}
