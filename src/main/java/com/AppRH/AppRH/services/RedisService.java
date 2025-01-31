package com.AppRH.AppRH.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.redis.core.RedisTemplate;

@Service
public class RedisService {

    // Injetando o RedisTemplate
    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public RedisService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // Função para salvar informação no Redis
    public void saveToRedis(String key, Object value) {
        // Armazena o valor no Redis com a chave especificada
        redisTemplate.opsForValue().set(key, value);
    }

    // Função para recuperar informação do Redis
    public Object getFromRedis(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public void deleteBy(String key) {
        redisTemplate.opsForValue().getOperations().delete(key);
    }
}
