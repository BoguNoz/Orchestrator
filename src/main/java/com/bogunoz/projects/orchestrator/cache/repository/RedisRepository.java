package com.bogunoz.projects.orchestrator.cache.repository;

import com.bogunoz.projects.orchestrator.cache.config.CacheProperties;
import org.apache.tomcat.util.buf.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Repository
public class RedisRepository<T> implements CacheRepository<T> {

    private final CacheProperties props;
    private final RedisTemplate<String, Object> redisTemplate;
    private static String KEY_PREFIX;

    public RedisRepository(CacheProperties props, RedisTemplate<String, Object> redisTemplate) {
        this.props = props;
        this.redisTemplate = redisTemplate;
        KEY_PREFIX = props.getPrefix();
    }

    @Override
    public CompletableFuture<Void> addAsync(T object, String key) {
        return CompletableFuture.runAsync(() -> {

            var messageKey = java.util.UUID.randomUUID().toString();
            var redisKey = KEY_PREFIX + key + messageKey;

            redisTemplate.opsForHash().put(redisKey, messageKey, object);
        });
    }

    @Override
    public CompletableFuture<List<T>> getAllAsync(String key) {
        return CompletableFuture.supplyAsync(() -> {
            String redisKeyPattern = KEY_PREFIX + key + "*";

            var matchingKeys = redisTemplate.keys(redisKeyPattern);
            if (matchingKeys == null || matchingKeys.isEmpty()) {
                return List.<T>of();
            }

            return matchingKeys.stream()
                    .flatMap(k -> redisTemplate.opsForHash().values(k).stream())
                    .map(o -> (T) o)
                    .collect(Collectors.toList());
        });
    }
}
