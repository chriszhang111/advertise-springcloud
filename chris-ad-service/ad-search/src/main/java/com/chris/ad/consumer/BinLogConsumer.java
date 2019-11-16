package com.chris.ad.consumer;

import com.alibaba.fastjson.JSON;
import com.chris.ad.dto.MySqlRowData;
import com.chris.ad.sender.ISender;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
public class BinLogConsumer {

    private final ISender sender;

    @Autowired
    public BinLogConsumer(ISender sender) {
        this.sender = sender;
    }

    @KafkaListener(topics = {"ad-search-mysql-data"}, groupId = "ad-search")
    public void processMySqlRowData(ConsumerRecord<?, ?> record){
        Optional<?> kafkaMessage = Optional.ofNullable(record.value());
        if(kafkaMessage.isPresent()){
            Object message = kafkaMessage.get();
            MySqlRowData rowData = JSON.parseObject(
                    message.toString(),
                    MySqlRowData.class
            );

            log.info("kafka processMysqlRowData: {}", JSON.toJSONString(rowData));
            sender.sender(rowData);
        }
    }
}
