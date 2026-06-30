package com.jjtutti.library.application.book;

import jakarta.validation.constraints.NotBlank;

public record UpdateBookCommand(
    
    @NotBlank
    String title, 
    
    @NotBlank
    String author, 
    
    @NotBlank
    String isbn) {

}
