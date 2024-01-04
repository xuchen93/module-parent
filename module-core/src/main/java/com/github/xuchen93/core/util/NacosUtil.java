package com.github.xuchen93.core.util;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.config.ConfigService;
import com.github.xuchen93.model.ex.BusiException;
import lombok.SneakyThrows;

import java.util.Properties;

/**
 * 使用完需要close
 * @author xuchen.wang
 * @date 2023/3/7
 */
public class NacosUtil {

	private static ConfigService configService;

	@SneakyThrows
	public static void initConfig(String serverAddr) {
		Properties properties = new Properties();
		properties.put(PropertyKeyConst.SERVER_ADDR, serverAddr);
		configService = NacosFactory.createConfigService(properties);
	}

	@SneakyThrows
	public static void push(String dataId, String content) {
		push(dataId, "DEFAULT_GROUP", content);
	}

	@SneakyThrows
	public static void push(String dataId, String group, String content) {
		checkConfig();
		configService.publishConfig(dataId, group, content);
	}

	@SneakyThrows
	public static String config(String dataId) {
		return config(dataId, "DEFAULT_GROUP");
	}

	@SneakyThrows
	public static String config(String dataId, String group) {
		checkConfig();
		return configService.getConfig(dataId, group, 5000);
	}

	@SneakyThrows
	public static void closeConfig() {
		checkConfig();
		configService.shutDown();
	}

	private static void checkConfig() {
		if (configService == null) {
			throw new BusiException("需要先初始化nacos config service");
		}
	}
}
