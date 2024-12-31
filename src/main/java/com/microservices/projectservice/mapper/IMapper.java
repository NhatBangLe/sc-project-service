package com.microservices.projectservice.mapper;

public interface IMapper<T, R> {
    R toResponse(T entity);
}
