package com.infosys;

import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/book")
public class Controller {

    private List<Book> books = new ArrayList<>();

    @PostMapping("/post")
    public Book createBook(@RequestBody Book book)
    {
        books.add(book);
        return book;
    }

    @GetMapping("/get")
    public List<Book> createBook()
    {
         return books;
    }

}
