package com.henriqueAraujo.libraryapi.service.repository;

import com.henriqueAraujo.libraryapi.model.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;


public interface BookRepository extends JpaRepository<Book, Long> {

    boolean existsByIsbn(String isbn);
}
