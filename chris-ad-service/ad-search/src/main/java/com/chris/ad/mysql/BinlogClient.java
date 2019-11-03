package com.chris.ad.mysql;

import com.chris.ad.mysql.listener.AggregationListener;
import com.github.shyiko.mysql.binlog.BinaryLogClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class BinlogClient {

    private BinaryLogClient client;

    private BinLogConfig config;

    private AggregationListener listener;

    @Autowired
    public BinlogClient(BinLogConfig config, AggregationListener listener) {
        this.config = config;
        this.listener = listener;
    }

    public void connect(){
        new Thread(() -> {
            client = new BinaryLogClient(
                    config.getHost(),
                    config.getPort(),
                    config.getUserName(),
                    config.getPassword()
            );

            if(!StringUtils.isEmpty(config.getBinlogName()) &&
                    !config.getPosition().equals(-1L)){
                client.setBinlogFilename(config.getBinlogName());
                client.setBinlogPosition(config.getPosition());
            }

            client.registerEventListener(listener);
            try{
                log.info("Connecting to mysql start");
                client.connect();
                log.info("connecting to mysql done");
            }catch (IOException ex){
                ex.printStackTrace();
            }
        }).start();
    }

    public void close(){
        try{
            client.disconnect();
        }catch (IOException ex){
            ex.printStackTrace();
        }
    }
}
