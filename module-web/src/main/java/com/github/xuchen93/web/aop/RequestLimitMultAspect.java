package com.github.xuchen93.web.aop;


import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.ServletUtil;
import com.github.xuchen93.core.config.XuchenProperties;
import com.github.xuchen93.model.ex.BusiException;
import com.github.xuchen93.web.annotation.RedisLimit;
import com.github.xuchen93.web.common.RequestContextProxy;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.concurrent.locks.Lock;

/**
 * 使用了redis分布式锁，适合分布式场景下使用
 */
@Aspect
@Slf4j
@Component
@ConditionalOnExpression("${xuchen.module.request.limit:false}")
@SuppressWarnings("ALL")
public class RequestLimitMultAspect {

	@Autowired
	RedisTemplate redisTemplate;
	@Autowired
	XuchenProperties xuchenProperties;
	@Autowired
	RedisLockRegistry redisLockRegistry;

	public RequestLimitMultAspect() {
		log.info("【xuchen-module-web】注入【限流】拦截");
	}

	@Pointcut("@annotation(com.github.xuchen93.web.annotation.RedisLimit)")
	public void limitPointCut() {

	}

	@Before("limitPointCut()")
	public void before(JoinPoint point) {
		Method method = ((MethodSignature) point.getSignature()).getMethod();
		RedisLimit redisLimit = method.getAnnotation(RedisLimit.class);
		String key = redisLimit.key();
		if (StrUtil.isBlank(key)) {
			key = method.getName();
		}

		switch (redisLimit.limitType()) {
			case USER_NAME:
				String userName = RequestContextProxy.getUserName();
				if (userName == null) {
					throw new BusiException(4000, "尚未登录");
				}
				key = userName + ":" + key;
				break;
			case IP:
				ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
				HttpServletRequest request = attributes.getRequest();
				key = ServletUtil.getClientIP(request) + ":" + key;
				break;
			default:
				break;
		}
		if (StrUtil.isNotBlank(xuchenProperties.getRedis().getPrefix())) {
			key = xuchenProperties.getRedis().getPrefix() + ":" + key;
		}
		Lock lock = redisLockRegistry.obtain(key + "-Lock");
		lock.lock();
		try {
			//采用令牌桶算法
			BoundListOperations ops = redisTemplate.boundListOps(key);
			if (ops.size() >= redisLimit.count()) {
				throw new BusiException(4100, "访问太频繁，过会再试试吧");
			}
			if (ops.size() == 0) {
				ops.rightPush(LocalDateTime.now().toString());
				ops.expire(redisLimit.period(), redisLimit.timeunit());
			} else {
				ops.rightPush(LocalDateTime.now().toString());
			}
		} finally {
			lock.unlock();
		}

	}
}

