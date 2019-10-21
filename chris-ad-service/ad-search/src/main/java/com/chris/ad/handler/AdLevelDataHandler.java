package com.chris.ad.handler;

import com.chris.ad.dump.table.AdCreativeTable;
import com.chris.ad.dump.table.AdPlanTable;
import com.chris.ad.index.DataTable;
import com.chris.ad.index.IndexAware;
import com.chris.ad.index.adplan.AdPlanIndex;
import com.chris.ad.index.adplan.AdPlanObject;
import com.chris.ad.index.creative.CreativeIndex;
import com.chris.ad.index.creative.CreativeObject;
import com.chris.ad.mysql.constant.OpType;
import lombok.extern.slf4j.Slf4j;

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
