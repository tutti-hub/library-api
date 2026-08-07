package com.jjtutti.library.web.book;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jjtutti.library.application.book.AddBookCommand;
import com.jjtutti.library.config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import com.jjtutti.library.application.book.BookService;
import com.jjtutti.library.application.book.UpdateBookCommand;
import com.jjtutti.library.auth.JwtService;
import com.jjtutti.library.infrastructure.exceptions.BookNotFoundException;
import static com.jjtutti.library.helpers.BookTestHelper.*;
import static org.assertj.core.api.Assertions.*;

@WebMvcTest(BookController.class)
@Import({SecurityConfig.class})
public class BookControllerTest {

    @Autowired
    MockMvcTester mvc;

    @MockitoBean
    BookService bookService;

    
    @MockitoBean
    JwtService jwtService;

    @MockitoBean
    UserDetailsService userDetailsService;

    ObjectMapper objectMapper = new ObjectMapper();

    @WithMockUser(authorities = "ROLE_USER")
    @Test
    public void shouldReturnAllBooks() {
        int count = 3;

        when(bookService.getBooks()).thenReturn(bookReadDtosList(count));

        var response = mvc.get().uri("/api/books").accept(MediaType.APPLICATION_JSON);

        assertThat(response)
                .hasStatusOk()
                .hasContentTypeCompatibleWith(MediaType.APPLICATION_JSON);

        assertThat(response).bodyJson().extractingPath("$.length()").isEqualTo(count);

        assertThat(response).bodyJson().extractingPath("$[0].title").isEqualTo("Title_1");

    }

    @WithMockUser(authorities = "ROLE_USER")
    @Test
    public void shouldReturnBookWhenExists() {
        long id = 1L;
        when(bookService.getBookById(id)).thenReturn(bookReadDto(id));

        var response = mvc.get().uri("/api/books/{id}", id).accept(MediaType.APPLICATION_JSON);

        assertThat(response).hasStatusOk().hasContentTypeCompatibleWith(MediaType.APPLICATION_JSON);

        var body = assertThat(response).bodyJson();

        body.extractingPath("$.id").convertTo(Long.class).isEqualTo(id);
        body.extractingPath("$.title").isEqualTo(title(id));
        body.extractingPath("$.author").isEqualTo(author(id));
        body.extractingPath("$.isbn").isEqualTo(isbn(id));
    }

    @WithMockUser(authorities = "ROLE_USER")
    @Test
    public void shouldReturnBookNotFoundWhenDoesNotExist() {
        long id = 84L;
        when(bookService.getBookById(id)).thenThrow(new BookNotFoundException(id));

        var response = mvc.get().uri("/api/books/{id}", id)
                .accept(MediaType.APPLICATION_JSON);

        assertThat(response).hasStatus(HttpStatus.NOT_FOUND)
                .hasContentTypeCompatibleWith(MediaType.APPLICATION_JSON);

        var body = assertThat(response).bodyJson();

        body.extractingPath("$.message").isEqualTo("Book with id " + id + " not found");
        body.extractingPath("$.statusCode").isEqualTo(HttpStatus.NOT_FOUND.value());
        body.extractingPath("$.path").isEqualTo("/api/books/" + id);

        body.extractingPath("$.errors.length()").isEqualTo(0);
    }

    @WithMockUser(authorities = "ROLE_LIBRARIAN")
    @Test
    public void shouldAddNewBook() throws JsonProcessingException {

        long id = 1;

        AddBookCommand addBookCommand = new AddBookCommand(title(id), author(id), isbn(id));

        when(bookService.addBook(addBookCommand)).thenReturn(bookReadDto(id));

        String jsonPayload = objectMapper.writeValueAsString(addBookCommand);

        var response = mvc.post()
                .uri("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonPayload);

        assertThat(response).hasStatusOk().hasContentTypeCompatibleWith(MediaType.APPLICATION_JSON);

        verify(bookService).addBook(addBookCommand);

        var body = assertThat(response).bodyJson();

        body.extractingPath("$.id").convertTo(Long.class).isEqualTo(id);
    }

    @WithMockUser(authorities = "ROLE_LIBRARIAN")
    @Test
    public void shouldUpdateBook() throws JsonProcessingException {
        long id = 1;

        UpdateBookCommand updateBookCommand = new UpdateBookCommand(title(id), author(id), isbn(id));

        when(bookService.updateBook(id, updateBookCommand)).thenReturn(bookReadDto(id));

        String jsonPayload = objectMapper.writeValueAsString(updateBookCommand);

        var response = mvc.put().uri("/api/books/{id}", id)
                .contentType(MediaType.APPLICATION_JSON).content(jsonPayload);

        assertThat(response).hasStatusOk().hasContentTypeCompatibleWith(MediaType.APPLICATION_JSON);
        verify(bookService).updateBook(id, updateBookCommand);

        var body = assertThat(response).bodyJson();

        body.extractingPath("$.title").isEqualTo(updateBookCommand.title());
        body.extractingPath("$.author").isEqualTo(updateBookCommand.author());
        body.extractingPath("$.isbn").isEqualTo(updateBookCommand.isbn());

    }

    @WithMockUser(authorities = "ROLE_ADMIN")
    @Test
    public void shouldDeleteBook() {
        long id = 1;

        var response = mvc.delete().uri("/api/books/{id}", id);
        assertThat(response).hasStatus(HttpStatus.NO_CONTENT);
        verify(bookService).deleteBookById(id);
        verifyNoMoreInteractions(bookService);
    }

    @WithAnonymousUser
    @Test
    public void shouldReturn401WhenNotAuthenticated() {
        var response = mvc.get().uri("/api/books");
        assertThat(response).hasStatus(HttpStatus.UNAUTHORIZED);
    }

    @WithMockUser(authorities = "ROLE_USER")
    @Test
    void shouldReturn403WhenUserCreatesBook() {
        var jsonPayload = "{}"; 
        var response = mvc.post().uri("/api/books")
        .contentType(MediaType.APPLICATION_JSON)
        .content(jsonPayload)
        .accept(MediaType.APPLICATION_JSON);
        
        assertThat(response).hasStatus(HttpStatus.FORBIDDEN);
        verifyNoInteractions(bookService);
    }

    @WithMockUser(authorities = "ROLE_LIBRARIAN")
    @Test
    void shouldReturn403WhenLibrarianDeletesBook() {
        var response = mvc.delete().uri("/api/books/{id}", 1);
        assertThat(response).hasStatus(HttpStatus.FORBIDDEN);
        verifyNoInteractions(bookService);
    }
}
