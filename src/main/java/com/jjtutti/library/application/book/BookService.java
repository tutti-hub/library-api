package com.jjtutti.library.application.book;

import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jjtutti.library.dao.entity.book.Book;
import com.jjtutti.library.dao.repository.book.BookRepository;
import com.jjtutti.library.infrastructure.exceptions.BookNotFoundException;
import com.jjtutti.library.mapper.book.BookMapper;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    public BookService(BookRepository bookRepository, BookMapper bookMapper) {
        this.bookRepository = bookRepository;
        this.bookMapper = bookMapper;
    }

    @Transactional(readOnly = true)
    public List<BookReadDto> getBooks() {
        return bookRepository.findAll().stream().map(bookMapper::toBookReadDto).toList();
    }

    @Transactional(readOnly = true)
    public BookReadDto getBookById(Long id) {
        
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Id must be positive");
        }

        return bookRepository
                .findById(id)
                .map(bookMapper::toBookReadDto)
                .orElseThrow(() -> new BookNotFoundException(id));
    }

    @Transactional
    public BookReadDto addBook(AddBookCommand addBookCommand) {
        Objects.requireNonNull(addBookCommand, "Add book command can not be null");
        Book newBook = bookMapper.fromAddBookCommand(addBookCommand);
        newBook = bookRepository.save(newBook);
        return bookMapper.toBookReadDto(newBook);
    }

    @Transactional
    public BookReadDto updateBook(Long id, UpdateBookCommand updateBookCommand) {
        Objects.requireNonNull(id, "Book id can not be null");
        Objects.requireNonNull(updateBookCommand, "Update book command can not be null");
        Book book = bookRepository
                .findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));
        bookMapper.updateBook(updateBookCommand, book);
        return bookMapper.toBookReadDto(book);
    }

    @Transactional
    public void deleteBookById(Long id) {
        Objects.requireNonNull(id, "Book id can not be null");
        Book book = bookRepository.findById(id).orElseThrow(() -> new BookNotFoundException(id));
        bookRepository.delete(book);
    }

}
