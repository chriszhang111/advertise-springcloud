package com.chris.ad.mysql.listener;

import com.chris.ad.mysql.constant.Constant;
import com.chris.ad.mysql.constant.OpType;
import com.chris.ad.mysql.dto.BinlogRowData;
import com.chris.ad.mysql.dto.MySqlRowData;
import com.chris.ad.mysql.dto.TableTemplate;
import com.chris.ad.sender.ISender;
import com.github.shyiko.mysql.binlog.event.EventType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class IncrementListener implements Ilistener {

    private AggregationListener aggregationListener;

    @Resource(name = "")
    private ISender sender;

    @Autowired
    public IncrementListener(AggregationListener aggregationListener) {
        this.aggregationListener = aggregationListener;
    }

    @Override
    @PostConstruct
    public void register() {
        log.info("IncrementListener register db and table info");
        Constant.table2Db.forEach((k,v) ->
        aggregationListener.register(v, k, this));
    }

    @Override
    public void onEvent(BinlogRowData eventData) {
        TableTemplate table = eventData.getTable();
        EventType eventType = eventData.getEventType();
        MySqlRowData rowData = new MySqlRowData();
        rowData.setTableName(table.getTableName());
        rowData.setLevel(table.getLevel());

        OpType optype = OpType.to(eventType);
        rowData.setOpType(optype);

        //取出模版中改造错对应的字段列表
        List<String> fieldList = table.getOpTypeFieldSetMap().get(optype);
        if(fieldList == null){
            return;
        }

        for(Map<String, String> afterMap: eventData.getAfter()){

            Map<String, String> _afterMap = new HashMap<>();
            for (Map.Entry<String, String> entry : afterMap.entrySet()) {
                String colName = entry.getKey();
                String colValue = entry.getValue();

                _afterMap.put(colName, colValue);
            }

            rowData.getFieldValueMap().add(_afterMap);

        }

        sender.sender(rowData);
    }
}
