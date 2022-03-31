package com.cothis.batchtransaction;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableBatchProcessing
public class BatchtransactionApplication {

    public static void main(String[] args) {
        SpringApplication.run(BatchtransactionApplication.class, args);
    }

}
