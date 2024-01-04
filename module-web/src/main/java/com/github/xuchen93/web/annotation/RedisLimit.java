package com.github.xuchen93.web.annotation;

import com.github.xuchen93.web.enums.LimitType;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface RedisLimit {

	/**
	 * key
	 */
	String key() default "";

	/**
	 * 时间值
	 */
	int period();

	/**
	 * 时间范围
	 */
	TimeUnit timeunit();

	/**
	 * 一定时间内最多访问次数
	 */
	int count();

	/**
	 * 限流的依据
	 */
	LimitType limitType() default LimitType.IP;
}
