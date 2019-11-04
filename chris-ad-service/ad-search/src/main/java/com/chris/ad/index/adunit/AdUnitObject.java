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

    private static boolean isKaiPing(int positionType){
        return (positionType & AdUnitConstants.POSITION_TYPE.KAIPING) > 0;
    }

    private static boolean isTiePian(int positionType){
        return (positionType & AdUnitConstants.POSITION_TYPE.TIEPIAN) > 0;
    }

    private static boolean isTiePianMiddle(int positionType) {
        return (positionType & AdUnitConstants.POSITION_TYPE.MIDDLE) > 0;
    }

    private static boolean isTiePianPause(int positionType) {
        return (positionType & AdUnitConstants.POSITION_TYPE.PAUSE) > 0;
    }

    private static boolean isTiePianPost(int positionType) {
        return (positionType & AdUnitConstants.POSITION_TYPE.POST) > 0;
    }

    public static boolean isAdSlotTypeOK(int adSlotType, int positionType) {

        switch (adSlotType) {
            case AdUnitConstants.POSITION_TYPE.KAIPING:
                return isKaiPing(positionType);
            case AdUnitConstants.POSITION_TYPE.TIEPIAN:
                return isTiePian(positionType);
            case AdUnitConstants.POSITION_TYPE.MIDDLE:
                return isTiePianMiddle(positionType);
            case AdUnitConstants.POSITION_TYPE.PAUSE:
                return isTiePianPause(positionType);
            case AdUnitConstants.POSITION_TYPE.POST:
                return isTiePianPost(positionType);
            default:
                return false;
        }
    }
}
