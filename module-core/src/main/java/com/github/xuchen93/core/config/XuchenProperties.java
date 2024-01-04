package com.github.xuchen93.core.config;

import lombok.Data;
import org.springframework.boot.ansi.AnsiColor;
import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Data
@Component
@ConfigurationProperties(prefix = "xuchen.module")
public class XuchenProperties {
	private RequestModel request = new RequestModel();
	private RedisModel redis = new RedisModel();
	private JwtModel jwt = new JwtModel();
	private LogModel log = new LogModel();

	@PostConstruct
	public void init() {
		String banner = "\n" +
				"                 _                                          _       _      \n" +
				"__  ___   _  ___| |__   ___ _ __        _ __ ___   ___   __| |_   _| | ___ \n" +
				"\\ \\/ / | | |/ __| '_ \\ / _ \\ '_ \\ _____| '_ ` _ \\ / _ \\ / _` | | | | |/ _ \\\n" +
				" >  <| |_| | (__| | | |  __/ | | |_____| | | | | | (_) | (_| | |_| | |  __/\n" +
				"/_/\\_\\\\__,_|\\___|_| |_|\\___|_| |_|     |_| |_| |_|\\___/ \\__,_|\\__,_|_|\\___|\n" +
				"                                                                           \n";
		System.out.println(AnsiOutput.toString(AnsiColor.BRIGHT_MAGENTA, banner));
		System.out.print(AnsiOutput.toString(AnsiColor.BLUE, "project version:\t\t"));
		System.out.println(AnsiOutput.toString(AnsiColor.BRIGHT_GREEN, XuchenModuleVersion.getVersion()));
	}

	@Data
	public static class RequestModel {
		/**
		 * 校验token信息
		 */
		private boolean checkToken = false;
		/**
		 * 请求日志
		 */
		private boolean log = false;
		/**
		 * 请求参数校验，需依赖spring-boot-starter-validation
		 */
		private boolean valid = false;
		/**
		 * 请求详情
		 */
		private boolean detail = false;
		/**
		 * redis限流
		 */
		private boolean limit = false;
	}

	@Data
	public static class RedisModel {
		/**
		 * key值的前缀
		 */
		private String prefix;
	}

	@Data
	public static class JwtModel {
		private String tokenKey = "Authorization";

		private String secret = "defaultSecret";
		/**
		 * token过期时长
		 */
		private int expiresMin = 120;
	}

	/**
	 * 引用方式：logback-spring.xml
	 * <?xml version="1.0" encoding="UTF-8"?>
	 * <!-- 每隔五分钟重新扫描日志配置 -->
	 * <configuration debug="false" scan="false" scanPeriod="300 seconds">
	 * <include resource="xuchen/logback/logback-spring.xml" />
	 * </configuration>
	 */
	@Data
	public static class LogModel {

		/**
		 * 日志级别
		 */
		private String logLevel = "INFO";
		/**
		 * 是否控制台输入日志
		 */
		private boolean enableStdout = true;
		/**
		 * 是否同步输出日志文件
		 */
		private boolean enableFile = false;
		/**
		 * 是否异步输出日志文件
		 */
		private boolean enableAsyncFile = false;
		/**
		 * 日志文件输出路径
		 */
		private String filePath = "./logs";
		/**
		 * 日志文件输出文件名
		 */
		private String appName = "defaultName";
		/**
		 * 日志文件输出频率
		 * ture以天输出
		 * false以小时输出
		 */
		private boolean fileIsDayType = true;
		/**
		 * 最大保留时长
		 */
		private int fileMaxHistory = 60;
	}
}
