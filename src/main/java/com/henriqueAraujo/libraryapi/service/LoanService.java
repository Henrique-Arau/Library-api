package com.henriqueAraujo.libraryapi.service;


import com.henriqueAraujo.libraryapi.model.entity.Loan;

import java.util.Optional;

public interface LoanService {


    Loan save(Loan loan);

    Optional<Loan> getById(Long id);

    Loan update(Loan loan);
}
