package com.chris.ad;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@EnableEurekaClient
@SpringBootApplication
public class BinLogKafkaApplication {

    public static void main(String[] args) {

        SpringApplication.run(BinLogKafkaApplication.class, args);
    }
}
