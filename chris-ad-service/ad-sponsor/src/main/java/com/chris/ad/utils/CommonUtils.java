package com.chris.ad.utils;

import com.chris.ad.exception.AdException;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.time.DateUtils;

import java.util.Date;

public class CommonUtils {
    private static String[] parsePatterns = {
      "yyyy-MM-dd",
      "yyyy/MM/dd",
      "yyyy.MM.dd"
    };
    public static String md5(String value){
        return DigestUtils.md5Hex(value).toUpperCase();
    }

    public static Date parseStringDate(String date) throws AdException{
        try{
            return DateUtils.parseDate(date, parsePatterns);
        }catch (Exception e){
            throw new AdException(e.getMessage());
        }
    }
}
