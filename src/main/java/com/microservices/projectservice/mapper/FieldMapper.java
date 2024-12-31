package com.microservices.projectservice.mapper;

import com.microservices.projectservice.dto.response.FieldResponse;
import com.microservices.projectservice.entity.Field;

public class FieldMapper implements IMapper<Field, FieldResponse> {

    @Override
    public FieldResponse toResponse(Field entity) {
        return new FieldResponse(
                entity.getId(),
                entity.getNumberOrder(),
                entity.getName(),
                entity.getCreatedAt().getTime(),
                entity.getForm().getId()
        );
    }

}
