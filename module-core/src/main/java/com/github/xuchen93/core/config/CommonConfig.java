package com.github.xuchen93.core.config;

import cn.hutool.extra.spring.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@Configuration
public class CommonConfig {


	@Bean
	public ThreadPoolTaskExecutor taskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(8);
		executor.setMaxPoolSize(100);
		executor.setQueueCapacity(99999);
		executor.setKeepAliveSeconds(30);
		executor.setThreadNamePrefix("taskExecutor-");
		executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
		executor.setWaitForTasksToCompleteOnShutdown(true);
		executor.setAwaitTerminationSeconds(60);
		executor.initialize();
		log.info("【xuchen-module-core】注入【线程池】");
		return executor;
	}

	@Bean
	public SpringUtil springUtil() {
		log.info("【xuchen-module-core】注入【springUtil】");
		return new SpringUtil();
	}
}
