package com.chris.ad.index.interest;

import com.chris.ad.index.IndexAware;
import com.chris.ad.utils.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

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

    @Override
    public Set<Long> getFromRedis(String key) {
        return null;
    }

    static{
        itUnitMap = new ConcurrentHashMap<>();
        unitItMap = new ConcurrentHashMap<>();
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
