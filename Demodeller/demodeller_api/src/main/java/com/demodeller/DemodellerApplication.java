package com.demodeller;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan
public class DemodellerApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemodellerApplication.class, args);
    }

}