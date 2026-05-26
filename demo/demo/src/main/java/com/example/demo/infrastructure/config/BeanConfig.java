package com.example.demo.infrastructure.config;

import com.example.demo.application.bo.ErroEventoBO;
import com.example.demo.application.ports.in.ErroEventoServicePort;
import com.example.demo.application.ports.out.ErroEventoRepositoryPort;
import com.example.demo.application.service.ErroEventoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public ErroEventoServicePort erroEventoServicePort(
            ErroEventoRepositoryPort repositoryPort,
            ErroEventoBO bo,
            ObjectMapper objectMapper,
            @Value("${queue.dlq-name}") String queueName) {
        return new ErroEventoService(repositoryPort, bo, objectMapper, queueName);
    }
}