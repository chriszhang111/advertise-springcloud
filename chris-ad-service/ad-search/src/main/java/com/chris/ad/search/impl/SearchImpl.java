package com.chris.ad.search.impl;

import com.chris.ad.index.DataTable;
import com.chris.ad.index.adunit.AdUnitIndex;
import com.chris.ad.search.ISearch;
import com.chris.ad.search.vo.SearchRequest;
import com.chris.ad.search.vo.SearchResponse;
import com.chris.ad.search.vo.feature.DistrictFeature;
import com.chris.ad.search.vo.feature.FeatureRelation;
import com.chris.ad.search.vo.feature.ItFeature;
import com.chris.ad.search.vo.feature.KeywordFeature;
import com.chris.ad.search.vo.media.AdSlot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;

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

        }
        return null;
    }
}
