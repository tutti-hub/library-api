package com.jjtutti.library.dao.repository.book;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jjtutti.library.dao.entity.book.Book;

public interface BookRepository extends JpaRepository<Book, Long> {

}
