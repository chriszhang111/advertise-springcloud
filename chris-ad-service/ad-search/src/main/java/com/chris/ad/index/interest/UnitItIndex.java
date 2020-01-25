package com.chris.ad.index.interest;

import com.alibaba.fastjson.JSON;
import com.chris.ad.index.IndexAware;
import com.chris.ad.redis.RedisUtils;
import com.chris.ad.utils.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

@Slf4j
@Component
public class UnitItIndex implements IndexAware<String, Set<Long>>{

    private static Map<String, Set<Long>> itUnitMap;
    private static Map<Long, Set<String>> unitItMap;
    private static String IndexName;
    private static String itUnit = "itU";
    private static String unitIt = "Uit";
    @Autowired
    private RedisUtils redisUtils;

    @Override
    public Set<Long> getFromRedis(String key) {
        return JSON.parseObject(redisUtils.hget(IndexName+":"+itUnit, key), ConcurrentSkipListSet.class);
    }

    static{
        itUnitMap = new ConcurrentHashMap<>();
        unitItMap = new ConcurrentHashMap<>();
        IndexName = UnitItIndex.class.getSimpleName();
    }
    @Override
    public Set<Long> get(String key) {
        return itUnitMap.get(key);
    }

    @Override
    public void add(String key, Set<Long> value) {
        Set<Long> unitIds = CommonUtils.GetorCreate(
                key, itUnitMap, ConcurrentSkipListSet::new
        );
        unitIds.addAll(value);
        for(Long unitId: value){
            Set<String> its = CommonUtils.GetorCreate(unitId, unitItMap, ConcurrentSkipListSet::new);
            its.add(key);
        }

        addToRedis(key, value);
    }

    public void addToRedis(String key, Set<Long> value){
        Set<Long> unitIds = JSON.parseObject(redisUtils.hget(IndexName+":"+itUnit, key), ConcurrentSkipListSet.class);
        if(unitIds == null)
            unitIds = new ConcurrentSkipListSet<>();
        unitIds.addAll(value);
        redisUtils.hset(IndexName+":"+itUnit, key, JSON.toJSONString(value));

        for(Long unitId: value){
            Set<String> its = JSON.parseObject(redisUtils.hget(IndexName+":"+unitIt, key),ConcurrentSkipListSet.class);
            if(its == null)
                its = new ConcurrentSkipListSet<>();
            its.add(key);
            redisUtils.hset(IndexName+":"+unitIt, unitId+"", JSON.toJSONString(its));
        }
    }

    @Override
    public void update(String key, Set<Long> value) {
            log.error("it index can not support update");
    }

    @Override
    public void delete(String key, Set<Long> value) {

        Set<Long> unitIds = CommonUtils.GetorCreate(
                key, itUnitMap, ConcurrentSkipListSet::new
        );
        unitIds.removeAll(value);
        for(Long unitId: value){
            Set<String> itTagSet = CommonUtils.GetorCreate(
                    unitId, unitItMap, ConcurrentSkipListSet::new
            );
            itTagSet.remove(key);
        }
    }

    public boolean match(Long unitId, List<String> tags){
        if(unitItMap.containsKey(unitId) &&
                CollectionUtils.isNotEmpty(unitItMap.get(unitId))){
            Set<String> unitKeywords = unitItMap.get(unitId);
            return CollectionUtils.isSubCollection(tags, unitKeywords);
        }
        return false;
    }
}
