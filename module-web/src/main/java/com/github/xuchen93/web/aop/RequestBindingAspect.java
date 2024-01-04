package com.github.xuchen93.web.aop;

import cn.hutool.core.collection.CollUtil;
import com.github.xuchen93.model.ex.BusiException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.ObjectError;

import java.util.ArrayList;

@Aspect
@Slf4j
@ConditionalOnExpression("${xuchen.module.request.valid:false}")
@ConditionalOnClass(name = "javax.validation.constraints.NotBlank")
@Component
@Order(200)
public class RequestBindingAspect {

	public RequestBindingAspect() {
		log.info("【xuchen-module-web】注入【valid校验】拦截");
	}

	@Pointcut("execution(* *..controller..*.*(..))")
	public void controllerPointCut() {

	}

	@Before("controllerPointCut()")
	public void before(JoinPoint point) {
		ArrayList<Object> objects = CollUtil.newArrayList(point.getArgs());
		BeanPropertyBindingResult bindingResult = (BeanPropertyBindingResult) objects.stream().filter(item -> item instanceof BeanPropertyBindingResult).findAny().orElse(null);
		if (bindingResult != null && bindingResult.getAllErrors().size() > 0) {
			ObjectError error = bindingResult.getAllErrors().get(0);
			throw new BusiException(error.getDefaultMessage());
		}
	}
}

