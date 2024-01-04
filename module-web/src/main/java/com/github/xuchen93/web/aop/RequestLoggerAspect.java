package com.github.xuchen93.web.aop;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Aspect
@Slf4j
@ConditionalOnExpression("${xuchen.module.request.log:false}")
@Component
@Order(100)
public class RequestLoggerAspect {

	public RequestLoggerAspect() {
		log.info("【xuchen-module-web】注入【请求日志】拦截");
	}

	private static String getJsonParams(Map<String, String[]> map) {
		Map<String, List<String>> paramsMap = new HashMap<>();
		for (Map.Entry<String, String[]> entry : map.entrySet()) {
			paramsMap.put(entry.getKey(), CollUtil.newArrayList(entry.getValue()));
		}
		return JSONUtil.toJsonStr(paramsMap);
	}

	@Pointcut("execution(* *..controller..*.*(..))")
	public void controllerPointCut() {

	}

	@Around("controllerPointCut()")
	public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
		ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		HttpServletRequest request = attributes.getRequest();
		if ("get".equalsIgnoreCase(request.getMethod())) {
			log.info("[Get]请求[{}]入参:[{}]", request.getRequestURI(), getJsonParams(request.getParameterMap()));
		} else {
			ServletInputStream inputStream = request.getInputStream();
			if (inputStream.isFinished()){//兼容requestBody只能读一次
				Object[] args = joinPoint.getArgs();
				log.info("[{}]请求[{}]入参:[{}]",request.getMethod(), request.getRequestURI(), JSONUtil.toJsonStr(args.length == 1?args[0]:Arrays.asList(args)));
			} else {
				String requestBody = new String(IoUtil.readBytes(inputStream));
				log.info("[{}]请求[{}]入参:[{}]",request.getMethod(), request.getRequestURI(), requestBody);
			}
		}
		long millis = System.currentTimeMillis();
		Object result = joinPoint.proceed();
		log.info("耗时：[{}ms]", System.currentTimeMillis() - millis);
		log.info("返回：[{}]", JSONUtil.toJsonStr(result));
		return result;
	}
}

