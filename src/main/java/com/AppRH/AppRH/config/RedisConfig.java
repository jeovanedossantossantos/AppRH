package com.AppRH.AppRH.config;

import static io.lettuce.core.ReadFrom.REPLICA_PREFERRED;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import java.time.Duration;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableConfigurationProperties(RedisProperties.class)
public class RedisConfig {

    @Bean
    LettuceConnectionFactory redisConnectionFactory() {
        var clientConfig = LettuceClientConfiguration.builder().readFrom(REPLICA_PREFERRED).build();

        return new LettuceConnectionFactory(
                new RedisStandaloneConfiguration("redis_rh", 6379), clientConfig);
    }

    @Bean
    RedisCacheManager cacheManager() {
        return RedisCacheManager.builder(this.redisConnectionFactory())
                .cacheDefaults(this.cacheConfiguration())
                .build();
    }

    @Bean
    RedisCacheConfiguration cacheConfiguration() {
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofSeconds(600000))
                .disableCachingNullValues()
                .serializeValuesWith(
                        SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));
    }

    @Bean
    RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        var template = new RedisTemplate<String, Object>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setDefaultSerializer(new JdkSerializationRedisSerializer());

        return template;
    }
}
