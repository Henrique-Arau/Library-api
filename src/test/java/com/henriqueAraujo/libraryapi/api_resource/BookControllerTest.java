package com.henriqueAraujo.libraryapi.api_resource;




import com.fasterxml.jackson.databind.ObjectMapper;
import com.henriqueAraujo.libraryapi.apiDTO.BookDTO;
import com.henriqueAraujo.libraryapi.exception.BusinesException;
import com.henriqueAraujo.libraryapi.model.entity.Book;
import com.henriqueAraujo.libraryapi.service.BookService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;


import java.util.Optional;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest
@AutoConfigureMockMvc
public class BookControllerTest {

    static String BOOK_API = "/api/books";

    @Autowired
    MockMvc mvc;
    @MockBean
    BookService service;


 @Test
 @DisplayName("Deve criar um livro com sucesso")
 public void createBookTest() throws Exception{

     BookDTO dto = createNewBook();
     Book savedBook = Book.builder().id(10L).author("Arthur").title("As aventuras").isbn("001").build();

     BDDMockito.given(service.save(Mockito.any(Book.class))).willReturn(savedBook);
     String json = new ObjectMapper().writeValueAsString(dto);

     MockHttpServletRequestBuilder request = MockMvcRequestBuilders
          .post(BOOK_API)
          .contentType(MediaType.APPLICATION_JSON)
          .accept(MediaType.APPLICATION_JSON)
          .content(json);

  mvc
          .perform(request)
          .andExpect(status().isCreated() )
          .andExpect(jsonPath("id").value(10L))
          .andExpect(jsonPath("title").value(dto.getTitle()) )
          .andExpect(jsonPath("author").value(dto.getAuthor()) )
          .andExpect(jsonPath("isbn").value(dto.getIsbn()) )

          ;
 }

 @Test
 @DisplayName("Deve lançar erro de validação quando não houver dados suficientes para criação do livro")
 public void createInvalidBookTest() throws Exception {

     String json = new ObjectMapper().writeValueAsString(new BookDTO());

     MockHttpServletRequestBuilder request = MockMvcRequestBuilders
             .post(BOOK_API)
             .contentType(MediaType.APPLICATION_JSON)
             .accept(MediaType.APPLICATION_JSON)
             .content(json);

     mvc
             .perform(request)
             .andExpect(status().isBadRequest())
             .andExpect(jsonPath("errors", hasSize(3)));

 }

 @Test
 @DisplayName("Deve lançar erro ao tentar cadastrar um livro com isbn já utilizado por outro.")
 public void createBookWithDuplicatesIsbn() throws Exception {

     BookDTO dto = createNewBook();
     String json = new ObjectMapper().writeValueAsString(dto);
     String mensagemErro = "Isbn já cadastrado. ";
     BDDMockito.given(service.save(Mockito.any(Book.class)))
             .willThrow(new BusinesException(mensagemErro));

     MockHttpServletRequestBuilder request = MockMvcRequestBuilders
             .post(BOOK_API)
             .contentType(MediaType.APPLICATION_JSON)
             .accept(MediaType.APPLICATION_JSON)
             .content(json);

     mvc.perform( request )
             .andExpect(status().isBadRequest())
             .andExpect(jsonPath("errors", hasSize(1)))
             .andExpect(jsonPath("errors[0]").value(mensagemErro));
 }

 @Test
 @DisplayName("Deve obter informacoes de um livro")
 public void getBookDetailsTest() throws Exception {
     //cenário (given)
     Long id = 1L;

     Book book = Book.builder()
             .id(id)
             .title(createNewBook().getTitle())
             .author(createNewBook().getAuthor())
             .isbn(createNewBook().getIsbn())
             .build();
     BDDMockito.given(service.getById(id)).willReturn(Optional.of(book));

     //executar (when)
     MockHttpServletRequestBuilder request = MockMvcRequestBuilders
             .get(BOOK_API.concat("/" + id))
             .accept(MediaType.APPLICATION_JSON);

     mvc
             .perform(request)
             .andExpect(status().isOk())
             .andExpect( jsonPath("id").value(id))
             .andExpect( jsonPath("title").value(createNewBook().getTitle()))
             .andExpect( jsonPath("author").value(createNewBook().getAuthor()))
             .andExpect( jsonPath("isbn").value(createNewBook().getIsbn()));

 }

 @Test
 @DisplayName("Deve retornar resource not found quando o livro procurado não existir")
 public void bookNotFoundTest() throws  Exception {


     BDDMockito.given( service.getById((anyLong())) ).willReturn( Optional.empty());

     MockHttpServletRequestBuilder request = MockMvcRequestBuilders
             .get(BOOK_API.concat("/" + 1))
             .accept(MediaType.APPLICATION_JSON);

     mvc
             .perform(request)
             .andExpect(status().isNotFound());
 }

 @Test
 @DisplayName("Deve deletar um livro")
 public void deleteBookTest() throws Exception {

     BDDMockito.given(service.getById(anyLong())).willReturn(Optional.of(Book.builder().id(1L).build()));

     MockHttpServletRequestBuilder request = MockMvcRequestBuilders
             .delete(BOOK_API.concat("/" + 1));


     mvc.perform( request )
             .andExpect( status().isNoContent());

 }

    @Test
    @DisplayName("Deve retorner resource not found quando não encontrar o livro para deletar")
    public void deleteInexistentBookTest() throws Exception {

        BDDMockito.given(service.getById(anyLong())).willReturn(Optional.empty());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(BOOK_API.concat("/" + 1));


        mvc.perform( request )
                .andExpect( status().isNotFound());

    }

 private BookDTO createNewBook() {
     return BookDTO.builder().author("Arthur").title("As aventuras").isbn("001").build();
 }


}
