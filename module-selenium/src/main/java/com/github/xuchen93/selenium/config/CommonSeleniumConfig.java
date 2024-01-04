package com.github.xuchen93.selenium.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * @author wangxuchen
 */
@Data
@Component
@ConfigurationProperties(prefix = "xuchen.selenium.common")
public class CommonSeleniumConfig {
	/**
	 * 获取元素方法失败重试次数
	 */
	private int retryCount = 5;
	/**
	 * 等待重试时间
	 */
	private long sleepMillis = 500;
	/**
	 * 需要重试的方法
	 */
	private Set<String> proxyMethod = new HashSet<>();
}
