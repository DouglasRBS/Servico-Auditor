package com.example.demo.infrastructure.adapters.out.persistence.repository;

import org.springframework.stereotype.Repository;

import com.example.demo.application.ports.out.ErroEventoRepositoryPort;
import com.example.demo.domain.model.ErroEvento;
import com.example.demo.infrastructure.adapters.out.persistence.mapper.ErroEventoMapper;

@Repository
public class ErroEventoRepositoryAdapter implements ErroEventoRepositoryPort {

    private final ErroEventoJpaRepository jpaRepository;
    private final ErroEventoMapper mapper;

    public ErroEventoRepositoryAdapter(ErroEventoJpaRepository jpaRepository,
            ErroEventoMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public void salvar(ErroEvento erroEvento) {
        jpaRepository.save(mapper.toEntity(erroEvento));
    }
}