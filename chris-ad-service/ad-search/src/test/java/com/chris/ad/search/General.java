package com.chris.ad.search;

import com.alibaba.fastjson.JSON;
import com.chris.ad.Application;
import com.chris.ad.index.DataTable;
import com.chris.ad.index.adunit.AdUnitIndex;
import com.chris.ad.index.adunit.AdUnitObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class}, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class General {



    @Test
    public void test1(){
        AdUnitIndex adUnitIndex = DataTable.of(AdUnitIndex.class);
        adUnitIndex.add(1L, new AdUnitObject());
        System.out.println(JSON.toJSON(adUnitIndex.get(1L))+" test!!");
    }

}
