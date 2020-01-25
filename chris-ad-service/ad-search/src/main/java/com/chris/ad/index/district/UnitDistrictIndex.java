package com.chris.ad.index.district;

import com.alibaba.fastjson.JSON;
import com.chris.ad.index.IndexAware;
import com.chris.ad.redis.RedisUtils;
import com.chris.ad.search.vo.feature.DistrictFeature;
import com.chris.ad.utils.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Collectors;

@Slf4j
@Component
public class UnitDistrictIndex implements IndexAware<String,Set<Long>>{
    private static Map<String, Set<Long>> districtUnitMap;
    private static Map<Long, Set<String>> unitDistrictMap;
    private static String IndexName;
    private static String disUnit = "disU";
    private static String unitDis = "uDis";

    @Autowired
    private RedisUtils redisUtils;

    @Override
    public Set<Long> getFromRedis(String key) {
        return JSON.parseObject(redisUtils.hget(IndexName+":"+disUnit, key), ConcurrentSkipListSet.class);
    }

    static{
        districtUnitMap = new ConcurrentHashMap<>();
        unitDistrictMap = new ConcurrentHashMap<>();
        IndexName = UnitDistrictIndex.class.getSimpleName();
    }

    @Override
    public Set<Long> get(String key) {
        return districtUnitMap.get(key);
    }

    @Override
    public void add(String key, Set<Long> value) {
        Set<Long> unitIds = CommonUtils.GetorCreate(
                key, districtUnitMap, ConcurrentSkipListSet::new
        );
        unitIds.addAll(value);
        for(Long unitId: value){
            Set<String> district = CommonUtils.GetorCreate(unitId, unitDistrictMap, ConcurrentSkipListSet::new);
            district.add(key);
        }

        addToRedis(key, value);
    }

    public void addToRedis(String key, Set<Long> value){
        boolean flag_set = true;
        Set<Long> unitIds = JSON.parseObject(redisUtils.hget(IndexName+":"+disUnit, key), ConcurrentSkipListSet.class);
        if(unitIds == null)
            unitIds = new ConcurrentSkipListSet<>();
        unitIds.addAll(value);
        redisUtils.hset(IndexName+":"+disUnit, key, JSON.toJSONString(unitIds));

        for(Long unitId: value){
            Set<String> district = JSON.parseObject(redisUtils.hget(IndexName+":"+unitDis, unitId+""), ConcurrentSkipListSet.class);
            if(district == null)
                district = new ConcurrentSkipListSet<>();
            district.add(key);
            redisUtils.hset(IndexName+":"+unitDis, unitId+"", JSON.toJSONString(district));

        }

    }

    @Override
    public void update(String key, Set<Long> value) {
        log.error("can not support update");
    }

    @Override
    public void delete(String key, Set<Long> value) {
        Set<Long> unitIds = CommonUtils.GetorCreate(key, districtUnitMap, ConcurrentSkipListSet::new);
        unitIds.removeAll(value);
        for(Long unitId: value){
            Set<String> district = CommonUtils.GetorCreate(unitId, unitDistrictMap, ConcurrentSkipListSet::new);
            district.remove(key);
        }
    }

    public boolean match(Long adUnitId, List<DistrictFeature.ProvinceAndCity> districts){
        if(unitDistrictMap.containsKey(adUnitId) &&
                CollectionUtils.isNotEmpty(unitDistrictMap.get(adUnitId))){
            Set<String> unitDistricts = unitDistrictMap.get(adUnitId);

            List<String> targetDistricts = districts.stream().map(
                    d->CommonUtils.stringConcat(d.getProvince(), d.getCity())
            ).collect(Collectors.toList());
            return CollectionUtils.isSubCollection(targetDistricts, unitDistricts);
        }
        return false;
    }
}
