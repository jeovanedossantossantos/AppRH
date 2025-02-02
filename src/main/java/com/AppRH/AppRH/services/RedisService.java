package com.AppRH.AppRH.services;

import com.AppRH.AppRH.dto.PageDTO;
import com.AppRH.AppRH.dto.VagaDTO;
import com.AppRH.AppRH.models.Vaga;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;

import java.util.ArrayList;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import com.fasterxml.jackson.annotation.PropertyAccessor;

@Service
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public RedisService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void saveToRedis(String key, Page<?> value) {

        ObjectMapper objectMapper = new ObjectMapper();
        // redisTemplate.opsForValue().set(key, value);
        try {
            PageDTO<Object> pageDTO = new PageDTO<>(value.getContent(), value.getTotalElements());
            String jsonValue = objectMapper.writeValueAsString(pageDTO);
            redisTemplate.opsForValue().set(key, jsonValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Object getFromRedis(String key, Pageable pageable) {
        ObjectMapper objectMapper = new ObjectMapper();

        String jsonValue = (String) redisTemplate.opsForValue().get(key);

        if (jsonValue != null) {
            try {
                PageDTO<Object> pageDTO = objectMapper.readValue(jsonValue, PageDTO.class);
                return new PageImpl<>(pageDTO.getContent(), pageable, pageDTO.getTotalElements());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return Page.empty();

    }

    public void saveToRedisId(String key, Object value) {
        ObjectMapper objectMapper = new ObjectMapper();

        // Evita a inclusão de @class no JSON
        // objectMapper.activateDefaultTyping(
        // LaissezFaireSubTypeValidator.instance,
        // ObjectMapper.DefaultTyping.NON_FINAL,
        // JsonTypeInfo.As.WRAPPER_ARRAY);
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

        try {

            if (value instanceof VagaDTO) {
                VagaDTO vaga = (VagaDTO) value;
                vaga.setCandidatos(new ArrayList<>(vaga.getCandidatos()));
            }

            String jsonValue = objectMapper.writeValueAsString(value);
            redisTemplate.opsForValue().set(key, jsonValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Object getToRedisId(String key, Class<?> clazz) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, true);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        try {

            String jsonValue = (String) redisTemplate.opsForValue().get(key);

            if (jsonValue == null || jsonValue.isEmpty()) {
                return null;
            }
            return objectMapper.readValue(jsonValue, clazz);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void deleteBy(String key) {
        redisTemplate.opsForValue().getOperations().delete(key);
    }

    public void deleteAllVagasPages(String pattern) {

        Set<String> keys = redisTemplate.keys(pattern);

        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }
}
