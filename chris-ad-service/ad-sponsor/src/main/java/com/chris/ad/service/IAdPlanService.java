package com.chris.ad.service;

import com.chris.ad.entity.AdPlan;
import com.chris.ad.exception.AdException;
import com.chris.ad.vo.AdPlanGetRequest;
import com.chris.ad.vo.AdPlanRequest;
import com.chris.ad.vo.AdPlanResponse;

import java.util.List;

public interface IAdPlanService {

    AdPlanResponse createAdPlan(AdPlanRequest request) throws AdException;

    List<AdPlan> getAdPlanByIds(AdPlanGetRequest request) throws AdException;

    AdPlanResponse updateAdPlan(AdPlanRequest request) throws AdException;

    void deleteAdPlan(AdPlanRequest request) throws AdException;

}
