package com.chris.ad.search.vo;

import com.chris.ad.search.vo.feature.DistrictFeature;
import com.chris.ad.search.vo.feature.FeatureRelation;
import com.chris.ad.search.vo.feature.ItFeature;
import com.chris.ad.search.vo.feature.KeywordFeature;
import com.chris.ad.search.vo.media.AdSlot;
import com.chris.ad.search.vo.media.App;
import com.chris.ad.search.vo.media.Device;
import com.chris.ad.search.vo.media.Geo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchRequest {

    private String mediaId;

    private RequestInfo requestInfo;

    private FeatureInfo featureInfo;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RequestInfo{

        private String requestId;
        private List<AdSlot> adSlots;
        private App app;
        private Geo geo;
        private Device device;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FeatureInfo{

        private KeywordFeature keywordFeature;
        private DistrictFeature districtFeature;
        private ItFeature itFeature;
        private FeatureRelation relation = FeatureRelation.AND;
    }
}
