package com.chris.ad.controller;

import com.alibaba.fastjson.JSON;
import com.chris.ad.annotation.IgnoreResponseAdvice;
import com.chris.ad.client.SponsorClient;
import com.chris.ad.client.vo.AdPlan;
import com.chris.ad.client.vo.AdPlanGetRequest;
import com.chris.ad.vo.CommonResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Slf4j
@RestController
public class SearchController {

    private RestTemplate restTemplate;
    private SponsorClient sponsorClient;

    public SearchController(RestTemplate restTemplate, SponsorClient sponsorClient) {
        this.restTemplate = restTemplate;
        this.sponsorClient = sponsorClient;
    }

    @IgnoreResponseAdvice
    @PostMapping("/getPlansByRibbon")
    public CommonResponse<List<AdPlan>> getAdPlansByRibbon(
            @RequestBody AdPlanGetRequest request
            ){
        log.info("ad-search: getAdPlansByRibbon -> {}", JSON.toJSONString(request));

        return restTemplate.postForEntity(
               "http://eureka-client-ad-sponsor/ad-sponsor/get/plan",
                request,
                CommonResponse.class
        ).getBody();
    }

    @IgnoreResponseAdvice
    @PostMapping("/getPlans")
    public CommonResponse<List<AdPlan>> getAdPlans(@RequestBody AdPlanGetRequest request){
        log.info("ad-search: getAdPlans -> {}", JSON.toJSONString(request));
        return sponsorClient.getAdPlans(request);
    }
}
