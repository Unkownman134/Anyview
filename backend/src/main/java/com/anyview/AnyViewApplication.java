package com.anyview;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class AnyViewApplication {
    public static void main(String[] args) {
        SpringApplication.run(AnyViewApplication.class, args);
    }
}
