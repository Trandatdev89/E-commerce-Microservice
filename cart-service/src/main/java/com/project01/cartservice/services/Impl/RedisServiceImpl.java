package com.project01.cartservice.services.Impl;

import com.project01.cartservice.services.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;


@Service
public class RedisServiceImpl implements RedisService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;


    @Override
    public void saveData(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    @Override
    public <T> T getData(String key) {
        return (T)redisTemplate.opsForValue().get(key);
    }

    @Override
    public void deleteData(String key) {
        redisTemplate.delete(key);
    }

    @Override
    public void saveDataOnTime(String key, Object value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value,timeout, unit);
    }

    @Override
    public boolean existsKey(String key) {
        return redisTemplate.hasKey(key);
    }
}
