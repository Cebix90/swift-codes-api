package com.cebix.swiftcodesapi.exception;

import com.cebix.swiftcodesapi.dto.MessageResponseDTO;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @SuppressWarnings("unchecked")
    @Test
    @DisplayName("should return 404 when EntityNotFoundException is thrown")
    void should_HandleEntityNotFoundException() {
        String errorMsg = "Entity not found";
        EntityNotFoundException ex = new EntityNotFoundException(errorMsg);

        ResponseEntity<Object> response = handler.handleEntityNotFound(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isInstanceOf(Map.class);

        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertThat(body).containsEntry("status", 404);
        assertThat(body).containsEntry("error", "Not Found");
        assertThat(body).containsEntry("message", errorMsg);
        assertThat(body).containsKey("timestamp");
    }

    @SuppressWarnings("unchecked")
    @Test
    @DisplayName("should return 400 when IllegalArgumentException is thrown")
    void should_HandleIllegalArgumentException() {
        String errorMsg = "Invalid input";
        IllegalArgumentException ex = new IllegalArgumentException(errorMsg);

        ResponseEntity<Object> response = handler.handleIllegalArgument(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isInstanceOf(Map.class);

        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertThat(body).containsEntry("status", 400);
        assertThat(body).containsEntry("error", "Bad Request");
        assertThat(body).containsEntry("message", errorMsg);
        assertThat(body).containsKey("timestamp");
    }

    @Test
    @DisplayName("should return 500 when unexpected exception occurs")
    void should_HandleGenericException() {
        Exception ex = new RuntimeException("Unexpected failure");

        ResponseEntity<MessageResponseDTO> response = handler.handleException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("Unexpected error occurred");
    }
}
