package com.henriqueAraujo.libraryapi.service.impl;

import com.henriqueAraujo.libraryapi.exception.BusinesException;
import com.henriqueAraujo.libraryapi.model.entity.Book;
import com.henriqueAraujo.libraryapi.service.repository.BookRepository;
import com.henriqueAraujo.libraryapi.service.BookService;
import org.springframework.stereotype.Service;

@Service
public class BookServiceImpl implements BookService {

    private BookRepository repository;

    public BookServiceImpl(BookRepository repository) {
        this.repository = repository;
    }

    @Override
    public Book save(Book book) {
        if( repository.existsByIsbn(book.getIsbn())) {
            throw new BusinesException("Isbn já cadastrado.");
        }
        return  repository.save(book);
    }
}
