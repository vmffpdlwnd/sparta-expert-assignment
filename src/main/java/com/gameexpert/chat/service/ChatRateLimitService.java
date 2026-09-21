package com.gameexpert.chat.service;

import java.util.List;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatRateLimitService {

    // Lv 19: 횟수 확인 + 중가 + 최초 만료 설정을 원자적으로 처리
    private final RedisScript<Long> ALLOW_SCRIPT = new DefaultRedisScript<>("""
            local current = tonumber(redis.call('GET', KEYS[1]) or '0')
            if current >= tonumber(ARGV[1]) then
                return 0
            end
            local update = redis.call('INCR', KEYS[1])
            if update == 1 then
                redis.call('EXPIRE', KEYS[1], ARGV[2])
            end
            return 1
            """, long.class);

    private final StringRedisTemplate redisTemplate;

    public boolean allow(Long playerId) {
        String key = "chat:limit:" + playerId;
        Long allowed = redisTemplate.execute(ALLOW_SCRIPT, List.of(key), "5", "10");
        return allowed != null && allowed == 1L;
    }
}
