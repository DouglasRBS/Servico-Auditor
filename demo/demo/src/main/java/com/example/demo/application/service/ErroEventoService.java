package com.example.demo.application.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.demo.application.bo.ErroEventoBO;
import com.example.demo.application.dto.OrderEventDTO;
import com.example.demo.application.ports.in.ErroEventoServicePort;
import com.example.demo.application.ports.out.ErroEventoRepositoryPort;
import com.example.demo.domain.enums.Severity;
import com.example.demo.domain.model.ErroEvento;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ErroEventoService implements ErroEventoServicePort {

    private static final Logger log = LoggerFactory.getLogger(ErroEventoService.class);

    private final ErroEventoRepositoryPort repositoryPort;
    private final ErroEventoBO erroEventoBO;
    private final ObjectMapper objectMapper;
    private final String queueName;

    public ErroEventoService(ErroEventoRepositoryPort repositoryPort,
            ErroEventoBO erroEventoBO,
            ObjectMapper objectMapper,
            String queueName) {
        this.repositoryPort = repositoryPort;
        this.erroEventoBO = erroEventoBO;
        this.objectMapper = objectMapper;
        this.queueName = queueName;
    }

    @Override
    public void process(String payload) {
        try {
            OrderEventDTO dto = objectMapper.readValue(payload, OrderEventDTO.class);

            int totalAmount = erroEventoBO.calcularTotalAmount(dto.getOrderItems());
            Severity severity = erroEventoBO.calcularSeverity(totalAmount);

            ErroEvento evento = new ErroEvento(queueName, payload, severity);

            repositoryPort.salvar(evento);

            log.info("Erro salvo — errorId: {} | severity: {}", evento.getErrorId(), severity);

        } catch (Exception e) {
            log.error("Falha ao processar mensagem da DLQ: {}", e.getMessage(), e);
            throw new RuntimeException("Falha ao processar mensagem da DLQ", e);
        }
    }
}