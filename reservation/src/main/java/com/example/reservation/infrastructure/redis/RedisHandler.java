package com.example.reservation.infrastructure.redis;

import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

//RedisConfig로부터 설정정보를 받아와서 구현하려는 데이터 타입에 따른 객체를 구성하거나 작업 내용에 따른 예외처리를 위한 컴포넌트
@Component
@RequiredArgsConstructor
public class RedisHandler {
    private final RedisConfig redisConfig;

    public ListOperations<String, Object> getListOperations() {
        return redisConfig.getListOperations();
    }

    public ValueOperations<String, Object> getValueOperations() {
        return redisConfig.getValueOperations();
    }

    public int executeOperation(Runnable operation) {
        try {
            operation.run();
            return 1;
        } catch (Exception e) {
            System.out.println("Redis 작업 오류 발생 :: " + e.getMessage());
            return 0;
        }
    }
}
