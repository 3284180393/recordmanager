package com.channelsoft.ccod.recordmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;

@SpringBootApplication(exclude = MongoAutoConfiguration.class)
public class RecordmanagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(RecordmanagerApplication.class, args);
    }

}
