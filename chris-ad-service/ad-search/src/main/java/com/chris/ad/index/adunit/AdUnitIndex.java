package com.chris.ad.index.adunit;

import com.chris.ad.index.IndexAware;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class AdUnitIndex implements IndexAware<Long, AdUnitObject> {

    private static Map<Long, AdUnitObject> map;

    static{
        map = new ConcurrentHashMap<>();
    }
    @Override
    public AdUnitObject get(Long key) {
        return map.get(key);
    }

    @Override
    public void add(Long key, AdUnitObject value) {
        map.put(key, value);
    }

    @Override
    public void update(Long key, AdUnitObject value) {
        AdUnitObject oldobj = map.get(key);
        if(oldobj != null){
            oldobj.update(value);
        }else{
            map.put(key, value);
        }
    }

    @Override
    public void delete(Long key, AdUnitObject value) {
            map.remove(key);
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
