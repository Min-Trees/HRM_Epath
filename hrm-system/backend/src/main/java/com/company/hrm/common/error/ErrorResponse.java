package com.company.hrm.common.error;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    private Instant timestamp;
    private String code;
    private String message;
    private List<FieldError> details;

    public ErrorResponse() {}

    public ErrorResponse(String code, String message) {
        this.timestamp = Instant.now();
        this.code = code;
        this.message = message;
    }

    public ErrorResponse(String code, String message, List<FieldError> details) {
        this(code, message);
        this.details = details;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<FieldError> getDetails() {
        return details;
    }

    public void setDetails(List<FieldError> details) {
        this.details = details;
    }

    public record FieldError(String field, String message) {}
}