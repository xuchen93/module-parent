package com.github.xuchen93.selenium.service;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.github.xuchen93.selenium.config.ChromeSeleniumConfig;
import com.github.xuchen93.selenium.config.CommonSeleniumConfig;
import com.github.xuchen93.selenium.service.base.BaseSeleniumService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.naming.ConfigurationException;

/**
 * @author wangxuchen
 */
@Slf4j
public class SeleniumChromeServiceImpl extends BaseSeleniumService {

	private static ChromeDriverService driverService;
	private static ChromeOptions options;
	private static ChromeSeleniumConfig chromeSeleniumConfig;

	public SeleniumChromeServiceImpl(ChromeSeleniumConfig chromeSeleniumConfig) {
		this.chromeSeleniumConfig = chromeSeleniumConfig;
	}

	@SneakyThrows
	@PostConstruct
	public void init() {
		log.info("【xuchen-module-selenium】初始化【selenium-chrome】配置:{}", JSONUtil.toJsonStr(chromeSeleniumConfig));
		options = new ChromeOptions();
		if (StrUtil.isBlank(chromeSeleniumConfig.getPath())){
			throw new ConfigurationException("【xuchen-module-selenium】chrome驱动缺少path配置");
		}
		System.setProperty("webdriver.chrome.driver", chromeSeleniumConfig.getPath());
		options.addArguments(chromeSeleniumConfig.getOptions());
		driverService = new ChromeDriverService.Builder().usingAnyFreePort().withSilent(true).build();
		log.info("【xuchen-module-selenium】启动【driverService】");
		driverService.start();
	}

	@PreDestroy
	public void destory() {
		log.info("【xuchen-module-selenium】关闭【driverService】");
		driverService.stop();
	}

	/**
	 * 用完需要调用close方法
	 *
	 * @return
	 */
	@Override
	public RemoteWebDriver getDriver() {
		ChromeDriver chromeDriver = new ChromeDriver(driverService, options);
		RemoteDriverProxy proxy = new RemoteDriverProxy(chromeDriver);
		return proxy.getProxyInstance(driverService, options);
	}

	/**
	 * 用完需要调用close方法
	 *
	 * @return
	 */
	@Override
	public RemoteWebDriver getDriver(CommonSeleniumConfig privateSeleniumConfig) {
		ChromeDriver chromeDriver = new ChromeDriver(driverService, options);
		RemoteDriverProxy proxy = new RemoteDriverProxy(chromeDriver, privateSeleniumConfig);
		return proxy.getProxyInstance(driverService, options);
	}
}
