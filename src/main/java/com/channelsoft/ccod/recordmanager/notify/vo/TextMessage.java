package com.channelsoft.ccod.recordmanager.notify.vo;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: TextMessage
 * @Author: lanhb
 * @Description: 封装消息实体类
 * @Date: 2020/4/6 11:32
 * @Version: 1.0
 */
public class TextMessage {

    private String text;

    private List<String> atMobiles;

    private boolean isAtAll;

    public TextMessage(String text, boolean isAtAll, List<String> atMobiles) {
        this.text = text;
        this.isAtAll = isAtAll;
        this.atMobiles = atMobiles;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<String> getAtMobiles() {
        return atMobiles;
    }

    public void setAtMobiles(List<String> atMobiles) {
        this.atMobiles = atMobiles;
    }

    public boolean isAtAll() {
        return isAtAll;
    }

    public void setIsAtAll(boolean isAtAll) {
        this.isAtAll = isAtAll;
    }

    public String toJsonString() {
        Map<String, Object> items = new HashMap<>();
        items.put("msgtype", "text");

        Map<String, String> textContent = new HashMap<>();
        if (StringUtils.isBlank(text)) {
            throw new IllegalArgumentException("text should not be blank");
        }
        textContent.put("content", text);
        items.put("text", textContent);

        Map<String, Object> atItems = new HashMap<>();
        if (atMobiles != null && !atMobiles.isEmpty()) {
            atItems.put("atMobiles", atMobiles);
        }
        if (isAtAll) {
            atItems.put("isAtAll", isAtAll);
        }
        items.put("at", atItems);

        return JSON.toJSONString(items);
    }
}