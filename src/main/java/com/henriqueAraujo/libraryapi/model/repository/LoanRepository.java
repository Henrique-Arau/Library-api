package com.henriqueAraujo.libraryapi.model.repository;


import com.henriqueAraujo.libraryapi.model.entity.Book;
import com.henriqueAraujo.libraryapi.model.entity.Loan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanRepository extends JpaRepository<Loan, Long> {


    boolean existsByBookAndNotReturned(Book book);
}
