package com.books;


import com.books.controllers.BookController;
import com.books.model.Book;
import com.books.service.BookService;
import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.Optional;

import static org.hamcrest.core.StringContains.containsString;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(classes = BookManagerApplication.class)
public class BookControllerTest {

    @MockBean
    private BookService bookService;

    @InjectMocks
    private BookController bookController;

    @Autowired
    private MockMvc mockMvc;



    @WithMockUser("USER")
    @Test
    public void getAllBooks() throws Exception {

        ArrayList<Book> allBooks = new ArrayList<>();
        allBooks.add(new Book("Book Title", "Book Author", "Book Summary 22", 2 ));
        allBooks.add(new Book("Book Title Second", "Book Author Second", "Book Summary 23 Second", 2 ));

        when(bookService.getAllBooksByPage(anyInt(),anyInt())).thenReturn(allBooks);
        String uri = UriComponentsBuilder.newInstance().path("/library/books").build().toUriString();

        mockMvc.perform(get(uri)).andDo(print()).andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Title")))
                .andExpect(content().string(containsString("Author")))
                .andExpect(content().string(containsString("Summary")))
                .andExpect(content().string(containsString("Second")))
                .andExpect(content().string(containsString("23")));
    }

    @WithMockUser("USER")
    @Test
    public void getAllBooksByTitleContaining() throws Exception {

        ArrayList<Book> allBooks = new ArrayList<>();
        allBooks.add(new Book("Book Title", "Book Author", "Book Summary 22", 2 ));
        allBooks.add(new Book("Book Title Second", "Book Author Second", "Book Summary 23 Second", 2 ));

        when(bookService.getBookByTitleContaining("Title",0,20)).thenReturn(allBooks);

        String uri = UriComponentsBuilder.newInstance().path("/library/books").query("titleContaining={titleContaining}")
                .buildAndExpand("Title").toUriString();

        mockMvc.perform(get(uri)).andDo(print()).andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Title")))
                .andExpect(content().string(containsString("Author")))
                .andExpect(content().string(containsString("Summary")))
                .andExpect(content().string(containsString("Second")))
                .andExpect(content().string(containsString("23")));
    }

    @WithMockUser("USER")
    @Test
    public void getSingleBook() throws Exception {
        int id = 1;
        Book book = new Book("Book Title Second",
                "Book Author Second", "Book Summary 23 Second", 2 );
        when(bookService.getBookById(id)).thenReturn(Optional.of(book));
        String uri = UriComponentsBuilder.newInstance().path("/library/books/{id}")
                .buildAndExpand(id).toUriString();

        mockMvc.perform(get(uri)).andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andExpect(content().string(containsString("Title")))
                .andExpect(content().string(containsString("Second")))
                .andExpect(content().string(containsString("Summary")))
                .andReturn();

    }

    @WithMockUser("USER")
    @Test
    public void deleteSingleQuote() throws Exception {
        String uri = UriComponentsBuilder.newInstance().path("/library/books/{id}")
                .buildAndExpand(1).toUriString();
        mockMvc.perform(delete(uri)).andExpect(status().isOk());

    }

    @WithMockUser("USER")
    @Test
    public void saveBook() throws Exception {
        int id = 1;
        Book book = new Book("Book Title Third",
                "Book Author Third", "Book Summary 34 Third", 3 );

        String json = new Gson().toJson(book);
        String uri = UriComponentsBuilder.newInstance().path("/library/books/")
                .build().toUriString();
        mockMvc.perform(post(uri).content(json).accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)).andDo(print())
                .andExpect(status().isOk());
    }

    @WithMockUser("USER")
    @Test
    public void addRating() throws Exception {
        String uri = UriComponentsBuilder.newInstance().path("/library/books/1/add_rating")
                .build().toUriString();
        mockMvc.perform(post(uri)).andDo(print())
                .andExpect(status().isOk());
    }

    @WithMockUser("USER")
    @Test
    public void removeRating() throws Exception {
        String uri = UriComponentsBuilder.newInstance().path("/library/books/1/remove_rating")
                .build().toUriString();
        mockMvc.perform(post(uri)).andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void getSingleBook_NotLoggedIn401() throws Exception {
        String uri = UriComponentsBuilder.newInstance().path("/library/books/{id}")
                .buildAndExpand("1").toUriString();

        mockMvc.perform(get(uri)).andDo(print())
                .andExpect(status().isUnauthorized())
                .andReturn();
    }

}
