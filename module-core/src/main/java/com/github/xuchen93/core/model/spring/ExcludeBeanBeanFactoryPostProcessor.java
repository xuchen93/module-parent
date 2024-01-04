package com.github.xuchen93.core.model.spring;

import cn.hutool.core.util.ClassUtil;
import cn.hutool.json.JSONUtil;
import com.github.xuchen93.core.util.ClassMethodUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.scheduling.config.TaskManagementConfigUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 排除spring中的bean
 *
 * @author xuchen.wang
 * @date 2024/1/3
 */
@Slf4j
public class ExcludeBeanBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

    private ExcludeBeanModel excludeBeanModel;

    public ExcludeBeanBeanFactoryPostProcessor(ExcludeBeanModel excludeBeanModel) {
        this.excludeBeanModel = excludeBeanModel;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        if (excludeBeanModel.isEmpty()) {
            return;
        }
        log.info("排除springBean配置：" + JSONUtil.toJsonStr(excludeBeanModel));
        DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) beanFactory;
        if (excludeBeanModel.isSchedule()) {
            excludeBeanModel.getBeanNameList().add(TaskManagementConfigUtils.SCHEDULED_ANNOTATION_PROCESSOR_BEAN_NAME);
        }
        if (excludeBeanModel.isAsync()) {
            excludeBeanModel.getBeanNameList().add(TaskManagementConfigUtils.ASYNC_ANNOTATION_PROCESSOR_BEAN_NAME);
        }
        Set<String> excludeBeanNameSet = new HashSet<>();
        defaultExclude(defaultListableBeanFactory, excludeBeanNameSet);
        customExclude(defaultListableBeanFactory, excludeBeanNameSet);
        excludeBeanNameSet = excludeBeanNameSet.stream()
                .filter(defaultListableBeanFactory::containsBean)
                .collect(Collectors.toSet());
        for (String excludeBeanName : excludeBeanNameSet) {
            defaultListableBeanFactory.removeBeanDefinition(excludeBeanName);
        }
        log.info("排除springBean结果：" + JSONUtil.toJsonStr(excludeBeanNameSet));
    }

    protected void customExclude(DefaultListableBeanFactory defaultListableBeanFactory, Set<String> excludeBeanNameSet) {

    }

    protected void defaultExclude(DefaultListableBeanFactory defaultListableBeanFactory, Set<String> excludeBeanNameSet) {
        String[] names = defaultListableBeanFactory.getBeanDefinitionNames();
        excludeBeanNameSet.addAll(excludeBeanModel.getBeanNameList());
        Set<String> annotationNameSet = excludeBeanModel.getAnnotationList().stream().map(i -> i.getName()).collect(Collectors.toSet());
        for (String name : names) {
            BeanDefinition definition = defaultListableBeanFactory.getBeanDefinition(name);
            String className = ClassMethodUtil.getOriginClassName(definition.getBeanClassName());
            Class<?> beanClass = definition.getResolvableType().resolve();
            if (className != null) {
                boolean packageMatch = excludeBeanModel.getPackageList().stream().anyMatch(i -> className.startsWith(i));
                if (packageMatch) {
                    excludeBeanNameSet.add(name);
                }
            }
            excludeAnnotation(excludeBeanNameSet, annotationNameSet, name, definition);
            excludeClass(excludeBeanNameSet, name, beanClass);
        }

    }

    protected void excludeClass(Set<String> excludeBeanNameSet, String name, Class<?> beanClass) {
        if (beanClass != null) {
            for (Class aClass : excludeBeanModel.getClassList()) {
                if (ClassUtil.isAssignable(aClass, beanClass)) {
                    excludeBeanNameSet.add(name);
                    return;
                }
            }
        }
    }

    protected void excludeAnnotation(Set<String> excludeBeanNameSet, Set<String> annotationNameSet, String name, BeanDefinition definition) {
        if (definition instanceof AnnotatedBeanDefinition) {
            AnnotatedBeanDefinition scannedGenericBeanDefinition = (AnnotatedBeanDefinition) definition;
            Set<String> annotationTypes = scannedGenericBeanDefinition.getMetadata().getAnnotationTypes();
            boolean annotationMatch = annotationNameSet.stream().anyMatch(i -> annotationTypes.contains(i));
            if (annotationMatch) {
                excludeBeanNameSet.add(name);
            }
        }
    }


    @Data
    public static class ExcludeBeanModel {
        private final List<String> beanNameList = new ArrayList<>();
        private final List<Class> classList = new ArrayList<>();
        private final List<Class> annotationList = new ArrayList<>();
        private final List<String> packageList = new ArrayList<>();
        private boolean schedule = false;
        private boolean async = false;

        public boolean isEmpty() {
            return beanNameList.size() + classList.size() + annotationList.size() + packageList.size() == 0
                    && !schedule && !async;
        }
    }

}
