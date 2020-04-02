package com.channelsoft.ccod.recordmanager.exception;

/**
 * @ClassName: CFGException
 * @Author: lanhb
 * @Description: 用来定义配置文件配置错误的异常类
 * @Date: 2020/4/1 11:49
 * @Version: 1.0
 */
public class CFGException extends Exception {

    private static final long serialVersionUID = -1L;

    public CFGException(String msg)
    {
        super(msg);
    }
}
