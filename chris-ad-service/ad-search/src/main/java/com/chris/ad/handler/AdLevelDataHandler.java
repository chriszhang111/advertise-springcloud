package com.chris.ad.handler;

import com.alibaba.fastjson.JSON;
import com.chris.ad.dump.table.*;
import com.chris.ad.index.DataTable;
import com.chris.ad.index.IndexAware;
import com.chris.ad.index.adplan.AdPlanIndex;
import com.chris.ad.index.adplan.AdPlanObject;
import com.chris.ad.index.adunit.AdUnitIndex;
import com.chris.ad.index.adunit.AdUnitObject;
import com.chris.ad.index.creative.CreativeIndex;
import com.chris.ad.index.creative.CreativeObject;
import com.chris.ad.index.creativeunit.CreativeUnitIndex;
import com.chris.ad.index.creativeunit.CreativeUnitObject;
import com.chris.ad.index.district.UnitDistrictIndex;
import com.chris.ad.index.interest.UnitItIndex;
import com.chris.ad.index.keyword.UnitKeywordIndex;
import com.chris.ad.mysql.constant.OpType;
import com.chris.ad.utils.CommonUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Slf4j
public class AdLevelDataHandler {

    public static void handleLevel2(AdPlanTable planTable, OpType type){
        AdPlanObject planObject = new AdPlanObject(
                planTable.getId(), planTable.getUserId(),
                planTable.getPlanStatus(), planTable.getStartDate(),
                planTable.getEndDate()
        );

        handleBinlogEvent(
                DataTable.of(AdPlanIndex.class),
                planObject.getPlanId(),
                planObject,
                type
        );
    }

    public static void handleLevel2(AdCreativeTable creativeTable, OpType type){
        CreativeObject creativeObject = new CreativeObject(
                creativeTable.getAdId(), creativeTable.getName(),
                creativeTable.getType(), creativeTable.getMaterialType(),
                creativeTable.getHeight(),creativeTable.getWidth(),
                creativeTable.getAuditStatus(),creativeTable.getAdUrl()
        );

        handleBinlogEvent(
                DataTable.of(CreativeIndex.class),
                creativeObject.getAdId(),
                creativeObject,
                type
        );
    }

    public static void handleLevel3(AdUnitTable unitTable,
                                    OpType type){
        AdPlanObject planObject = DataTable.of(
                AdPlanIndex.class
        ).get(unitTable.getPlanId());

        if(planObject == null){
            log.error("handleLevel3 found adplanobject error");
            return;
        }

        AdUnitObject unitObject = new AdUnitObject(
                unitTable.getUnitId(),
                unitTable.getUnitStatus(),
                unitTable.getPositionType(),
                unitTable.getPlanId(),
                planObject
        );

        handleBinlogEvent(
                DataTable.of(AdUnitIndex.class),
                unitTable.getUnitId(),
                unitObject,
                type
        );

    }

    public static void handleLevel3(AdCreativeUnitTable creativeUnitTable,
                                    OpType opType){
        if(opType == OpType.UPDATE){
            log.error("Creative Unit index not support update");
            return;
        }

        AdUnitObject unitObject = DataTable.of(
                AdUnitIndex.class
        ).get(creativeUnitTable.getUnitId());

        CreativeObject creativeObject = DataTable.of(
                CreativeIndex.class
        ).get(creativeUnitTable.getAdId());

        if(unitObject == null || creativeObject == null){
            log.error("unitObj or creativeObj does not exist:{}", JSON.toJSONString(creativeUnitTable));
            return;
        }

        CreativeUnitObject creativeUnitObject = new CreativeUnitObject(
                creativeUnitTable.getAdId(),
                creativeUnitTable.getUnitId()
        );

        handleBinlogEvent(
                DataTable.of(CreativeUnitIndex.class),
                CommonUtils.stringConcat(creativeUnitTable.getAdId().toString(), creativeUnitObject.getUnitId().toString()),
                creativeUnitObject,
                opType
        );
    }

    public static void handleLevel4(AdUnitDistrictTable districtTable,
                                    OpType type){
        if(type == OpType.UPDATE){
            log.error("district index can not support update");
            return;
        }

        AdUnitObject unitObject = DataTable.of(
                AdUnitIndex.class
        ).get(districtTable.getUnitId());
        if(unitObject == null){
            log.error("district table index error");
            return;
        }

        String key = CommonUtils.stringConcat(districtTable.getProvince(), districtTable.getCity());
        Set<Long> value = new HashSet<>(
                Collections.singleton(districtTable.getUnitId())
        );

        handleBinlogEvent(
                DataTable.of(UnitDistrictIndex.class),
                key,value, type
        );
    }

    public static void handleLevel4(AdUnitItTable itTable, OpType type){
        if(type == OpType.UPDATE){
            log.error("IT index can'' support update");
            return;
        }

        AdUnitObject unitObject = DataTable.of(AdUnitIndex.class).get(itTable.getUnitId());
        if(unitObject == null){
            return;
        }

        Set<Long> value = new HashSet<>(Collections.singleton(itTable.getUnitId()));
        handleBinlogEvent(
                DataTable.of(UnitItIndex.class),
                itTable.getItTag(),
                value,
                type
        );
    }

    public static void handleLevel4(AdUnitKeywordTable keywordTable, OpType opType){
        if(opType == OpType.UPDATE){
            log.error("keyword index does not support update");
            return;
        }

        AdUnitObject unitObject = DataTable.of(AdUnitIndex.class).get(keywordTable.getUnitId());
        if(unitObject == null){
            log.error("keyword index error");
            return;
        }

        Set<Long> value = new HashSet<>(Collections.singleton(keywordTable.getUnitId()));
        handleBinlogEvent(DataTable.of(UnitKeywordIndex.class),
                keywordTable.getKeyword(),
                value,
                opType);
    }

    private static <K, V> void handleBinlogEvent(IndexAware<K, V> index,
                                                 K key, V value,
                                                 OpType type){
        switch (type){
            case ADD:
                index.add(key, value);
                break;
            case DELETE:
                index.delete(key, value);
                break;
            case UPDATE:
                index.update(key, value);
                break;
            default:
                break;
        }
    }
}
