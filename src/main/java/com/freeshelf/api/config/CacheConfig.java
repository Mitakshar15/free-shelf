package com.freeshelf.api.config;

import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableCaching
public class CacheConfig {

  @Bean
  public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
    // Use our custom Redis serializer for better handling of complex entity relationships
    CustomRedisSerializer customRedisSerializer = new CustomRedisSerializer();

    // Configure Redis cache
    RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
        .entryTtl(Duration.ofHours(24)) // Set default TTL to 24 hours
        .disableCachingNullValues()
        .serializeKeysWith(
            RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
        .serializeValuesWith(
            RedisSerializationContext.SerializationPair.fromSerializer(customRedisSerializer));

    // Build and return the cache manager
    return RedisCacheManager.builder(redisConnectionFactory).cacheDefaults(redisCacheConfiguration)
        .build();
  }

  @Bean
  public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
    return (builder) -> {
      // Configure different TTLs for different cache names
      Map<String, RedisCacheConfiguration> configMap = new HashMap<>();

      // User cache with 1 hour TTL
      configMap.put("user", RedisCacheConfiguration.defaultCacheConfig()
          .entryTtl(Duration.ofHours(1)).disableCachingNullValues());

      // Space cache with 2 hours TTL
      configMap.put("space", RedisCacheConfiguration.defaultCacheConfig()
          .entryTtl(Duration.ofHours(2)).disableCachingNullValues());

      // Address cache with 6 hours TTL
      configMap.put("address", RedisCacheConfiguration.defaultCacheConfig()
          .entryTtl(Duration.ofHours(6)).disableCachingNullValues());

      builder.withInitialCacheConfigurations(configMap);
    };
  }
}
