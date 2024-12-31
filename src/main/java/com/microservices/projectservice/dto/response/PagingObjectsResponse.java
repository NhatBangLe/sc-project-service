package com.microservices.projectservice.dto.response;

import org.springframework.lang.NonNull;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public record PagingObjectsResponse<T>(
        Integer totalPages,
        Long totalElements,
        Integer number,
        Integer size,
        Integer numberOfElements,
        Boolean first,
        Boolean last,
        List<T> content
) {

    @NonNull
    public <R> PagingObjectsResponse<R> map(Function<T, R> mapper) {
        Objects.requireNonNull(mapper);
        List<T> currentContent = Objects.requireNonNullElse(this.content, Collections.emptyList());
        List<R> newContent = currentContent.stream().map(mapper).toList();
        return new PagingObjectsResponse<>(
                this.totalPages,
                this.totalElements,
                this.number,
                this.size,
                this.numberOfElements,
                this.first,
                this.last,
                newContent
        );
    }

}
