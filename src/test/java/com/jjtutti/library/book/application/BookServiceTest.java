package com.jjtutti.library.book.application;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.jjtutti.library.book.application.dto.AddBookCommand;
import com.jjtutti.library.book.application.dto.BookReadDto;
import com.jjtutti.library.book.application.dto.UpdateBookCommand;
import com.jjtutti.library.book.dao.Book;
import com.jjtutti.library.book.dao.BookRepository;
import com.jjtutti.library.book.infrastructure.exceptions.BookNotFoundException;
import com.jjtutti.library.book.infrastructure.mapper.BookMapper;

import static com.jjtutti.library.helpers.BookTestHelper.*;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {

    @Mock
    BookRepository bookRepository;

    BookService bookService;

    @BeforeEach
    void setUp() {
        bookService = new BookService(
                bookRepository,
                new BookMapper());
    }

    @Test
    public void shouldReturnAllBooks() {
        int booksCount = 5;
        when(bookRepository.findAll()).thenReturn(booksWithIds(booksCount));

        var books = bookService.getBooks();
        assertThat(books).hasSize(booksCount);

        verify(bookRepository).findAll();
    }

    @Test
    public void shouldReturnBookWhenExists() {
        long id = 1;
        when(bookRepository.findById(id))
                .thenReturn(Optional.of(bookWithId(id)));

        var bookDto = bookService.getBookById(id);

        assertThat(bookDto)
                .extracting(
                        BookReadDto::id,
                        BookReadDto::title,
                        BookReadDto::author)
                .containsExactly(
                        id,
                        title(id),
                        author(id));
    }

    @Test
    public void shouldThrowExceptionWhenBookNotFound() {
        long id = 1;
        when(bookRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.getBookById(id))
                .isInstanceOf(BookNotFoundException.class)
                .hasMessage("Book with id 1 not found");
    }

    @Test
    public void shouldAddBook() {
        long id = 1L;
        AddBookCommand addBookCommand = new AddBookCommand(title(id), author(id), isbn(id));
        

        when(bookRepository.save(any(Book.class)))
            .thenReturn(bookWithId(id));

        var bookReadDto = bookService.addBook(addBookCommand);

        assertThat(bookReadDto).extracting(
                BookReadDto::id,
                BookReadDto::title,
                BookReadDto::author,
                BookReadDto::isbn)
                .containsExactly(id, title(id), author(id), isbn(id));

        verify(bookRepository).save(any());
    }

    @Test
    public void shouldUpdateBook() {
        long id = 1L;

        UpdateBookCommand updateBookCommand = 
            new UpdateBookCommand(title(11), author(11), isbn(11));

        when(bookRepository.findById(id)).thenReturn(Optional.of(bookWithId(id)));            
        
        BookReadDto bookReadDto = bookService.updateBook(id, updateBookCommand);

        assertThat(bookReadDto)
            .extracting(BookReadDto::id, BookReadDto::title, BookReadDto::author, BookReadDto::isbn)
            .containsExactly(id, updateBookCommand.title(), updateBookCommand.author(), updateBookCommand.isbn());

        verify(bookRepository).findById(id);
        verify(bookRepository, never()).save(any());
        verify(bookRepository, never()).delete(any());
    }

    @Test
    public void shouldDeleteBook() {
        long id = 1L;
        Book book = bookWithId(id);
        when(bookRepository.findById(id)).thenReturn(Optional.of(book));
        
        bookService.deleteBookById(id);            
        
        verify(bookRepository).findById(id);
        verify(bookRepository).delete(book);
        verifyNoMoreInteractions(bookRepository);
    }

}
