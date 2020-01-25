package com.chris.ad.redis;


import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Set;

@Component
public class RedisUtils {

    @Autowired
    private StringRedisTemplate redisTemplate;

    public void set(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public void set(String key, Set<Integer> value){
        redisTemplate.opsForSet().add(key, JSON.toJSONString(value));
    }

    public String get(String key) {
        return (String)redisTemplate.opsForValue().get(key);
    }

    public String getSet(String key){
        return redisTemplate.opsForSet().pop(key);
    }

    public void hset(String key, Long field, Object value) {
        redisTemplate.opsForHash().put(key, field, value);
    }

    public void hset(String key, String field, Object value){
        redisTemplate.opsForHash().put(key, field, value);
    }

    /**
     * 实现命令：HGET key field，返回哈希表 key中给定域 field的值
     *
     * @param key
     * @param field
     * @return
     */
    public String hget(String key, String field) {
        return (String) redisTemplate.opsForHash().get(key, field);
    }

    public void hdel(String key, Object... fields) {
        redisTemplate.opsForHash().delete(key, fields);
    }

}
