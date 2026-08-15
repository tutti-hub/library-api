package com.jjtutti.library.book.infrastructure.mapper;

import java.util.Objects;

import org.springframework.stereotype.Component;

import com.jjtutti.library.book.application.dto.AddBookCommand;
import com.jjtutti.library.book.application.dto.BookReadDto;
import com.jjtutti.library.book.application.dto.UpdateBookCommand;
import com.jjtutti.library.book.dao.Book;


@Component
public class BookMapper {
    public BookReadDto toBookReadDto(Book entity) {
        Objects.requireNonNull(entity, "Book can not be null");
        return new BookReadDto(entity.getId(), entity.getTitle(), entity.getAuthor(), entity.getIsbn());
    }

    public Book fromAddBookCommand(AddBookCommand addBookComand){
        Objects.requireNonNull(addBookComand, "Add book command can not be null");
        return Book.builder()
        .title(addBookComand.title())
        .author(addBookComand.author())
        .isbn(addBookComand.isbn())
        .build();
    }

    public Book updateBook(UpdateBookCommand updateBookCommand, Book book) {
        Objects.requireNonNull(updateBookCommand, "Update book command can not be null");
        Objects.requireNonNull(book, "Book can not be null");
        
        book.setTitle(updateBookCommand.title());
        book.setAuthor(updateBookCommand.author());
        book.setIsbn(updateBookCommand.isbn());
        return book;
    }
}
