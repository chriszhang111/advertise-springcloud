package com.chris.ad.index.creativeunit;

import com.alibaba.fastjson.JSON;
import com.chris.ad.index.IndexAware;
import com.chris.ad.index.adunit.AdUnitObject;
import com.chris.ad.redis.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

@Slf4j
@Component
public class CreativeUnitIndex implements IndexAware<String, CreativeUnitObject>{

    private static Map<String, CreativeUnitObject> objectMap;
    // <k:adId, v:unitId Set>
    private static Map<Long, Set<Long>> creativeUnitMap;
    // <k:unitId, v:adId set>
    private static Map<Long, Set<Long>> unitCreativeMap;

    private static String IndexName;
    private static String ObjMap = "objM";
    private static String CreUniMap = "cuM";
    private static String UniCreMap = "ucM";

    @Autowired
    private RedisUtils redisUtils;

    static {
        objectMap = new ConcurrentHashMap<>();
        creativeUnitMap = new ConcurrentHashMap<>();
        unitCreativeMap = new ConcurrentHashMap<>();
        IndexName = CreativeUnitIndex.class.getSimpleName();
    }

    @Override
    public CreativeUnitObject getFromRedis(String key) {
        return JSON.parseObject(redisUtils.hget(IndexName, key), CreativeUnitObject.class);
    }

    @Override
    public CreativeUnitObject get(String key) {
        return objectMap.get(key);
    }

    @Override
    public void add(String key, CreativeUnitObject value) {

        objectMap.put(key, value);
        Set<Long> unitSet = creativeUnitMap.get(value.getAdId());
        if (CollectionUtils.isEmpty(unitSet)) {
            unitSet = new ConcurrentSkipListSet<>();
            creativeUnitMap.put(value.getAdId(), unitSet);
        }
        unitSet.add(value.getUnitId());

        Set<Long> creativeSet = unitCreativeMap.get(value.getUnitId());
        if (CollectionUtils.isEmpty(creativeSet)) {
            creativeSet = new ConcurrentSkipListSet<>();
            unitCreativeMap.put(value.getUnitId(), creativeSet);
        }
        creativeSet.add(value.getAdId());
        addToRedis(key, value);

    }

    public void addToRedis(String key, CreativeUnitObject value){
        redisUtils.hset(IndexName+":"+ObjMap, key, JSON.toJSONString(value));
        Set<Long> unitSet = JSON.parseObject(redisUtils.hget(IndexName+":"+CreUniMap, value.getAdId()+""), HashSet.class);
        if(CollectionUtils.isEmpty(unitSet)){
            unitSet = new ConcurrentSkipListSet<>();
        }
        unitSet.add(value.getUnitId());
        Set<Long> creativeSet = JSON.parseObject(redisUtils.hget(IndexName+":"+UniCreMap, value.getUnitId()+""), HashSet.class);
        if(CollectionUtils.isEmpty(creativeSet)){
            creativeSet = new ConcurrentSkipListSet<>();
        }
        creativeSet.add(value.getAdId());
        redisUtils.hset(IndexName+":"+CreUniMap, value.getAdId()+"", JSON.toJSONString(unitSet));
        redisUtils.hset(IndexName+":"+UniCreMap, value.getUnitId()+"",JSON.toJSONString(creativeSet));
    }

    @Override
    public void update(String key, CreativeUnitObject value) {
        log.error("CreativeUnitIndex not support update");
    }

    @Override
    public void delete(String key, CreativeUnitObject value) {
        objectMap.remove(key);

        Set<Long> unitSet = creativeUnitMap.get(value.getAdId());
        if (CollectionUtils.isNotEmpty(unitSet)) {
            unitSet.remove(value.getUnitId());
        }

        Set<Long> creativeSet = unitCreativeMap.get(value.getUnitId());
        if (CollectionUtils.isNotEmpty(creativeSet)) {
            creativeSet.remove(value.getAdId());
        }
    }

    public List<Long> selectAds(List<AdUnitObject> unitObjects) {
        if (CollectionUtils.isEmpty(unitObjects)) {
            return Collections.emptyList();
        }
        List<Long> result = new ArrayList<>();

        for (AdUnitObject unitObject : unitObjects) {
            Set<Long> adIds = unitCreativeMap.get(unitObject.getUnitId());
            if (CollectionUtils.isNotEmpty(adIds)) {
                result.addAll(adIds);
            }
        }
        return result;
    }
}
