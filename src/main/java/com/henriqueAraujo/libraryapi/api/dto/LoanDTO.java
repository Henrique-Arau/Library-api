package com.henriqueAraujo.libraryapi.api.dto;


import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanDTO {

    private Long id;
    private String isbn;
    private String customer;
    private BookDTO book;
}
