package com.jjtutti.library.mapper.book;

import java.util.Objects;

import org.springframework.stereotype.Component;

import com.jjtutti.library.application.book.AddBookCommand;
import com.jjtutti.library.application.book.BookReadDto;
import com.jjtutti.library.application.book.UpdateBookCommand;
import com.jjtutti.library.dao.entity.book.Book;


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
