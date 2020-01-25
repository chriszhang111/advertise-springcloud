package com.chris.ad.index.adunit;

import com.alibaba.fastjson.JSON;
import com.chris.ad.index.IndexAware;
import com.chris.ad.redis.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class AdUnitIndex implements IndexAware<Long, AdUnitObject> {

    private static Map<Long, AdUnitObject> map;
    private static String IndexName;

    @Autowired
    private RedisUtils redisUtils;

    static{
        map = new ConcurrentHashMap<>();
        IndexName = AdUnitIndex.class.getSimpleName();
    }
    @Override
    public AdUnitObject get(Long key) {
        return map.get(key);
    }

    @Override
    public AdUnitObject getFromRedis(Long key) {
        return JSON.parseObject(redisUtils.hget(IndexName, key+""), AdUnitObject.class);
    }

    @Override
    public void add(Long key, AdUnitObject value) {
        map.put(key, value);
        redisUtils.hset(IndexName, key+"", JSON.toJSONString(value));
        log.info("unit object add:{}", value);
    }

    @Override
    public void update(Long key, AdUnitObject value) {
        AdUnitObject oldobj = map.get(key);
        AdUnitObject object2 = JSON.parseObject(redisUtils.hget(IndexName, key+""), AdUnitObject.class);
        if(oldobj != null){
            oldobj.update(value);
        }else{
            map.put(key, value);
        }
        if(object2 != null){
            object2.update(value);
            redisUtils.hset(IndexName, key+"", JSON.toJSONString(object2));
        }else{
            redisUtils.hset(IndexName, key+"", JSON.toJSONString(value));
        }
    }

    @Override
    public void delete(Long key, AdUnitObject value) {
            map.remove(key);
            redisUtils.hdel(IndexName, key+"");
    }

    public Set<Long> match(Integer positionType) {

        Set<Long> adUnitIds = new HashSet<>();

        map.forEach((k, v) -> {
            if (AdUnitObject.isAdSlotTypeOK(positionType,
                    v.getPositionType())) {
                adUnitIds.add(k);
            }
        });

        return adUnitIds;
    }

    public List<AdUnitObject> fetch(Collection<Long> adUnitIds) {

        if (CollectionUtils.isEmpty(adUnitIds)) {
            return Collections.emptyList();
        }

        List<AdUnitObject> result = new ArrayList<>();

        adUnitIds.forEach(u -> {
            AdUnitObject object = get(u);
            if (object == null) {
                log.error("AdUnitObject not found: {}", u);
                return;
            }
            result.add(object);
        });

        return result;
    }
}
