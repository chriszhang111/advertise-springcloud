package com.chris.ad.service;

import com.alibaba.fastjson.JSON;
import com.chris.ad.Application;
import com.chris.ad.exception.AdException;
import com.chris.ad.vo.AdPlanGetRequest;
import com.chris.ad.vo.AdUnitRequest;
import com.chris.ad.vo.AdUnitResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class},
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class AdPlanServiceTest {

    @Autowired
    private IAdPlanService adPlanService;

    @Autowired
    private IAdUnitService unitService;

    @Test
    public void testGetAdPlan() throws AdException{
        System.out.println("****************************************************");
        System.out.println(
                adPlanService.getAdPlanByIds(
                        new AdPlanGetRequest(15L,
                                Collections.singletonList(10L))
                )
        );
    }

    @Test
    public void testCreateUnit() throws AdException{
        System.out.println("****************************************************");
        AdUnitResponse response = unitService.createUnit(
                new AdUnitRequest(
                        10L,
                        "第三个推广单元",
                        1,
                        1000000L
                )
        );

        System.out.println(JSON.toJSONString(response));
    }




}
