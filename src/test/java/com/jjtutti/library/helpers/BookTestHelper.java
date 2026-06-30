package com.jjtutti.library.helpers;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import com.jjtutti.library.application.book.BookReadDto;
import com.jjtutti.library.dao.entity.book.Book;

public class BookTestHelper {

    public static List<Book> booksWithoutIds(int count) {
        return IntStream.range(1, count + 1)
        .mapToObj(i -> new Book(title(i), author(i), isbn(i))).toList();
    }

    public static List<Book> booksWithIds(int count) {
        return IntStream.range(1, count + 1).mapToObj(i -> { 
            var book = new Book(title(i), author(i), isbn(i));
            book.setId((long)i);
            return book;
        }).toList();
    }

    public static Book bookWithoutId() {
        return new Book(title(1), author(1), isbn(1)); 
    }

    public static Book bookWithId(Long id) {
        Book book = new Book(title(id), author(id), isbn(id));
        book.setId(id);
        return book;
    }

    public static List<BookReadDto> bookReadDtosList(int count) {
        return LongStream.rangeClosed(1, count).mapToObj(i -> 
            new BookReadDto(i, title(i), author(i), isbn(i))).toList();
    }

    public static BookReadDto bookReadDto(long id) {
        return new BookReadDto(id, title(id), author(id), isbn(id));
    }

    public static String title(long id) {
        return "Title_" + id; 
    }

    public static String author(long id) {
        return "Author_" + id;
    }

    public static String isbn(long id) {
        return "ISBN_" + id;
    }
}
