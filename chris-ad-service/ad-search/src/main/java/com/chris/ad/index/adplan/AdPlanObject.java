package com.chris.ad.index.adplan;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdPlanObject {

    private Long planId;
    private Long userId;
    private Integer planStatus;
    private Date startDate;
    private Date endDate;

    public void update(AdPlanObject newobj){
        if(null != newobj.getPlanId()){
            this.planId = newobj.getPlanId();
        }
        if(null != newobj.getUserId()){
            this.userId = newobj.getUserId();
        }
        if(null != newobj.getPlanStatus()){
            this.planStatus = newobj.getPlanStatus();
        }
        if(null != newobj.getStartDate()){
            this.startDate = newobj.getStartDate();
        }
        if(null != newobj.getEndDate()){
            this.endDate = newobj.getEndDate();
        }
    }
}
