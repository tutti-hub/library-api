package com.jjtutti.library.infrastructure.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.jjtutti.library.auth.RefreshTokenNotActiveException;
import com.jjtutti.library.auth.RefreshTokenNotFoundException;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BookNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleBookNotFound(BookNotFoundException e, HttpServletRequest req) {
        return new ErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND.value(), req.getRequestURI());
    }

    @ExceptionHandler(RefreshTokenNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleRefreshTokenNotFound(RefreshTokenNotFoundException e, HttpServletRequest req) {
        return new ErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND.value(), req.getRequestURI());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIllegalArgument(IllegalArgumentException e, HttpServletRequest req) {
        return new ErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST.value(), req.getRequestURI());
    }

    @ExceptionHandler(JwtException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleJwtException(JwtException e, HttpServletRequest req){
        return new ErrorResponse(e.getMessage(), HttpStatus.UNAUTHORIZED.value(), req.getRequestURI());
    }

    @ExceptionHandler(RefreshTokenNotActiveException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleRefreshTokenNotActive(RefreshTokenNotActiveException e, HttpServletRequest req) {
        return new ErrorResponse(e.getMessage(), HttpStatus.UNAUTHORIZED.value(), req.getRequestURI());
    }
     

}
