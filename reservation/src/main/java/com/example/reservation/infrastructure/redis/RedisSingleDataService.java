package com.example.reservation.infrastructure.redis;

import java.time.Duration;

import org.springframework.stereotype.Service;


//레디스에서 싱글 데이터에 관한 서비스 인터페이스
@Service
public interface RedisSingleDataService {
    int setSingleData(String key, Object value);
    int setSingleData(String key, Object value, Duration duration);
    String getSingleData(String key);
    int deleteSingleData(String key);
    boolean isEmpty();
}
