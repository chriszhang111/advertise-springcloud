package com.chris.ad.controller;

import com.alibaba.fastjson.JSON;
import com.chris.ad.entity.AdPlan;
import com.chris.ad.exception.AdException;
import com.chris.ad.service.IAdPlanService;
import com.chris.ad.vo.AdPlanGetRequest;
import com.chris.ad.vo.AdPlanRequest;
import com.chris.ad.vo.AdPlanResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
public class AdPlanController {

    private IAdPlanService adPlanService;

    public AdPlanController(IAdPlanService adPlanService) {
        this.adPlanService = adPlanService;
    }

    @PostMapping("/create/plan")
    public AdPlanResponse createAdPlan(@RequestBody AdPlanRequest request) throws AdException{
        log.info("ad-sponsor: createAdPlan -> {}",
                JSON.toJSONString(request));
        return adPlanService.createAdPlan(request);
    }

    @PostMapping("/get/plan")
    public List<AdPlan> getAdPlanByIds(@RequestBody AdPlanGetRequest request) throws AdException{
        log.info("ad-sponsor: getAdPlan -> {}",
                JSON.toJSONString(request));
        return adPlanService.getAdPlanByIds(request);
    }

    @PutMapping("/update/plan")
    public AdPlanResponse updateAdPlan(@RequestBody AdPlanRequest request) throws AdException{
        log.info("ad-sponsor: updateAdPlan -> {}",
                JSON.toJSONString(request));
        return adPlanService.updateAdPlan(request);
    }

    @DeleteMapping("/delete/plan")
    public void deleteAdPlan(@RequestBody AdPlanRequest request) throws  AdException{
        log.info("ad-sponsor:deleteAdPlan -> {}",
                JSON.toJSONString(request));
        adPlanService.deleteAdPlan(request);
    }
}
