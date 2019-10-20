package com.chris.ad.index.adunit;

import com.chris.ad.index.IndexAware;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
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
}
