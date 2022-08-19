package com.henriqueAraujo.libraryapi.service.impl;

import com.henriqueAraujo.libraryapi.exception.BusinesException;
import com.henriqueAraujo.libraryapi.model.entity.Loan;
import com.henriqueAraujo.libraryapi.model.repository.LoanRepository;
import com.henriqueAraujo.libraryapi.service.LoanService;

public class LoanServiceImpl implements LoanService {
    private LoanRepository repository;

    public LoanServiceImpl(LoanRepository repository) {
        this.repository = repository;
    }

    @Override
    public Loan save(Loan loan) {
        if(repository.existsByBookAndNotReturned(loan.getBook()) ){
           throw new BusinesException("Book already loaned");
        }
        return repository.save(loan);
    }
}
