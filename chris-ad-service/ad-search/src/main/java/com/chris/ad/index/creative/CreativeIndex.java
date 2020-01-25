package com.chris.ad.index.creative;



import com.alibaba.fastjson.JSON;
import com.chris.ad.index.IndexAware;
import com.chris.ad.redis.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class CreativeIndex implements IndexAware<Long, CreativeObject>{
    private static Map<Long, CreativeObject> objectMap;
    private static String IndexName;
    @Autowired
    private RedisUtils redisUtils;

    static {
        objectMap = new ConcurrentHashMap<>();
        IndexName = CreativeIndex.class.getSimpleName();
    }

    //根据creativeID，获取对象
    public List<CreativeObject> fetch(Collection<Long> adIds) {

        if (CollectionUtils.isEmpty(adIds)) {
            return Collections.emptyList();
        }
        List<CreativeObject> result = new ArrayList<>();
        adIds.forEach(u -> {
            CreativeObject object = get(u);
            if (null == object) {
                log.error("CreativeObject not found: {}", u);
                return;
            }

            result.add(object);
        });

        return result;
    }

    @Override
    public CreativeObject getFromRedis(Long key) {
        return JSON.parseObject(redisUtils.hget(IndexName, key+""), CreativeObject.class);
    }

    @Override
    public CreativeObject get(Long key) {
        return objectMap.get(key);
    }

    @Override
    public void add(Long key, CreativeObject value) {
        objectMap.put(key, value);
        redisUtils.hset(IndexName, key+"", JSON.toJSONString(value));
    }

    @Override
    public void update(Long key, CreativeObject value) {
        CreativeObject oldObject = objectMap.get(key);
        if (null == oldObject) {
            objectMap.put(key, value);
        } else {
            oldObject.update(value);
        }
    }

    @Override
    public void delete(Long key, CreativeObject value) {
        objectMap.remove(key);
    }

    //Todo: add redis update, delete
}
