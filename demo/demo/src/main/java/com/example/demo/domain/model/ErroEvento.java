package com.example.demo.domain.model;

import java.time.Instant;
import java.util.UUID;

import com.example.demo.domain.enums.Severity;
import com.example.demo.domain.enums.Status;

public class ErroEvento {

    private UUID errorId;
    private String queueName;
    private String payload;
    private Instant timestamp;
    private Status status;
    private Severity severity;

    public ErroEvento(String queueName, String payload, Severity severity) {
        this.errorId   = UUID.randomUUID();
        this.queueName = queueName;
        this.payload   = payload;
        this.timestamp = Instant.now();
        this.status    = Status.PENDING_ANALYSIS;
        this.severity  = severity;
    }

    public UUID getErrorId()      { return errorId; }
    public String getQueueName()  { return queueName; }
    public String getPayload()    { return payload; }
    public Instant getTimestamp() { return timestamp; }
    public Status getStatus()     { return status; }
    public Severity getSeverity() { return severity; }
}