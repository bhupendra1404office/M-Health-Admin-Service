package com.mhealth.admin.exception;

import com.mhealth.admin.config.Constants;
import com.mhealth.admin.dto.Status;
import com.mhealth.admin.dto.exception.ErrorDetails;
import com.mhealth.admin.dto.exception.ValidationError;
import com.mhealth.admin.dto.exception.ValidationErrorResponse;
import com.mhealth.admin.dto.response.Response;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.auth.InvalidCredentialsException;
import org.springframework.context.NoSuchMessageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<?> handleUserNotFoundException(UsernameNotFoundException ex, WebRequest request) {
        Response response = new Response(Status.FAILED, Constants.USER_NOT_FOUND_CODE,Constants.USER_NOT_FOUND, null);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<?> handleInvalidCredentialsException(InvalidCredentialsException ex, WebRequest request) {
        Response response = new Response(Status.FAILED,Constants.INVALID_PASSWORD_CODE,Constants.INVALID_PASSWORD,null);
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(NoSuchMessageException.class)
    public ResponseEntity<?> handleNoSuchMessageException(NoSuchMessageException ex, WebRequest request) {
        Response response = new Response(Status.FAILED, "10001", "Message not found for the specified locale.", null);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        ErrorDetails response = new ErrorDetails(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> badCredentialsException(BadCredentialsException ex, WebRequest request) {
        Response response = new Response(Status.FAILED,Constants.INVALID_PASSWORD_CODE,Constants.INVALID_PASSWORD,null);
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGlobalException(Exception ex, WebRequest request) {
        ex.printStackTrace();
        log.error("Exception: ",ex);
        ErrorDetails response = new ErrorDetails(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Set<String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Set<String> messages = new HashSet<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            messages.add(error.getDefaultMessage());
        }
        return new ResponseEntity<>(messages, HttpStatus.PRECONDITION_FAILED);
    }

}
