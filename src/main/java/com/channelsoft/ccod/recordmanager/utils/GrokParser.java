package com.channelsoft.ccod.recordmanager.utils;

import com.alibaba.fastjson.JSONObject;
import io.krakens.grok.api.Grok;
import io.krakens.grok.api.GrokCompiler;
import io.krakens.grok.api.Match;
import io.krakens.grok.api.exception.GrokException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @ClassName: GrokParser
 * @Author: lanhb
 * @Description: 用来定义grok解析相关的工具类
 * @Date: 2020/4/2 9:44
 * @Version: 1.0
 */
public class GrokParser {

    private final static Logger logger = LoggerFactory.getLogger(GrokParser.class);

    /**
     * 用指定的grok pattern匹配一段文本
     * @param grokPattern 用来匹配的gork pattern
     * @param text 需要被匹配的文本
     * @return 如果能够匹配,用map返回解析的k,v键值对，否则返回空
     */
    public static Map<String, Object> match(String grokPattern, String text)
    {
        try
        {
            GrokCompiler grokCompiler = GrokCompiler.newInstance();
            // 进行注册, registerDefaultPatterns()方法注册的是Grok内置的patterns
            grokCompiler.registerDefaultPatterns();
            Grok grok = grokCompiler.compile(grokPattern);
            Match match = grok.match(text);
            Map<String, Object> resultMap = match.capture();
            if(resultMap == null || resultMap.size() == 0)
            {
                logger.debug(String.format("%s not match pattern %s", text, grokPattern));
                return null;
            }
            else
            {
                logger.debug(String.format("%s match pattern %s : %s", text, grokPattern, JSONObject.toJSONString(resultMap)));
                return resultMap;
            }
        }
        catch (GrokException e)
        {
            logger.error(String.format("match %s with pattern %s exception", text, grokPattern), e);
            return null;
        }
    }
}
