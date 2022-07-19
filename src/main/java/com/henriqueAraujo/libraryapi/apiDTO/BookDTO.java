package com.henriqueAraujo.libraryapi.apiDTO;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookDTO {


    private Long id;
    private String title;
    private String author;
    private String isbn;
}


