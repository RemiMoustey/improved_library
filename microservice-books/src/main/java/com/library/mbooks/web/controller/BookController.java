package com.library.mbooks.web.controller;

import com.library.mbooks.configurations.ApplicationPropertiesConfiguration;
import com.library.mbooks.dao.BookDao;
import com.library.mbooks.model.Book;
import com.library.mbooks.web.exceptions.BookNotFoundException;
import com.sun.org.apache.xpath.internal.operations.Mult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.ui.ModelMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
public class BookController {

    Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    BookDao bookDao;

    @GetMapping(value = "/livres")
    public List<Book> getListBooks() {
        List<Book> books = bookDao.findAll();

        if(books.isEmpty()) throw new BookNotFoundException("Aucun livre n'est disponible");

        return books;
    }

    @GetMapping(value = "/livres/{id}")
    public Book getBook(@PathVariable int id) {
        Book book = bookDao.findById(id);

        if(book == null) throw new BookNotFoundException("Le livre correspondant à l'id " + id + " n'existe pas.");

        return book;
    }

    @PostMapping(value = "/livres/resultats")
    public List<Book> getListSearchedBooks(@RequestParam String search) {
//        StringBuilder sb = new StringBuilder();
////
////        ((List<Character>) (paramMap.get("search"))).forEach(c -> sb.append(c.toString()));
////
////        String result = paramMap.toString();
////        List<String> listChar = (List<String>) paramMap.get("search");
////        String string = listChar.get(0);
//        List<String> listChar = (List<String>) paramMap.get("search");
//        String string = listChar.get(0);

        //String string = (new String (((List<char[]>) (paramMap.get("search"))).get(0)));

        return bookDao.findAllByTitle(search);

    }
}
