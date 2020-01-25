package com.chris.ad.index.adplan;

import com.alibaba.fastjson.JSON;
import com.chris.ad.client.vo.AdPlan;
import com.chris.ad.index.IndexAware;
import com.chris.ad.redis.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class AdPlanIndex implements IndexAware<Long, AdPlanObject>{

    private static Map<Long, AdPlanObject> objectMap;
    private static String IndexName;

    @Autowired
    private RedisUtils redisUtils;

    static{
        objectMap = new ConcurrentHashMap<>();
        IndexName = AdPlanIndex.class.getSimpleName();
    }
    @Override
    public AdPlanObject get(Long key) {
        return objectMap.get(key);
    }

    @Override
    public void add(Long key, AdPlanObject value) {
        log.info("before add");
        objectMap.put(key, value);
        redisUtils.hset(IndexName, key+"", JSON.toJSONString(value));
        log.info("after add: {}", objectMap);

    }

    @Override
    public AdPlanObject getFromRedis(Long key) {
        return JSON.parseObject(redisUtils.hget(IndexName, key+""), AdPlanObject.class);
    }

    @Override
    public void update(Long key, AdPlanObject value) {
            AdPlanObject oldobj = objectMap.get(key);
            AdPlanObject oldobj2 = JSON.parseObject(redisUtils.hget(IndexName, key+""), AdPlanObject.class);
            if(oldobj == null)
                objectMap.put(key, value);
            else
                oldobj.update(value);
            if(oldobj2 == null)
                redisUtils.hset(IndexName, key+"", JSON.toJSONString(value));
            else{
                oldobj2.update(value);
                redisUtils.hset(IndexName, key+"", JSON.toJSONString(value));
            }
    }

    @Override
    public void delete(Long key, AdPlanObject value) {
            objectMap.remove(key);
            redisUtils.hdel(IndexName, key+"");

    }
}
