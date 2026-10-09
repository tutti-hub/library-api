package com.jjtutti.library.book.web;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.jjtutti.library.book.application.BookService;
import com.jjtutti.library.book.application.dto.AddBookCommand;
import com.jjtutti.library.book.application.dto.BookReadDto;
import com.jjtutti.library.book.application.dto.UpdateBookCommand;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/books")
public class BookController {
    
    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public List<BookReadDto> getBooks() {
        return bookService.getBooks();
    }
   
    @GetMapping("/{id}")
    public BookReadDto getBookById(@PathVariable("id") Long id) {
        return bookService.getBookById(id);
    }

    @PostMapping
    public BookReadDto addNewBook(@RequestBody @Valid AddBookCommand addBookCommand) {
        return bookService.addBook(addBookCommand);
    }

    @PutMapping("/{id}")
    public BookReadDto updateBook(@PathVariable("id") Long id, @RequestBody @Valid UpdateBookCommand updateBookCommand) {
        return bookService.updateBook(id, updateBookCommand);
    }
    
    @DeleteMapping("/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deleteBookById(@PathVariable("id") Long id) {
        bookService.deleteBookById(id);
    }
}
