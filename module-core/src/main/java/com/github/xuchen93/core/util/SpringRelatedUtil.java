package com.github.xuchen93.core.util;

import cn.hutool.core.util.ReflectUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.config.ConfigFileApplicationListener;
import org.springframework.boot.context.properties.ConfigurationPropertiesBindingPostProcessor;
import org.springframework.boot.env.OriginTrackedMapPropertySource;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.DefaultResourceLoader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author xuchen.wang
 * @date 2023/12/19
 */
@Slf4j
public class SpringRelatedUtil {
    private SpringRelatedUtil() {
    }

    /**
     * 获取springboot的配置
     */
    public static List<OriginTrackedMapPropertySource> getConfigProperties() {
        DefaultResourceLoader resourceLoader = new DefaultResourceLoader();
        ConfigFileApplicationListener listener = new ConfigFileApplicationListener();
        StandardEnvironment environment = new StandardEnvironment();
        ReflectUtil.invoke(listener, "addPropertySources", environment, resourceLoader);
        return environment.getPropertySources().stream()
                .filter(i -> i instanceof OriginTrackedMapPropertySource)
                .map(i -> (OriginTrackedMapPropertySource) i)
                .collect(Collectors.toList());
    }

    /**
     * 获取springboot的配置
     */
    public static Map<String, Object> getConfigMap() {
        List<OriginTrackedMapPropertySource> list = getConfigProperties();
        Map<String, Object> map = new HashMap<>();
        list.forEach(i -> map.putAll(i.getSource()));
        return map;
    }

    /**
     * 获取springboot的配置，注入到bean中，返回bean
     */
    public static <T> T getConfigBean(Class<T> beanClass) throws Exception {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext("XUCHEN");
        applicationContext.registerBean("org.springframework.boot.context.internalConfigurationPropertiesBinder", Class.forName("org.springframework.boot.context.properties.ConfigurationPropertiesBinder"));
        MutablePropertySources mutablePropertySources = applicationContext.getEnvironment().getPropertySources();
        List<OriginTrackedMapPropertySource> propertySources = getConfigProperties();
        propertySources.stream().forEach(p -> mutablePropertySources.addLast(p));
        ConfigurationPropertiesBindingPostProcessor propertiesBindingPostProcessor = new ConfigurationPropertiesBindingPostProcessor();
        propertiesBindingPostProcessor.setApplicationContext(applicationContext);
        propertiesBindingPostProcessor.afterPropertiesSet();
        return (T) propertiesBindingPostProcessor.postProcessBeforeInitialization(ReflectUtil.newInstance(beanClass), "commonSeleniumConfig");
    }
}
