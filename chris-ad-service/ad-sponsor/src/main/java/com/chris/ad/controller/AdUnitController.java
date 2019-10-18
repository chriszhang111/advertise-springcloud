package com.chris.ad.controller;


import com.alibaba.fastjson.JSON;
import com.chris.ad.exception.AdException;
import com.chris.ad.service.IAdUnitService;
import com.chris.ad.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class AdUnitController {

    private IAdUnitService adUnitService;

    public AdUnitController(IAdUnitService adUnitService) {
        this.adUnitService = adUnitService;
    }

    @PostMapping("/create/adunit")
    public AdUnitResponse createUnit(
            @RequestBody AdUnitRequest request
            ) throws AdException{
        log.info("ad-sponsor:createUnit -> {}",
                JSON.toJSONString(request));
        return adUnitService.createUnit(request);
    }

    @PostMapping("/create/keyword")
    public AdUnitKeywordResponse createKeyword(@RequestBody AdUnitKeywordRequest request) throws AdException{
        log.info("ad-sponsor:createKeyword -> {}", JSON.toJSONString(request));
        return adUnitService.createUnitKeyword(request);

    }

    @PostMapping("/create/unitIt")
    public AdUnitItResponse createUnitIt(
            @RequestBody AdUnitItRequest request
    ) throws AdException {
        log.info("ad-sponsor: createUnitIt -> {}",
                JSON.toJSONString(request));
        return adUnitService.createUnitIt(request);
    }

    @PostMapping("/create/unitDistrict")
    public AdUnitDistrictResponse createUnitDistrict(
            @RequestBody AdUnitDistrictRequest request
    ) throws AdException {
        log.info("ad-sponsor: createUnitDistrict -> {}",
                JSON.toJSONString(request));
        return adUnitService.createUnitDistrict(request);
    }

    @PostMapping("/create/creativeUnit")
    public CreativeUnitResponse createCreativeUnit(
            @RequestBody CreativeUnitRequest request
    ) throws AdException {
        log.info("ad-sponsor: createCreativeUnit -> {}",
                JSON.toJSONString(request));
        return adUnitService.createCreativeUnit(request);
    }
}
