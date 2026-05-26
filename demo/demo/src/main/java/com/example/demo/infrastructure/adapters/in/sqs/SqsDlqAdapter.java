package com.example.demo.infrastructure.adapters.in.sqs;

import com.example.demo.application.ports.in.ErroEventoServicePort;
import io.awspring.cloud.sqs.annotation.SqsListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class SqsDlqAdapter {

    private static final Logger log = LoggerFactory.getLogger(SqsDlqAdapter.class);

    private final ErroEventoServicePort servicePort;

    public SqsDlqAdapter(ErroEventoServicePort servicePort) {
        this.servicePort = servicePort;
    }

    @SqsListener("${queue.dlq-name}")
    public void listen(@Payload String payload) {
        log.info("Mensagem recebida da DLQ");
        servicePort.process(payload);
        log.info("Mensagem da DLQ processada e removida com sucesso");
    }
}