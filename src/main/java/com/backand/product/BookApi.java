package com.backand.product;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class BookApi {

    @GetMapping("/api/books")
    public List<Book> get() {

        List<Book> bookList = new ArrayList<>();
        bookList.add(new Book("Spring", "Pep"));
        bookList.add(new Book("ANgular", "Pep2"));

        return bookList;
    }
}
