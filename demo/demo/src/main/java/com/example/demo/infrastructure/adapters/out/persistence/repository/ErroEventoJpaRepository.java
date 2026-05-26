package com.example.demo.infrastructure.adapters.out.persistence.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.infrastructure.adapters.out.persistence.entity.ErroEventoEntity;

public interface ErroEventoJpaRepository extends JpaRepository<ErroEventoEntity, UUID> {
}