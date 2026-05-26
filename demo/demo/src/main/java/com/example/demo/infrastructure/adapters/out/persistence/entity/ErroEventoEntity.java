package com.example.demo.infrastructure.adapters.out.persistence.entity;

import java.time.Instant;
import java.util.UUID;

import com.example.demo.domain.enums.Severity;
import com.example.demo.domain.enums.Status;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "erro_evento")
public class ErroEventoEntity {

    @Id
    @Column(name = "error_id")
    private UUID errorId;

    @Column(name = "queue_name", nullable = false)
    private String queueName;

    @Column(name = "payload", nullable = false, columnDefinition = "TEXT")
    private String payload;

    @Column(name = "timestamp", nullable = false)
    private Instant timestamp;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

    @Enumerated(EnumType.STRING)
    @Column(name = "severity", nullable = false)
    private Severity severity;

    public UUID getErrorId()             { return errorId; }
    public void setErrorId(UUID v)       { this.errorId = v; }
    public String getQueueName()         { return queueName; }
    public void setQueueName(String v)   { this.queueName = v; }
    public String getPayload()           { return payload; }
    public void setPayload(String v)     { this.payload = v; }
    public Instant getTimestamp()        { return timestamp; }
    public void setTimestamp(Instant v)  { this.timestamp = v; }
    public Status getStatus()            { return status; }
    public void setStatus(Status v)      { this.status = v; }
    public Severity getSeverity()        { return severity; }
    public void setSeverity(Severity v)  { this.severity = v; }
}