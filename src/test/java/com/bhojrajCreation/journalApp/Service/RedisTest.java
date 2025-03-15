package com.bhojrajCreation.journalApp.Service;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest
@Disabled
public class RedisTest {
    @Autowired
    private RedisTemplate redisTemplate;

    public RedisTest() {
        super();
    }

    @Test
    public void sendMail(){
        redisTemplate.opsForValue().set("email","radhe@gmail.com");
        Object email = redisTemplate.opsForValue().get("salary");
        int a=1;
    }
}
