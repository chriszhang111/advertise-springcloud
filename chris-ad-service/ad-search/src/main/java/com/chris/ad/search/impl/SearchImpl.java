package com.chris.ad.search.impl;

import com.chris.ad.index.DataTable;
import com.chris.ad.index.adunit.AdUnitIndex;
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
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class SearchImpl implements ISearch {

    @Override
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

        }
        return null;
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
}
