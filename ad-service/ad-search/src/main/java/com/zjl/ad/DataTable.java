package com.zjl.ad;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.PriorityOrdered;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: JunLog
 * @Description: 通过DataTable实现ApplicationContextAware应用程序上下文 得到Spring容器中初始化的所有组件
 * 用这种方式来缓存这么多创建的索引
 * Date: 2022/7/27 20:27
 */
@Component
public class DataTable implements ApplicationContextAware, PriorityOrdered {

    private static ApplicationContext applicationContext;

    // 定义Map保存所有Index
    private static final Map<Class, Object> dataTableMap = new ConcurrentHashMap<>();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        // 初始化
        DataTable.applicationContext = applicationContext;
    }

    @Override
    public int getOrder() {
        return PriorityOrdered.HIGHEST_PRECEDENCE;
    }

    /**
     * 提供获取索引类的方法 在需要运用的适合调用该方法获取相应的索引类
     * @param clazz
     * @param <T>
     * @return
     */
    @SuppressWarnings("all")
    public static <T> T of (Class<T> clazz) {
        T instance = (T) dataTableMap.get(clazz);
        if (null != instance ){
            return instance;
        }
        dataTableMap.put(clazz, bean(clazz));
        return (T) dataTableMap.get(clazz);
    }

    /**
     * 通过beanName获得bean
     * @param beanName
     * @param <T>
     * @return
     */
    @SuppressWarnings("all")
    private static <T> T bean(String beanName) {
        return (T) applicationContext.getBean(beanName);
    }

    /**
     * 通过Class获得bean
     * @param clazz
     * @param <T>
     * @return
     */
    @SuppressWarnings("all")
    private static <T> T bean(Class clazz) {
        return (T) applicationContext.getBean(clazz);
    }
}
