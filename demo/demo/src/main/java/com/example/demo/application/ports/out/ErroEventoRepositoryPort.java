package com.example.demo.application.ports.out;

import com.example.demo.domain.model.ErroEvento;

public interface ErroEventoRepositoryPort {
    void salvar(ErroEvento erroEvento);
}