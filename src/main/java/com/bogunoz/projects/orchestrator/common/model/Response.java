package com.bogunoz.projects.orchestrator.common.model;

import lombok.Getter;
import org.springframework.modulith.NamedInterface;

import java.util.List;

@Getter
@NamedInterface
public class Response<T> {

    private final T data;
    private final List<String> errors;

    public Response(T data, List<String> errors) {
        this.data = data;
        this.errors = errors;
    }

    public static <T> Response<T> ok(T data) {
        return new Response<>(data, List.of());
    }

    public static <T> Response<T> error(String... errors) {
        return new Response<>(null, List.of(errors));
    }
}
