package com.jjtutti.library.web.book;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import com.jjtutti.library.application.book.AddBookCommand;
import com.jjtutti.library.application.book.BookReadDto;
import com.jjtutti.library.application.book.BookService;
import com.jjtutti.library.application.book.UpdateBookCommand;
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
    public BookReadDto getBookById(@PathVariable Long id) {
        return bookService.getBookById(id);
    }

    @PostMapping
    public BookReadDto addNewBook(@RequestBody @Valid AddBookCommand addBookCommand) {
        return bookService.addBook(addBookCommand);
    }

    @PutMapping("/{id}")
    public BookReadDto updateBook(@PathVariable Long id, @RequestBody @Valid UpdateBookCommand updateBookCommand) {
        return bookService.updateBook(id, updateBookCommand);
    }
    
    @DeleteMapping("/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deleteBookById(@PathVariable Long id) {
        bookService.deleteBookById(id);
    }
}
