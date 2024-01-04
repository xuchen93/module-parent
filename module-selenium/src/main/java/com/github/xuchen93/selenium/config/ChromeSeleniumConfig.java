package com.github.xuchen93.selenium.config;

import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.List;


/**
 * @author wangxuchen
 */
@Data
@Component
@ConfigurationProperties(prefix = "xuchen.selenium.chrome.driver")
public class ChromeSeleniumConfig {
	/**
	 * 驱动位置
	 */
	private String path;
	/**
	 * 启动参数
	 */
	private List<String> options;
}
