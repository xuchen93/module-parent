package com.github.xuchen93.selenium.service.base;

import cn.hutool.core.thread.ThreadUtil;
import com.github.xuchen93.selenium.config.CommonSeleniumConfig;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.service.DriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * 基础的selenium实现
 */
@Slf4j
public abstract class BaseSeleniumService {

	protected static CommonSeleniumConfig commonSeleniumConfig;

	@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
	@Autowired
	public void setCommonSeleniumConfig(CommonSeleniumConfig commonSeleniumConfig) {
		this.commonSeleniumConfig = commonSeleniumConfig;
	}


	public abstract RemoteWebDriver getDriver();

	public abstract RemoteWebDriver getDriver(CommonSeleniumConfig privateSeleniumConfig);

	protected class RemoteDriverProxy implements MethodInterceptor {
		private RemoteWebDriver target;
		private CommonSeleniumConfig privateSeleniumConfig;

		public RemoteDriverProxy(ChromeDriver target) {
			this.target = target;
		}

		public RemoteDriverProxy(ChromeDriver target, CommonSeleniumConfig privateSeleniumConfig) {
			this.target = target;
			this.privateSeleniumConfig = privateSeleniumConfig;
		}

		/**
		 * 给目标对象创建一个代理对象
		 */
		public RemoteWebDriver getProxyInstance(DriverService driverService, Capabilities capabilities) {
			//工具类
			Enhancer en = new Enhancer();
			//设置父类
			en.setSuperclass(target.getClass());
			//设置回调函数
			en.setCallback(this);
			//创建子类代理对象
			return (RemoteWebDriver) en.create(new Class[]{driverService.getClass(), capabilities.getClass()}, new Object[]{driverService, capabilities});
		}

		@Override
		public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
			CommonSeleniumConfig config = privateSeleniumConfig == null ? commonSeleniumConfig : privateSeleniumConfig;
			int retryCount = config.getRetryCount();
			long sleepMillis = config.getSleepMillis();
			if (config.getProxyMethod().contains(method.getName())) {
				NoSuchElementException tempException = null;
				if (log.isInfoEnabled()) {
					log.info("重复执行:{}", method.getName());
				}
				for (int i = 0; i <= retryCount; i++) {
					if (log.isInfoEnabled()) {
						log.info("第{}次尝试获取", i + 1);
					}
					try {
						return methodProxy.invokeSuper(o, objects);
					} catch (NoSuchElementException noSuchElementException) {
						tempException = noSuchElementException;
						ThreadUtil.sleep(sleepMillis);
					} catch (Exception e) {
						throw e;
					}
				}
				throw tempException;
			}
			return methodProxy.invokeSuper(o, objects);
		}
	}
}
