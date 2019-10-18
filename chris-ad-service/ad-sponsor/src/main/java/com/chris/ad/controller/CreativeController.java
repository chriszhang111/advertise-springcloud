package com.chris.ad.controller;

import com.alibaba.fastjson.JSON;
import com.chris.ad.exception.AdException;
import com.chris.ad.service.ICreativeService;
import com.chris.ad.vo.AdCreativeRequest;
import com.chris.ad.vo.AdCreativeResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class CreativeController {

    private ICreativeService creativeService;

    public CreativeController(ICreativeService creativeService) {
        this.creativeService = creativeService;
    }

    @PostMapping("/create/creative")
    public AdCreativeResponse createCreative(
            @RequestBody AdCreativeRequest request
    ) throws AdException{
        log.info("ad-sponsor: createCreative -> {}",
                JSON.toJSONString(request));
        return creativeService.createCreative(request);
    }
}
