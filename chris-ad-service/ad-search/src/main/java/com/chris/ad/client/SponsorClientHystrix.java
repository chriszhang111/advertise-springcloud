package com.chris.ad.client;

import com.chris.ad.client.vo.AdPlan;
import com.chris.ad.client.vo.AdPlanGetRequest;
import com.chris.ad.vo.CommonResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SponsorClientHystrix implements SponsorClient{
    @Override
    public CommonResponse<List<AdPlan>> getAdPlans(AdPlanGetRequest request) {
        return new CommonResponse<>(-1, "Eureka Client Ad-sponsor Error");
    }


}
