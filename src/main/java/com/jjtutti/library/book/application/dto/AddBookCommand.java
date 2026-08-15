package com.jjtutti.library.book.application.dto;

import jakarta.validation.constraints.NotBlank;

public record AddBookCommand(
    
    @NotBlank
    String title, 
    
    @NotBlank
    String author, 
    
    @NotBlank
    String isbn) {

}
