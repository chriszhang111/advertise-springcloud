package com.chris.ad.service.impl;

import com.chris.ad.Dao.AdPlanRepository;
import com.chris.ad.Dao.AdUserRepository;
import com.chris.ad.constant.CommonStatus;
import com.chris.ad.constant.Constans;
import com.chris.ad.entity.AdPlan;
import com.chris.ad.entity.AdUser;
import com.chris.ad.exception.AdException;
import com.chris.ad.service.IAdPlanService;
import com.chris.ad.utils.CommonUtils;
import com.chris.ad.vo.AdPlanGetRequest;
import com.chris.ad.vo.AdPlanRequest;
import com.chris.ad.vo.AdPlanResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class AdPlanServiceImpl implements IAdPlanService{

    private AdPlanRepository planRepository;
    private AdUserRepository userRepository;

    @Autowired
    public AdPlanServiceImpl(AdPlanRepository planRepository, AdUserRepository userRepository) {
        this.planRepository = planRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public AdPlanResponse createAdPlan(AdPlanRequest request) throws AdException {
        if(!request.createValidate()){
            throw new AdException(Constans.ErrorMsg.REQUEST_PARAM_ERROR);
        }

        Optional<AdUser> adUser = userRepository.findById(request.getUserId());
        if(!adUser.isPresent()){
            throw new AdException(Constans.ErrorMsg.CAN_NOT_FIND_RECORD);
        }

        AdPlan oldplan = planRepository.findByUserIdAndPlanName(request.getUserId(), request.getPlanName());
        if(oldplan != null){
            throw new AdException(Constans.ErrorMsg.SAME_NAME_PLAN_ERROR);
        }

        AdPlan plan = planRepository.save(new AdPlan(
                request.getUserId(),
                request.getPlanName(),
                CommonUtils.parseStringDate(request.getStartDate()),
                CommonUtils.parseStringDate(request.getEndDate())
        ));

        return new AdPlanResponse(plan.getId(), plan.getPlanName());
    }

    @Override
    public List<AdPlan> getAdPlanByIds(AdPlanGetRequest request) throws AdException {

        if(!request.validate()){
            throw new AdException(Constans.ErrorMsg.REQUEST_PARAM_ERROR);
        }
        return planRepository.findAllByIdInAnAndUserId(request.getIds(), request.getUserId());
    }

    @Override
    @Transactional
    public AdPlanResponse updateAdPlan(AdPlanRequest request) throws AdException {

        if(!request.updateValidate()){
            throw new AdException(Constans.ErrorMsg.REQUEST_PARAM_ERROR);
        }
        AdPlan plan = planRepository.findByIdAndAndUserId(request.getId(), request.getUserId());
        if(plan == null){
            throw new AdException(Constans.ErrorMsg.CAN_NOT_FIND_RECORD);
        }
        if(request.getPlanName() != null){
            plan.setPlanName(request.getPlanName());
        }
        if(request.getStartDate() != null){
            plan.setStartDate(CommonUtils.parseStringDate(request.getStartDate()));
        }
        if(request.getEndDate() != null){
            plan.setEndDate(CommonUtils.parseStringDate(request.getEndDate()));
        }

        plan.setUpdateTime(new Date());
        plan = planRepository.save(plan);
        return new AdPlanResponse(plan.getId(), plan.getPlanName());
    }

    @Override
    @Transactional
    public void deleteAdPlan(AdPlanRequest request) throws AdException {
        if(!request.deleteValudate()){
            throw new AdException(Constans.ErrorMsg.REQUEST_PARAM_ERROR);
        }
        AdPlan plan = planRepository.findByIdAndAndUserId(request.getId(), request.getUserId());
        if(plan == null){
            throw new AdException(Constans.ErrorMsg.CAN_NOT_FIND_RECORD);
        }
        plan.setPlanStatus(CommonStatus.INVALID.getStatus());
        plan.setUpdateTime(new Date());
        planRepository.save(plan);
    }
}
