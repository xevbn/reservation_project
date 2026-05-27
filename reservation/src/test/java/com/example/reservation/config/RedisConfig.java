package com.example.reservation.config;

import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import redis.embedded.RedisServer;

@Configuration
public class RedisConfig {
  private RedisServer redisServer;

  @PostConstruct
  public void startRedis() {
    redisServer = RedisServer.builder()
      .port(6379)
      .setting("maxmemory 128M")
      .build();
    redisServer.start();
  }

  @PreDestroy
  public void stopRedis() {
    if(!redisServer.isActive()) {
      redisServer.stop();
    }
  }
}
