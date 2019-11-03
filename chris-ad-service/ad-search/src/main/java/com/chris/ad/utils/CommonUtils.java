package com.chris.ad.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.time.DateUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;
import java.util.Date;

@Slf4j
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

    //Sun Nov 03 09:35:45 EST 2019
    public static Date parseStringDate(String date){

        try{
            DateFormat dateFormat = new SimpleDateFormat(
                    "EEE MMM dd HH:mm:ss zzz yyyy",
                    Locale.US
            );

            return DateUtils.addHours(dateFormat.parse(date), -0);
        }catch (ParseException ex){
            log.error(ex.getMessage());
            return null;
        }
    }

//    public static void main(String[] args) {
//        System.out.println(parseStringDate("Sun Nov 03 09:35:45 EST 2019"));
//    }
}
