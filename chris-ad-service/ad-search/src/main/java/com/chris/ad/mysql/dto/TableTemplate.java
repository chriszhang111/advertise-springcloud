package com.chris.ad.mysql.dto;

import com.chris.ad.mysql.constant.OpType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TableTemplate {

    private String tableName;
    private String level;

    /**
     * Example: OPType:ADD -> List<String>:{"id", "user_id", "plan_status", "start_date"...}
     */
    private Map<OpType, List<String>> opTypeFieldSetMap = new HashMap<>();

    //字段索引 -> 字段名
    private Map<Integer, String> posMap = new HashMap<>();
}
