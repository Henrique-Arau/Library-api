package com.henriqueAraujo.libraryapi.service;


import com.henriqueAraujo.libraryapi.exception.BusinesException;
import com.henriqueAraujo.libraryapi.model.entity.Book;
import com.henriqueAraujo.libraryapi.model.entity.Loan;
import com.henriqueAraujo.libraryapi.model.repository.LoanRepository;
import com.henriqueAraujo.libraryapi.service.impl.LoanServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class LoanServiceTest {

    LoanService service;
    @MockBean
    LoanRepository repository;

    @BeforeEach
    public void setUp() {
        this.service = new LoanServiceImpl(repository);

    }


    @Test
    @DisplayName("Deve salvar um emprestimo")
    public void saveLoanTest() {

        Book book = Book.builder().id(1L).build();
        String customer = "Fulano";

        Loan savingLoan = Loan.builder()
                .book(Book.builder().build())
                .customer("Fulano")
                .loanDate(LocalDate.now())
                .build();

        Loan savedLoan = Loan.builder()
                .id(1L)
                .loanDate(LocalDate.now())
                .customer(customer)
                .book(book).build();

        Mockito.when(repository.existsByBookAndNotReturned(book)).thenReturn(false);
        Mockito.when(repository.save(savingLoan)).thenReturn(savedLoan);

        Loan loan = service.save(savingLoan);

        assertThat(loan.getId()).isEqualTo((savedLoan.getId()));
        assertThat(loan.getBook().getId()).isEqualTo((savedLoan.getBook().getId()));
        assertThat(loan.getCustomer()).isEqualTo((savedLoan.getCustomer()));
        assertThat(loan.getLoanDate()).isEqualTo(savedLoan.getLoanDate());

    }

    @Test
    @DisplayName("Deve lançar erro de negocio ao salvar um  emprestimo com livro já emprestado")
    public void loanedBookSaveTest() {

        Book book = Book.builder().id(1L).build();
        String customer = "Fulano";

        Loan savingLoan = Loan.builder()
                .book(book)
                .customer(customer)
                .loanDate(LocalDate.now())
                .build();

        Mockito.when(repository.existsByBookAndNotReturned(book)).thenReturn(true);


        Throwable exception = catchThrowable(() -> service.save(savingLoan));


        assertThat(exception)
                .isInstanceOf(BusinesException.class)
                .hasMessage("Book already loaned");

        verify(repository, never()).save(savingLoan);
    }
}
