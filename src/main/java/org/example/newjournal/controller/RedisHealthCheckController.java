package org.example.newjournal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class RedisHealthCheckController {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @GetMapping("/check-redis")
    public ResponseEntity<String> checkRedis() {
        try {
            redisTemplate.opsForValue().set("ping", "pong");
            String value = redisTemplate.opsForValue().get("ping");
            return ResponseEntity.ok("Redis connected successfully. Value: " + value);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Redis connection failed: " + e.getMessage());
        }
    }
}

