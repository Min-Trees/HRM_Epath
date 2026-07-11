package com.company.hrm.common.error;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mock.http.MockHttpInputMessage;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleBusiness_returns409WithCode() {
        ResponseEntity<ErrorResponse> resp = handler.handleBusiness(
                new BusinessException("PAYROLL_LOCKED", "Bảng công đã chốt"));
        assertEquals(HttpStatus.CONFLICT, resp.getStatusCode());
        assertEquals("PAYROLL_LOCKED", resp.getBody().getCode());
        assertEquals("Bảng công đã chốt", resp.getBody().getMessage());
    }

    @Test
    void handleBusiness_supportsCustomStatus() {
        ResponseEntity<ErrorResponse> resp = handler.handleBusiness(
                new BusinessException("X", "msg", HttpStatus.UNPROCESSABLE_ENTITY));
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, resp.getStatusCode());
    }

    @Test
    void handleNotFound_returns404WithCode() {
        ResponseEntity<ErrorResponse> resp = handler.handleNotFound(
                new ResourceNotFoundException("EMPLOYEE_NOT_FOUND", "Không tìm thấy nhân viên"));
        assertEquals(HttpStatus.NOT_FOUND, resp.getStatusCode());
        assertEquals("EMPLOYEE_NOT_FOUND", resp.getBody().getCode());
    }

    @Test
    void handleForbidden_returns403() {
        ResponseEntity<ErrorResponse> resp = handler.handleForbidden(
                new ForbiddenException("FORBIDDEN", "Không đủ quyền"));
        assertEquals(HttpStatus.FORBIDDEN, resp.getStatusCode());
        assertEquals("FORBIDDEN", resp.getBody().getCode());
    }

    @Test
    void handleIllegal_returns400() {
        ResponseEntity<ErrorResponse> resp = handler.handleIllegal(
                new IllegalArgumentException("Sai định dạng"));
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
        assertEquals("INVALID_ARGUMENT", resp.getBody().getCode());
    }

    @Test
    void handleNotReadable_returns400() {
        ResponseEntity<ErrorResponse> resp = handler.handleNotReadable(
                new HttpMessageNotReadableException("bad json", new MockHttpInputMessage(new byte[0])));
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
        assertEquals("MALFORMED_REQUEST", resp.getBody().getCode());
    }

    @Test
    void errorResponse_serializesDetails() {
        ErrorResponse.FieldError fe = new ErrorResponse.FieldError("maNv", "không được trống");
        ErrorResponse r = new ErrorResponse("VALIDATION_ERROR", "Bad", List.of(fe));
        assertEquals(1, r.getDetails().size());
        assertEquals("maNv", r.getDetails().get(0).field());
        assertEquals("không được trống", r.getDetails().get(0).message());
    }
}