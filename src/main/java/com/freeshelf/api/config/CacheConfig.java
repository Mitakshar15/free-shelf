package com.freeshelf.api.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisKeyValueAdapter;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.io.IOException;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableRedisRepositories(enableKeyspaceEvents = RedisKeyValueAdapter.EnableKeyspaceEvents.OFF)
@RequiredArgsConstructor
public class CacheConfig {

  private final LettuceConnectionFactory redisConnectionFactory;

  @Bean
  public RedisTemplate<String, Object> redisTemplate() {
    RedisTemplate<String, Object> template = new RedisTemplate<>();
    template.setConnectionFactory(redisConnectionFactory);

    ObjectMapper objectMapper = objectMapper();
    GenericJackson2JsonRedisSerializer serializer =
        new GenericJackson2JsonRedisSerializer(objectMapper);

    template.setKeySerializer(new StringRedisSerializer());
    template.setValueSerializer(serializer);
    template.setHashKeySerializer(new StringRedisSerializer());
    template.setHashValueSerializer(serializer);
    template.afterPropertiesSet();

    return template;
  }

  @Bean
  public ObjectMapper objectMapper() {
    ObjectMapper mapper = new ObjectMapper();

    // Register JavaTimeModule for Java 8 date/time types
    JavaTimeModule javaTimeModule = new JavaTimeModule();

    // Configure serialization for OffsetDateTime
    javaTimeModule.addSerializer(OffsetDateTime.class, new JsonSerializer<OffsetDateTime>() {
      @Override
      public void serialize(OffsetDateTime value, JsonGenerator gen, SerializerProvider serializers)
          throws IOException {
        gen.writeString(value.toString()); // ISO-8601 format
      }
    });

    // Configure deserialization for OffsetDateTime
    javaTimeModule.addDeserializer(OffsetDateTime.class, new JsonDeserializer<OffsetDateTime>() {
      @Override
      public OffsetDateTime deserialize(JsonParser p, DeserializationContext ctxt)
          throws IOException {
        return OffsetDateTime.parse(p.getValueAsString());
      }
    });

    mapper.registerModule(javaTimeModule);

    // Optional: Configure to use ISO-8601 dates by default
    mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

    return mapper;
  }



}
