package com.chris.ad.index.adplan;

import com.chris.ad.index.IndexAware;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class AdPlanIndex implements IndexAware<Long, AdPlanObject>{

    private static Map<Long, AdPlanObject> objectMap;

    static{
        objectMap = new ConcurrentHashMap<>();
    }
    @Override
    public AdPlanObject get(Long key) {
        return objectMap.get(key);
    }

    @Override
    public void add(Long key, AdPlanObject value) {
        log.info("before add");
        objectMap.put(key, value);
        log.info("after add: {}", objectMap);

    }

    @Override
    public void update(Long key, AdPlanObject value) {
            AdPlanObject oldobj = objectMap.get(key);
            if(oldobj == null)
                objectMap.put(key, value);
            else
                oldobj.update(value);
    }

    @Override
    public void delete(Long key, AdPlanObject value) {
            objectMap.remove(key);
    }
}
