package com.chris.ad.search.vo;

import com.chris.ad.index.creative.CreativeObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchResponse {

    public Map<String, List<Creative>> adSlot2Ads = new HashMap<>();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Creative{
        private Long id;
        private String adurl;
        private Integer width;
        private  Integer height;
        private Integer type;
        private Integer materialType;

        private List<String> showMonitorUrl = Arrays.asList("www.imooc.com",
                "www.imooc.com");

        private List<String> clickMonitorUrl = Arrays.asList("www.imooc.com",
                "www.imooc.com");
    }

    public static Creative convert(CreativeObject object){
        Creative creative = new Creative();
        creative.setId(object.getAdId());
        creative.setAdurl(object.getAdUrl());
        creative.setWidth(object.getWidth());
        creative.setHeight(object.getHeight());
        creative.setType(object.getType());
        creative.setMaterialType(object.getMaterialType());
        return creative;
    }
}
