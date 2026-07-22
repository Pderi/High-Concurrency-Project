package com.hc.ticket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TktServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(TktServerApplication.class, args);
    }
}
