package com.chris.ad.index.adunit;

import com.chris.ad.index.adplan.AdPlanObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdUnitObject {

    private Long unitId;
    private Integer unitStatus;
    private Integer positionType;
    private Long planId;

    private AdPlanObject adPlanObject;

    public void update(AdUnitObject newobj){
        if(null != newobj.getUnitId())
            this.unitId = newobj.getUnitId();
        if(null != newobj.getPlanId())
            this.planId = newobj.getPlanId();
        if(null != newobj.getPositionType())
            this.positionType = newobj.getPositionType();
        if(null != newobj.getUnitStatus())
            this.unitStatus = newobj.getUnitStatus();
        if(null != newobj.getAdPlanObject())
            this.adPlanObject = newobj.getAdPlanObject();
    }
}
