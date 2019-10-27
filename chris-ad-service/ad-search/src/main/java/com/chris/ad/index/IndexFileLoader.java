package com.chris.ad.index;

import com.alibaba.fastjson.JSON;
import com.chris.ad.dump.DConstant;
import com.chris.ad.dump.table.*;
import com.chris.ad.handler.AdLevelDataHandler;
import com.chris.ad.mysql.constant.OpType;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Component
@DependsOn("dataTable")
public class IndexFileLoader {

    //@PostConstruct
    public void init(){
        List<String> adPlanString = loadDumpData(
                String.format("%s%s", DConstant.DATA_ROOT_DIR, DConstant.AD_PLAN)
        );

        adPlanString.forEach(p-> AdLevelDataHandler.handleLevel2(
                JSON.parseObject(p, AdPlanTable.class), OpType.ADD
        ));

        List<String> creativeStrings = loadDumpData(
                String.format("%s%s", DConstant.DATA_ROOT_DIR, DConstant.AD_CREATIVE)
        );

        creativeStrings.forEach(p->AdLevelDataHandler.handleLevel2(
                JSON.parseObject(p, AdCreativeTable.class), OpType.ADD
        ));

        List<String> adUnitStrings = loadDumpData(
                String.format("%s%s", DConstant.DATA_ROOT_DIR, DConstant.AD_UNIT)
        );

        adUnitStrings.forEach(p->AdLevelDataHandler.handleLevel3(
                JSON.parseObject(p, AdUnitTable.class), OpType.ADD
        ));

        List<String> creativeUnitStrings = loadDumpData(
                String.format("%s%s", DConstant.DATA_ROOT_DIR, DConstant.AD_CREATIVE_UNIT)
        );

        creativeUnitStrings.forEach(cu ->AdLevelDataHandler.handleLevel3(
                JSON.parseObject(cu, AdCreativeUnitTable.class), OpType.ADD
        ));

        List<String> districtStrings = loadDumpData(
                String.format("%s%s", DConstant.DATA_ROOT_DIR, DConstant.AD_UNIT_DISTRICT)
        );

        districtStrings.forEach(d->AdLevelDataHandler.handleLevel4(
                JSON.parseObject(d, AdUnitDistrictTable.class), OpType.ADD
        ));

        List<String> itStrings = loadDumpData(String.format("%s%s", DConstant.DATA_ROOT_DIR, DConstant.AD_UNIT_IT));

        itStrings.forEach(it->AdLevelDataHandler.handleLevel4(
                JSON.parseObject(it, AdUnitItTable.class), OpType.ADD
        ));

        List<String> keywordStrings = loadDumpData(String.format("%s%s", DConstant.DATA_ROOT_DIR, DConstant.AD_UNIT_KEYWORD));

        keywordStrings.forEach(k->AdLevelDataHandler.handleLevel4(
                JSON.parseObject(k, AdUnitKeywordTable.class), OpType.ADD
        ));
    }

    private List<String> loadDumpData(String fileName){
        try(BufferedReader br = Files.newBufferedReader(
              Paths.get(fileName)
        )){

            return br.lines().collect(Collectors.toList());

        }catch (IOException e){
            throw new RuntimeException(e.getMessage());
        }
    }

}
