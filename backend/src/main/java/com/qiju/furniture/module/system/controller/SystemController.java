package com.qiju.furniture.module.system.controller;

import com.qiju.furniture.common.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 系统健康检查 Controller
 */
@RestController
@RequestMapping("/api/system")
public class SystemController {

    @Autowired(required = false)
    private StringRedisTemplate redisTemplate;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/health")
    public Result<Map<String, Object>> health() {
        Map<String, Object> info = new HashMap<>();
        info.put("status", "UP");
        info.put("timestamp", LocalDateTime.now().toString());
        info.put("service", "qiju-furniture");
        info.put("version", "1.0.0");

        // Check database
        try {
            jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            info.put("database", "UP");
        } catch (Exception e) {
            info.put("database", "DOWN: " + e.getMessage());
        }

        // Check Redis
        if (redisTemplate != null) {
            try {
                redisTemplate.opsForValue().set("health:ping", "ok", 5, TimeUnit.SECONDS);
                String pong = redisTemplate.opsForValue().get("health:ping");
                info.put("redis", "UP".equals(pong) ? "UP" : "DOWN");
            } catch (Exception e) {
                info.put("redis", "DOWN: " + e.getMessage());
            }
        } else {
            info.put("redis", "NOT_CONFIGURED");
        }

        return Result.ok(info);
    }

    @GetMapping("/stats")
    public Result<Map<String, Object>> stats() {
        Map<String, Object> stats = new HashMap<>();

        try {
            Integer productCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM product WHERE status = 1", Integer.class);
            Integer userCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM user", Integer.class);
            Integer orderCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM orders", Integer.class);
            Integer reviewCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM review", Integer.class);

            stats.put("productCount", productCount);
            stats.put("userCount", userCount);
            stats.put("orderCount", orderCount);
            stats.put("reviewCount", reviewCount);
        } catch (Exception e) {
            stats.put("error", e.getMessage());
        }

        return Result.ok(stats);
    }
}
