

package com.healthcare.healthcare_insurance_api.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // -----------------------------------------
    // ResponseStatusException
    // -----------------------------------------

    @ExceptionHandler(
        ResponseStatusException.class
    )
    public ResponseEntity<ApiError>
    handleResponseStatusException(
            ResponseStatusException exception
    ) {

        HttpStatus status =
                HttpStatus.valueOf(
                        exception.getStatusCode().value()
                );

        ApiError error = new ApiError(

                LocalDateTime.now(),

                status.value(),

                exception.getReason(),

                null
        );

        return ResponseEntity
                .status(status)
                .body(error);
    }

    // -----------------------------------------
    // IllegalArgumentException
    // -----------------------------------------

    @ExceptionHandler(
        IllegalArgumentException.class
    )
    public ResponseEntity<ApiError>
    handleIllegalArgument(
            IllegalArgumentException exception
    ) {

        ApiError error = new ApiError(

                LocalDateTime.now(),

                HttpStatus.BAD_REQUEST.value(),

                exception.getMessage(),

                null
        );

        return ResponseEntity
                .badRequest()
                .body(error);
    }

    // -----------------------------------------
    // Validation errors
    // -----------------------------------------

    @ExceptionHandler(
        MethodArgumentNotValidException.class
    )
    public ResponseEntity<ApiError>
    handleValidation(
            MethodArgumentNotValidException exception
    ) {

        Map<String, String> errors =
                new HashMap<>();

        exception.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        errors.put(
                                error.getField(),
                                error.getDefaultMessage()
                        )
                );

        ApiError response = new ApiError(

                LocalDateTime.now(),

                HttpStatus.BAD_REQUEST.value(),

                "Validation failed",

                errors
        );

        return ResponseEntity
                .badRequest()
                .body(response);
    }
}
