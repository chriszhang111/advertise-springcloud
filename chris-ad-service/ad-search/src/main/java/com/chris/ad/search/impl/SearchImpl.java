package com.chris.ad.search.impl;

import com.alibaba.fastjson.JSON;
import com.chris.ad.index.CommonStatus;
import com.chris.ad.index.DataTable;
import com.chris.ad.index.adunit.AdUnitIndex;
import com.chris.ad.index.adunit.AdUnitObject;
import com.chris.ad.index.creative.CreativeIndex;
import com.chris.ad.index.creative.CreativeObject;
import com.chris.ad.index.creativeunit.CreativeUnitIndex;
import com.chris.ad.index.district.UnitDistrictIndex;
import com.chris.ad.index.interest.UnitItIndex;
import com.chris.ad.index.keyword.UnitKeywordIndex;
import com.chris.ad.search.ISearch;
import com.chris.ad.search.vo.SearchRequest;
import com.chris.ad.search.vo.SearchResponse;
import com.chris.ad.search.vo.feature.DistrictFeature;
import com.chris.ad.search.vo.feature.FeatureRelation;
import com.chris.ad.search.vo.feature.ItFeature;
import com.chris.ad.search.vo.feature.KeywordFeature;
import com.chris.ad.search.vo.media.AdSlot;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class SearchImpl implements ISearch {

    public SearchResponse fallback(SearchRequest request, Throwable e){

        return null;
    }
    @Override
    @HystrixCommand(fallbackMethod = "fallback")
    public SearchResponse fetchAds(SearchRequest request) {

        List<AdSlot> adSlots = request.getRequestInfo().getAdSlots();
        KeywordFeature keywordFeature = request.getFeatureInfo().getKeywordFeature();
        DistrictFeature districtFeature = request.getFeatureInfo().getDistrictFeature();
        ItFeature itFeature = request.getFeatureInfo().getItFeature();
        FeatureRelation relation = request.getFeatureInfo().getRelation();

        //构造响应对象
        SearchResponse response = new SearchResponse();
        Map<String, List<SearchResponse.Creative>> adSlot2Ads = response.getAdSlot2Ads();
        for (AdSlot adSlot : adSlots) {
            Set<Long> targetUnitIdSet;
            Set<Long> adUnitIdSet = DataTable.of(
                    AdUnitIndex.class
            ).match(adSlot.getPositionType());

            if(relation == FeatureRelation.AND){
                filterKeywordFeature(adUnitIdSet, keywordFeature);
                filterDistrictFeature(adUnitIdSet, districtFeature);
                filterItFeature(adUnitIdSet, itFeature);

                targetUnitIdSet = adUnitIdSet;
            }else{
                targetUnitIdSet = getORRelationUnitIds(adUnitIdSet, keywordFeature, districtFeature, itFeature);
            }

            List<AdUnitObject> unitObjects = DataTable.of(AdUnitIndex.class).fetch(targetUnitIdSet);
            filterAdUnitAndPlanStatus(unitObjects, CommonStatus.VALID);
            List<Long> adIds = DataTable.of(CreativeUnitIndex.class).selectAds(unitObjects);
            List<CreativeObject> creatives = DataTable.of(CreativeIndex.class).fetch(adIds);
            //通过adslot实现对creativeobj的过滤
            filterCreativeByAdSlot(creatives,
                    adSlot.getWidth(),
                    adSlot.getHeight(),
                    adSlot.getType());
            adSlot2Ads.put(adSlot.getAdSlotCode(), buildCreativeResponse(creatives));

        }
        log.info("Fetch Ads: {}-{}", JSON.toJSONString(request), JSON.toJSONString(response));
        return response;
    }

    private List<SearchResponse.Creative> buildCreativeResponse(List<CreativeObject> creatives){
        if(CollectionUtils.isEmpty(creatives))
            return Collections.emptyList();
        CreativeObject randomObj = creatives.get(Math.abs(new Random().nextInt() % creatives.size()));
        return Collections.singletonList(
                SearchResponse.convert(randomObj)
        );
    }

    private Set<Long> getORRelationUnitIds(Set<Long> adUnitItSet,
                                           KeywordFeature keywordFeature,
                                           DistrictFeature districtFeature,
                                           ItFeature itFeature){
        if(CollectionUtils.isEmpty(adUnitItSet))
            return Collections.emptySet();
        Set<Long> keywordUnitIdSet = new HashSet<>(adUnitItSet);
        Set<Long> districtUnitIdSet = new HashSet<>(adUnitItSet);
        Set<Long> itUnitIdSet = new HashSet<>(adUnitItSet);

        filterKeywordFeature(keywordUnitIdSet, keywordFeature);
        filterDistrictFeature(districtUnitIdSet, districtFeature);
        filterItFeature(itUnitIdSet, itFeature);

        return new HashSet<>(
                CollectionUtils.union(
                        CollectionUtils.union(keywordUnitIdSet, districtUnitIdSet),
                        itUnitIdSet
                )
        );
    }

    private void filterKeywordFeature(Collection<Long> adUnitIds, KeywordFeature keywordFeature){
        if(CollectionUtils.isEmpty(adUnitIds))
            return;
        if(CollectionUtils.isNotEmpty(keywordFeature.getKeywords())){
            CollectionUtils.filter(
                    adUnitIds,
                    adUnitId -> DataTable.of(UnitKeywordIndex.class).match(adUnitId, keywordFeature.getKeywords())
            );
        }
    }

    private void filterDistrictFeature(Collection<Long> adUnitIds, DistrictFeature districtFeature){
        if(CollectionUtils.isEmpty(adUnitIds))
            return;
        if(CollectionUtils.isNotEmpty(districtFeature.getDistricts())){
            CollectionUtils.filter(
                    adUnitIds,
                    adUnitId -> DataTable.of(UnitDistrictIndex.class).match(adUnitId, districtFeature.getDistricts())
            );
        }
    }

    private void filterItFeature(Collection<Long> adUnitIds, ItFeature itFeature){
        if(CollectionUtils.isEmpty(adUnitIds))
            return;
        if(CollectionUtils.isNotEmpty(itFeature.getIts())){
            CollectionUtils.filter(
                    adUnitIds,
                    adUnitId -> DataTable.of(UnitItIndex.class).match(adUnitId, itFeature.getIts())
            );
        }
    }

    private void filterAdUnitAndPlanStatus(List<AdUnitObject> unitObjects,
                                           CommonStatus status){
        if(CollectionUtils.isEmpty(unitObjects))
            return;
        CollectionUtils.filter(
                unitObjects,
                unitObject->unitObject.getUnitStatus().equals(status.getStatus()) &&
                        unitObject.getAdPlanObject().getPlanStatus().equals(status.getStatus())
        );
    }

    private void filterCreativeByAdSlot(List<CreativeObject> creativeObjects,
                                        Integer width,
                                        Integer height,
                                        List<Integer> type){
        if(CollectionUtils.isEmpty(creativeObjects))
            return;
        CollectionUtils.filter(creativeObjects,
                creative->
        creative.getAuditStatus().equals(CommonStatus.VALID.getStatus())
        && creative.getWidth().equals(width)
        && creative.getHeight().equals(height)
        && type.contains(creative.getType()));

    }
}
