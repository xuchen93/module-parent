package com.github.xuchen93.web.common;

import cn.hutool.extra.spring.SpringUtil;
import org.springframework.web.context.request.RequestContextHolder;

public class RequestContextProxy {

	private static final ThreadLocal<RequestContextBean> THREAD_LOCAL = new ThreadLocal<>();

	public static void setThreadLocal(RequestContextBean requestContextBean) {
		THREAD_LOCAL.set(requestContextBean);
	}

	public static void removeThreadLocal() {
		THREAD_LOCAL.remove();
	}

	/**
	 * 如果没有请求则从线程中取，如果没有线程则新建
	 *
	 * @return
	 */
	public static RequestContextBean getRequestContextBean() {
		if (RequestContextHolder.getRequestAttributes() == null) {
			RequestContextBean requestContextBean = THREAD_LOCAL.get();
			if (requestContextBean != null) {
				return requestContextBean;
			}
			return new RequestContextBean();
		}
		return SpringUtil.getBean(RequestContextBean.class);
	}

	public static String getUserId() {
		return getRequestContextBean().getUserId();
	}

	public static String getUserName() {
		return getRequestContextBean().getUserName();
	}

	public static String getNickName() {
		return getRequestContextBean().getNickName();
	}
}
