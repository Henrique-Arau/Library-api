package com.henriqueAraujo.libraryapi.model.repository;

import com.henriqueAraujo.libraryapi.model.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;


public interface BookRepository extends JpaRepository<Book, Long> {
}
