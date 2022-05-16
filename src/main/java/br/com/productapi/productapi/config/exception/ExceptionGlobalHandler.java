package br.com.productapi.productapi.config.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionGlobalHandler {

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<?> handleValidationException(ValidationException validationException) {
        var exceptionDetails = new ExceptionDetails();
        exceptionDetails.setStatus(HttpStatus.BAD_REQUEST.value());
        exceptionDetails.setMessage(validationException.getMessage());
        return new ResponseEntity<>(exceptionDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<?> handleAuthenticationException(AuthenticationException authenticationException) {
        var exceptionDetails = new ExceptionDetails();
        exceptionDetails.setStatus(HttpStatus.UNAUTHORIZED.value());
        exceptionDetails.setMessage(authenticationException.getMessage());
        return new ResponseEntity<>(exceptionDetails, HttpStatus.UNAUTHORIZED);
    }
}
