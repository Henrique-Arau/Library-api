package com.henriqueAraujo.libraryapi.service.impl;

import com.henriqueAraujo.libraryapi.api.dto.LoanFilterDTO;
import com.henriqueAraujo.libraryapi.exception.BusinesException;
import com.henriqueAraujo.libraryapi.model.entity.Loan;
import com.henriqueAraujo.libraryapi.model.repository.LoanRepository;
import com.henriqueAraujo.libraryapi.service.LoanService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

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

    @Override
    public Optional<Loan> getById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Loan update(Loan loan) {
        return repository.save(loan);
    }

    @Override
    public Page<Loan> find(LoanFilterDTO filterDTO, Pageable pageable) {
        return repository.findByBookIsbnOrCustomer(filterDTO.getIsbn(), filterDTO.getCustomer(), pageable);
    }
}
