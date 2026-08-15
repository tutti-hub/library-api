package com.jjtutti.library.common.dto;

import java.util.List;

public record ErrorResponse(String message, List<FieldError> errors, int statusCode, String path) {
    public ErrorResponse(String message, int statusCode, String path) {
        this(message, List.of(), statusCode, path);
    }
}
