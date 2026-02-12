package com.bogunoz.projects.orchestrator.cache.repository;

import com.bogunoz.projects.orchestrator.cache.config.CacheProperties;
import com.bogunoz.projects.orchestrator.contract.websocket.model.ChatMessage;
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
        return CompletableFuture.runAsync(() ->
                redisTemplate.opsForHash().put(KEY_PREFIX, key, object));
    }

    @Override
    public CompletableFuture<List<T>> getAllAsync() {
        return CompletableFuture.supplyAsync(() ->
                redisTemplate.opsForHash().values(KEY_PREFIX)
                        .stream()
                        .map(o -> (T) o)
                        .collect(Collectors.toList()));
    }
}
