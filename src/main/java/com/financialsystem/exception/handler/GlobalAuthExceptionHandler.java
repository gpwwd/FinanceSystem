package com.financialsystem.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.security.SignatureException;
import org.springframework.security.access.AccessDeniedException;

@RestControllerAdvice
public class GlobalAuthExceptionHandler {

    @ExceptionHandler({AuthenticationException.class, SignatureException.class})
    public ProblemDetail handleAuthenticationException(RuntimeException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, e.getMessage());
    }

    @ExceptionHandler({AccessDeniedException.class})
    public ProblemDetail handleAccessDeniedException(AccessDeniedException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, e.getMessage());
    }
}
