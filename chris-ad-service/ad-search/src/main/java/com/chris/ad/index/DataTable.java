package com.chris.ad.index;

import com.chris.ad.search.vo.feature.KeywordFeature;
import com.chris.ad.search.vo.media.Geo;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.PriorityOrdered;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class DataTable implements ApplicationContextAware, PriorityOrdered{

    private static ApplicationContext applicationContext;

    public static final Map<Class, Object> dataTableMap = new ConcurrentHashMap<>();

    @Override
    public int getOrder() {
        return PriorityOrdered.HIGHEST_PRECEDENCE;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        DataTable.applicationContext = applicationContext;

    }

    private static <T> T bean(String beanName){
        return (T) applicationContext.getBean(beanName);
    }

    private static <T> T bean(Class clazz){
        return (T)applicationContext.getBean(clazz);
    }

    public static <T> T of(Class<T> clazz){
        T instance = (T) dataTableMap.get(clazz);
        if(instance != null){
            return instance;
        }

        dataTableMap.put(clazz, bean(clazz));
        return (T) dataTableMap.get(clazz);
    }


}
