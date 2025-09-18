package com.struggle;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Indexed;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 启动类
 * Created by struggle on 2018-02-22 11:29:03
 */
@EnableAsync
@EnableTransactionManagement
@MapperScan("com.struggle.**.mapper")
@SpringBootApplication
@Indexed
public class StruggleFrontApplication {

    public static void main(String[] args) {
        SpringApplication.run(StruggleFrontApplication.class, args);
    }

}
