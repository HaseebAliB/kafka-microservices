package com.kafka.microservices.productservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@Profile("k8s")
public class RedisConfig {
// Configuration details for Redis would go here

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Set key serializer
        template.setKeySerializer(new StringRedisSerializer());
       // template.setHashKeySerializer(new StringRedisSerializer());

        // Set value serializer
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        //template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());

        // Set default TTL (Time-To-Live) for cache entries
        template.afterPropertiesSet();

        return template;
    }


}
