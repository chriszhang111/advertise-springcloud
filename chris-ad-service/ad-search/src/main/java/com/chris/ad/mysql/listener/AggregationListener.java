package com.chris.ad.mysql.listener;

import com.chris.ad.mysql.TemplateHolder;
import com.chris.ad.mysql.dto.BinlogRowData;
import com.chris.ad.mysql.dto.TableTemplate;
import com.github.shyiko.mysql.binlog.BinaryLogClient;
import com.github.shyiko.mysql.binlog.event.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class AggregationListener implements BinaryLogClient.EventListener{

    private String dbName;
    private String tableName;
    private Map<String, Ilistener> listenerMap = new HashMap<>();
    private TemplateHolder templateHolder;

    @Autowired
    public AggregationListener(TemplateHolder templateHolder) {
        this.templateHolder = templateHolder;
    }

    private String genKey(String dbName, String tableName){
        return dbName + ":" + tableName;
    }

    public void register(String _dbName, String _tableName,
                         Ilistener listener){
        log.info("register :{}-{}", _dbName, _tableName);
        String key = genKey(_dbName, _tableName);
        this.listenerMap.put(key, listener);
    }

    @Override
    public void onEvent(Event event) {

        EventType type = event.getHeader().getEventType();
        log.debug("event type:{}", type);

        if(type == EventType.TABLE_MAP){
            TableMapEventData data = event.getData();
            this.tableName = data.getTable();
            this.dbName = data.getDatabase();
            return;
        }

        if(type != EventType.EXT_UPDATE_ROWS &&
        type != EventType.EXT_DELETE_ROWS &&
                type != EventType.EXT_WRITE_ROWS){
            return;
        }

        //数据库表名，库名是否填充
        if(StringUtils.isEmpty(dbName) || StringUtils.isEmpty(tableName)){
            log.error("No meta data event");
            return;
        }

        //找出对应表有兴趣的监听器
        String key = genKey(this.dbName, this.tableName);
        Ilistener ilistener = this.listenerMap.get(key);
        if(ilistener == null){
            log.debug("skip {}", key);
            return;
        }

        log.info("trigger event : {}", type.name());

        try{

            BinlogRowData rowData = buildRowData(event.getData());
            if(rowData == null){
                return;
            }

            rowData.setEventType(type);
            ilistener.onEvent(rowData);

        }catch (Exception ex){
            ex.printStackTrace();
            log.error(ex.getMessage());
        }finally {
            this.dbName = "";
            this.tableName = "";
        }
    }

    private List<Serializable[]> getAfterValues(EventData eventData){
        if(eventData instanceof WriteRowsEventData){
            return ((WriteRowsEventData) eventData).getRows();
        }

        if(eventData instanceof UpdateRowsEventData){
            return ((UpdateRowsEventData) eventData).getRows().stream().map(Map.Entry::getValue).collect(Collectors.toList());
        }

        if(eventData instanceof DeleteRowsEventData){
            return ((DeleteRowsEventData) eventData).getRows();
        }

        return Collections.emptyList();
    }

    private BinlogRowData buildRowData(EventData eventData){
        TableTemplate table = templateHolder.getTable(tableName);
        if(table == null){
            log.warn("table {} not found", tableName);
        }

        List<Map<String, String>> afterMapList = new ArrayList<>();

        for (Serializable[] after : getAfterValues(eventData)) {
            Map<String, String> afterMap = new HashMap<>();

            int colLen = after.length;

            for(int ix=0;ix<colLen;++ix){
                //取出当前位置列名
                String colName = table.getPosMap().get(ix);
                if(colName == null)
                    continue;
                String colValue = after[ix].toString();
                afterMap.put(colName, colValue);
            }
            afterMapList.add(afterMap);

        }

        BinlogRowData rowData = new BinlogRowData();
        rowData.setAfter(afterMapList);
        rowData.setTable(table);
        return rowData;
    }
}
