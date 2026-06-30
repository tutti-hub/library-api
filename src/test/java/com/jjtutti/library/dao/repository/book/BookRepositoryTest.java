package com.jjtutti.library.dao.repository.book;

import com.jjtutti.library.dao.entity.book.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import static com.jjtutti.library.helpers.BookTestHelper.*;
import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.Test;

@ActiveProfiles("test")
@DataJpaTest
public class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Test
    public void shouldSaveBooks() {
        int count = 5;
        booksWithoutIds(count).forEach(bookRepository::save);
        assertThat(bookRepository.count()).isEqualTo(count);
    }

    @Test
    void shouldFindBook() {
        Book saved = bookRepository.save(bookWithoutId());

        assertThat(bookRepository.findById(saved.getId()))
                .get()
                .extracting(Book::getTitle, Book::getAuthor, Book::getIsbn)
                .containsExactly(
                        saved.getTitle(),
                        saved.getAuthor(),
                        saved.getIsbn());
    }

    @Test
    public void shouldGenerateId() {
        Book book = bookWithoutId();
        bookRepository.save(book);
        assertThat(book.getId()).isNotNull();
    }

    @Test
    public void shouldPersistBook() {
        Book book = bookWithoutId();
        Book saved = bookRepository.save(book);
        assertThat(saved.getTitle()).isEqualTo(book.getTitle());
    }

    @Test
    public void shouldReturnEmptyWhenBookDoesNotExist() {
        assertThat(bookRepository.findById(100L)).isEmpty();
    }

    @Test
    void shouldDeleteBook() {
        Book saved = bookRepository.save(bookWithoutId());

        bookRepository.deleteById(saved.getId());

        assertThat(bookRepository.findById(saved.getId())).isEmpty();
    }

    @Test
    void shouldDecreaseCountAfterDelete() {
        Book saved = bookRepository.save(bookWithoutId());

        bookRepository.deleteById(saved.getId());

        assertThat(bookRepository.count()).isEqualTo(0);
    }
}
