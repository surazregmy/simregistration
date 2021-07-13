package com.example.mtsimregister.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ExceptionMessage {
    private LocalDateTime timeOfException;
    private String message;
    private String details;
}
