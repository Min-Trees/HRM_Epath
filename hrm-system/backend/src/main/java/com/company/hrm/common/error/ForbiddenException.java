package com.company.hrm.common.error;

import org.springframework.http.HttpStatus;

public class ForbiddenException extends RuntimeException {
    private final String code;

    public ForbiddenException(String code, String message) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public HttpStatus getStatus() {
        return HttpStatus.FORBIDDEN;
    }
}