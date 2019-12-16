package com.library.mbooks.web.controller;

import com.library.mbooks.configurations.ApplicationPropertiesConfiguration;
import com.library.mbooks.dao.BookDao;
import com.library.mbooks.model.Book;
import com.library.mbooks.web.exceptions.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
public class BookController {

    Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    BookDao bookDao;

    @Autowired
    ApplicationPropertiesConfiguration appProperties;

    @GetMapping(value = "/Livres")
    public List<Book> listBooks() {
        List<Book> books = bookDao.findAll();

        if(books.isEmpty()) throw new BookNotFoundException("Aucun livre n'est disponible");

        return books;
    }

    @GetMapping(value = "/Livres/{id}")
    public Optional<Book> takeBook(@PathVariable int id) {
        Optional<Book> book = bookDao.findById(id);

        if(!book.isPresent()) throw new BookNotFoundException("Le livre correspondant à l'id " + id + " n'existe pas.");

        return book;
    }
}
