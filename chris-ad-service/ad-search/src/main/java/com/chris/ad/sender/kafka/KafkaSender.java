package com.chris.ad.sender.kafka;

import com.alibaba.fastjson.JSON;
import com.chris.ad.mysql.dto.MySqlRowData;
import com.chris.ad.sender.ISender;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component("kafkaSender")
public class KafkaSender implements ISender {

    @Value("${adconf.kafka.topic}")
    private String topic;

    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    public KafkaSender(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void sender(MySqlRowData rowData) {
        kafkaTemplate.send(
                topic,
                JSON.toJSONString(rowData)
        );
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
            System.out.println("kafka processMySQLRowData");
        }

    }
}
