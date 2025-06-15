package org.example.newjournal.Service;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
@Service
public class RedisService {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    public <T> T get(String key, Class<?> entityClass) {
        try {
            Object o= redisTemplate.opsForValue().get(key);
            ObjectMapper objectMapper = new ObjectMapper();
            return (T) objectMapper.readValue(o.toString(), entityClass);
        } catch (Exception e) {
            return null;
        }
       
    }
    public <T> void set(String key, T value,Long ttl) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonValue = objectMapper.writeValueAsString(value);
            redisTemplate.opsForValue().set(key, jsonValue,ttl,TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
