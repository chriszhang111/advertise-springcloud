package com.chris.ad.service.impl;

import com.chris.ad.Dao.AdPlanRepository;
import com.chris.ad.Dao.AdUnitRepository;
import com.chris.ad.Dao.CreativeRepository;
import com.chris.ad.Dao.unit_condition.AdUnitDistrictRepository;
import com.chris.ad.Dao.unit_condition.AdUnitItRepository;
import com.chris.ad.Dao.unit_condition.AdUnitKeywordRepository;
import com.chris.ad.Dao.unit_condition.CreativeUnitRepository;
import com.chris.ad.constant.Constans;
import com.chris.ad.entity.AdPlan;
import com.chris.ad.entity.AdUnit;
import com.chris.ad.entity.unit_condition.AdUnitDistrict;
import com.chris.ad.entity.unit_condition.AdUnitIt;
import com.chris.ad.entity.unit_condition.AdUnitKeyword;
import com.chris.ad.entity.unit_condition.CreativeUnit;
import com.chris.ad.exception.AdException;
import com.chris.ad.service.IAdUnitService;
import com.chris.ad.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AdUnitServiceImpl implements IAdUnitService{

    private AdUnitRepository unitRepository;
    private AdPlanRepository planRepository;
    private AdUnitKeywordRepository keywordRepository;
    private AdUnitItRepository itRepository;
    private AdUnitDistrictRepository districtRepository;
    private CreativeRepository creativeRepository;
    private CreativeUnitRepository creativeUnitRepository;

    public AdUnitServiceImpl(AdUnitRepository unitRepository,
                             AdPlanRepository planRepository,
                             AdUnitKeywordRepository keywordRepository,
                             AdUnitItRepository itRepository,
                             AdUnitDistrictRepository districtRepository,
                             CreativeRepository creativeRepository,
                             CreativeUnitRepository creativeUnitRepository) {
        this.unitRepository = unitRepository;
        this.planRepository = planRepository;
        this.keywordRepository = keywordRepository;
        this.itRepository = itRepository;
        this.districtRepository = districtRepository;
        this.creativeRepository = creativeRepository;
        this.creativeUnitRepository = creativeUnitRepository;
    }

    @Override
    public AdUnitResponse createUnit(AdUnitRequest request) throws AdException {
        if(!request.createValidate()){
            throw new AdException(Constans.ErrorMsg.REQUEST_PARAM_ERROR);
        }

        Optional<AdPlan> plan= planRepository.findById(request.getPlanId());
        if(!plan.isPresent()){
            throw new AdException(Constans.ErrorMsg.CAN_NOT_FIND_RECORD);
        }

        AdUnit oldAdUnit = unitRepository.findByPlanIdAndUnitName(
                request.getPlanId(),request.getUnitName()
        );
        if(oldAdUnit != null){
            throw new AdException(Constans.ErrorMsg.SAME_NAME_UNIT_ERROT);
        }

        AdUnit newAdunit = unitRepository.save(
                new AdUnit(request.getPlanId(), request.getUnitName(),
                        request.getPositionType(), request.getBudget())
        );

        return new AdUnitResponse(newAdunit.getId(),
                newAdunit.getUnitName());
    }

    @Override
    @Transactional
    public AdUnitKeywordResponse createUnitKeyword(AdUnitKeywordRequest request)
            throws AdException {
        List<Long> unitIds = request.getUnitKeywords().stream()
                .map(AdUnitKeywordRequest.UnitKeyword::getUnitId)
                .collect(Collectors.toList());
        if(!isRelatedUnitExist(unitIds))
            throw new AdException(Constans.ErrorMsg.REQUEST_PARAM_ERROR);
        List<Long> ids = Collections.emptyList();

        List<AdUnitKeyword> unitKeywords = new ArrayList<>();
        if(!CollectionUtils.isEmpty(request.getUnitKeywords())){
            request.getUnitKeywords().forEach(i -> unitKeywords.add(
                    new AdUnitKeyword(i.getUnitId(), i.getKeyword())
            ));

            ids = keywordRepository.saveAll(unitKeywords).stream()
                    .map(AdUnitKeyword::getId).collect(Collectors.toList());
        }
        return new AdUnitKeywordResponse(ids);
    }

    @Override
    @Transactional
    public AdUnitItResponse createUnitIt(AdUnitItRequest request) throws AdException {
        List<Long> unitIds = request.getUnitIts().stream()
                .map(AdUnitItRequest.UnitIt::getUnitId)
                .collect(Collectors.toList());
        if(!isRelatedUnitExist(unitIds))
            throw new AdException(Constans.ErrorMsg.REQUEST_PARAM_ERROR);
        List<Long> ids = Collections.emptyList();
        List<AdUnitIt> unitIts = new ArrayList<>();
        request.getUnitIts().forEach(i -> unitIts.add(
                new AdUnitIt(i.getUnitId(), i.getItTag())
        ));

        ids = itRepository.saveAll(unitIts).stream().map(AdUnitIt::getId)
                .collect(Collectors.toList());
        return new AdUnitItResponse(ids);

    }

    @Override
    @Transactional
    public AdUnitDistrictResponse createUnitDistrict(AdUnitDistrictRequest request) throws AdException {
        List<Long> unitIds = request.getUnitDistricts().stream()
                .map(AdUnitDistrictRequest.UnitDistrict::getUnitId)
                .collect(Collectors.toList());
        if(!isRelatedUnitExist(unitIds))
            throw new AdException(Constans.ErrorMsg.REQUEST_PARAM_ERROR);

        List<Long> ids = Collections.emptyList();
        List<AdUnitDistrict> districts = new ArrayList<>();
        request.getUnitDistricts().forEach(i -> districts.add(
                new AdUnitDistrict(i.getUnitId(), i.getProvince(), i.getCity())
        ));

        ids = districtRepository.saveAll(districts).stream().map(
                AdUnitDistrict::getId
        ).collect(Collectors.toList());
        return new AdUnitDistrictResponse(ids);
    }

    private boolean isRelatedUnitExist(List<Long> unitIds){
        if(CollectionUtils.isEmpty(unitIds))
            return false;
        return unitRepository.findAllById(unitIds).size() == new HashSet<>(unitIds).size();
    }

    private boolean isRelatedCreativeExist(List<Long> creativeIds){
        if(CollectionUtils.isEmpty(creativeIds))
            return false;
        return creativeRepository.findAllById(creativeIds).size() == new HashSet<>(creativeIds).size();
    }

    @Override
    @Transactional
    public CreativeUnitResponse createCreativeUnit(CreativeUnitRequest request) throws AdException {
        List<Long> unitIds = request.getCreativeunitItems().stream().
                map(CreativeUnitRequest.CreativeUnitItem::getUnitId).collect(Collectors.toList());
        List<Long> creativeIds = request.getCreativeunitItems().stream()
                .map(CreativeUnitRequest.CreativeUnitItem::getCreativeId).collect(Collectors.toList());

        if(!isRelatedUnitExist(unitIds) || !isRelatedCreativeExist(creativeIds))
            throw new AdException(Constans.ErrorMsg.REQUEST_PARAM_ERROR);

        List<Long> ids = Collections.emptyList();
        List<CreativeUnit> creativeUnits = new ArrayList<>();
        request.getCreativeunitItems().forEach(i -> creativeUnits.add(
                new CreativeUnit(i.getUnitId(), i.getCreativeId())
        ));
        ids = creativeUnitRepository.saveAll(creativeUnits).stream().map(
                CreativeUnit::getId
        ).collect(Collectors.toList());
        return new CreativeUnitResponse(ids);

    }
}
