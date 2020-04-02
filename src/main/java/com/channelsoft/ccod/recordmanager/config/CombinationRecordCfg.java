package com.channelsoft.ccod.recordmanager.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName: CombinationRecordCfg
 * @Author: lanhb
 * @Description: 用来定义并线录音配置
 * @Date: 2020/4/1 21:19
 * @Version: 1.0
 */
@Configuration
@ConfigurationProperties(prefix = "record.combination")
public class CombinationRecordCfg {
}
