package com.test.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
@Entity
@Table(name = "error_log",schema = "db001")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "error_message", nullable = false)
    private String errorMessage;

    @Column(name = "error_code", length = 100)
    private String errorCode;

    @Column(name = "stack_trace", columnDefinition = "TEXT")
    private String stackTrace;

    @Column(name = "service_name", length = 255)
    private String serviceName;

    @Column(name = "environment", length = 100)
    private String environment;

    @Column(name = "process_id", length = 100)
    private String processId;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;
}