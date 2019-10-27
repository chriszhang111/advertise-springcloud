package com.chris.ad.utils;

import java.util.Map;
import java.util.function.Supplier;

public class CommonUtils {

    public static <K,V> V GetorCreate(K key, Map<K,V> map, Supplier<V> factory){
        return map.computeIfAbsent(key, k->factory.get());
    }

    public static String stringConcat(String...args){
        StringBuilder result = new StringBuilder();
        for(String arg:args){
            result.append(arg).append("-");
        }
        result.deleteCharAt(result.length()-1);
        return result.toString();
    }
}
