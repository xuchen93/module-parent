package pers.xuchen.module.web.aop;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import pers.xuchen.module.core.model.ex.BusiException;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Aspect
@Slf4j
@ConditionalOnExpression("${xuchen.module.request.log:false}")
@Component
public class RequestLoggerAspect {

    @Pointcut("execution(* *..controller..*.*(..))")
    public void controllerPointCut() {

    }

    @Around("controllerPointCut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        ArrayList<Object> objects = CollUtil.newArrayList(joinPoint.getArgs());
        if ("get".equalsIgnoreCase(request.getMethod())) {
            log.info("[Get]请求[{}]入参:[{}]", request.getRequestURI(), getJsonParams(request.getParameterMap()));
        } else {
            log.info("[Post]请求[{}]入参:[{}]", request.getRequestURI(), JSONUtil.toJsonStr(objects));
        }
        long millis = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        log.info("耗时：[{}ms]", System.currentTimeMillis() - millis);
        log.info("返回：[{}]", JSONUtil.toJsonStr(result));
        return result;
    }


    private static String getJsonParams(Map<String, String[]> map) {
        Map<String, List<String>> paramsMap = new HashMap<>();
        for (Map.Entry<String, String[]> entry : map.entrySet()) {
            paramsMap.put(entry.getKey(), CollUtil.newArrayList(entry.getValue()));
        }
        return JSONUtil.toJsonStr(paramsMap);
    }
}

