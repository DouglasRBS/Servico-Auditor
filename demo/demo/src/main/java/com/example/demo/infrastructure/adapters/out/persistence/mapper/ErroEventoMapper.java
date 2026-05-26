package com.example.demo.infrastructure.adapters.out.persistence.mapper;

import org.springframework.stereotype.Component;

import com.example.demo.domain.model.ErroEvento;
import com.example.demo.infrastructure.adapters.out.persistence.entity.ErroEventoEntity;

@Component
public class ErroEventoMapper {

    public ErroEventoEntity toEntity(ErroEvento domain) {
        ErroEventoEntity entity = new ErroEventoEntity();
        entity.setErrorId(domain.getErrorId());
        entity.setQueueName(domain.getQueueName());
        entity.setPayload(domain.getPayload());
        entity.setTimestamp(domain.getTimestamp());
        entity.setStatus(domain.getStatus());
        entity.setSeverity(domain.getSeverity());
        return entity;
    }
}