package com.henriqueAraujo.libraryapi.service;


import com.henriqueAraujo.libraryapi.api.dto.LoanFilterDTO;
import com.henriqueAraujo.libraryapi.exception.BusinessException;
import com.henriqueAraujo.libraryapi.model.entity.Book;
import com.henriqueAraujo.libraryapi.model.entity.Loan;
import com.henriqueAraujo.libraryapi.model.repository.LoanRepository;
import com.henriqueAraujo.libraryapi.service.impl.LoanServiceImpl;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
                .isInstanceOf(BusinessException.class)
                .hasMessage("Book already loaned");

        verify(repository, never()).save(savingLoan);
    }

    @Test
    @DisplayName("Deve obter as informações de um emprestimo pelo ID")
    public void getLoanDetaisTest() {
        //cenario
        Long id = 1L;

        Loan loan = createLoan();
        loan.setId(id);

        Mockito.when(repository.findById(id)).thenReturn(Optional.of(loan));

        //execucao
        Optional<Loan> result = service.getById(id);

        //verificação
        assertThat(result.isPresent()).isTrue();
        assertThat(result.get().getId()).isEqualTo(id);
        assertThat(result.get().getCustomer()).isEqualTo(loan.getCustomer());
        assertThat(result.get().getBook()).isEqualTo(loan.getBook());
        assertThat(result.get().getLoanDate()).isEqualTo(loan.getLoanDate());


        verify(repository).findById(id);
    }

    @Test
    @DisplayName("Deve atualizar um emprestimo.")
    public void updateLoanTest() {
        Loan loan = createLoan();
        loan.setId(1L);
        loan.setReturned(true);

        Mockito.when( repository.save(loan) ).thenReturn( loan );

        Loan updatedLoan = service.update(loan);

        assertThat(updatedLoan.getReturned()).isTrue();
        verify(repository).save(loan);

    }

    @Test
    @DisplayName("Deve filtrar emprestimos pelas propriedades")
    public void findLoanTest() {
        //cenário

        LoanFilterDTO loanFilterDTO = LoanFilterDTO.builder().customer("Fulano").isbn("321").build();

        Loan loan = createLoan();
        loan.setId(1L);

        PageRequest pageRequest = PageRequest.of(0, 10);

        List<Loan> lista = Arrays.asList(loan);
        Page<Loan> page = new PageImpl<Loan>(lista, pageRequest, lista.size());
        Mockito.when( repository.findByBookIsbnOrCustomer(
                        Mockito.anyString(),
                        Mockito.anyString(),
                        Mockito.any(PageRequest.class)))
                .thenReturn(page);

        //execução
        Page<Loan> result = service.find(loanFilterDTO, pageRequest);

        //verificações
        AssertionsForClassTypes.assertThat( result.getTotalElements()).isEqualTo(1);
        AssertionsForClassTypes.assertThat( result.getContent()).isEqualTo(lista);
        AssertionsForClassTypes.assertThat( result.getPageable().getPageNumber()).isEqualTo(0);
        AssertionsForClassTypes.assertThat( result.getPageable().getPageSize()).isEqualTo(10);
    }


    public static Loan createLoan() {
        Book book = Book.builder().id(1L).build();
        String customer = "Fulano";

        return Loan.builder()
                .book(book)
                .customer(customer)
                .loanDate(LocalDate.now())
                .build();
    }
}
