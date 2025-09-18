package com.struggle;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Indexed;

/**
 * 启动类
 * Created by Struggle on 2018-02-22 11:29:03
 */
@EnableAsync
@MapperScan("com.struggle.**.mapper")
@SpringBootApplication
@EnableCaching
@Indexed
public class StruggleFrameworkApplication {

    public static void main(String[] args) {
        SpringApplication.run(StruggleFrameworkApplication.class, args);
    }
}
