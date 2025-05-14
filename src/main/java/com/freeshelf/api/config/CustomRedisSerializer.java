package com.freeshelf.api.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.lang.Nullable;

/**
 * Custom Redis serializer that handles complex entity relationships and circular references. This
 * serializer is designed to work with the entity design improvements for better caching.
 */
public class CustomRedisSerializer implements RedisSerializer<Object> {

  private final GenericJackson2JsonRedisSerializer serializer;

  public CustomRedisSerializer() {
    ObjectMapper objectMapper = new ObjectMapper();

    // Register JavaTimeModule to handle Java 8 date/time types
    objectMapper.registerModule(new JavaTimeModule());

    // Disable writing dates as timestamps
    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    // Configure visibility for all fields
    objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);

    // Enable type information to be stored in the JSON
    objectMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance,
        ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);

    // Create serializer with configured ObjectMapper
    this.serializer = new GenericJackson2JsonRedisSerializer(objectMapper);
  }

  @Override
  public byte[] serialize(@Nullable Object object) throws SerializationException {
    if (object == null) {
      return new byte[0];
    }
    try {
      return serializer.serialize(object);
    } catch (Exception e) {
      // Log the error and return null to prevent cache failures
      System.err.println("Error serializing object: " + e.getMessage());
      return null;
    }
  }

  @Override
  public Object deserialize(@Nullable byte[] bytes) throws SerializationException {
    if (bytes == null || bytes.length == 0) {
      return null;
    }
    try {
      return serializer.deserialize(bytes);
    } catch (Exception e) {
      // Log the error and return null to prevent cache failures
      System.err.println("Error deserializing object: " + e.getMessage());
      return null;
    }
  }
}
