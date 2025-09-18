package com.struggle.common.core.utils;


import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class RedissionUtil {

    @Value("${spring.redis.host}")
    private String redisHost;

    @Value("${spring.redis.password}")
    private String password;

    @Value("${spring.redis.port}")
    private String port;

    @Bean
    public RedissonClient getRedisson() {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://" + redisHost + ":" + port).setPassword(password);
        config.setCodec(new JsonJacksonCodec());
        return Redisson.create(config);
    }
}
