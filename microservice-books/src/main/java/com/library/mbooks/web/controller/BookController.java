package com.library.mbooks.web.controller;

import com.library.mbooks.dao.BookDao;
import com.library.mbooks.model.Book;
import com.library.mbooks.web.exceptions.BookNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
public class BookController {

    Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    BookDao bookDao;

    @GetMapping(value = "/livres")
    public List<Book> getListBooks() {
        return bookDao.findAll();
    }

    @GetMapping(value = "/livres/{id}")
    public Book getBook(@PathVariable int id) {
        Book book = bookDao.findById(id);

        if(book == null) throw new BookNotFoundException("Le livre correspondant n'existe pas.");

        return book;
    }

    @PostMapping(value = "/livres/resultats")
    public List<Book> getListSearchedBooks(@RequestParam String search) {
        return bookDao.findAllByCriteria(search);
    }

    @GetMapping(value = "/liste_prets/{bookIds}")
    public List<Book> getListBooksOfLoans(@PathVariable List<Integer> bookIds) {
        List<Book> booksOfLoans = bookDao.findAllByIdIn(bookIds);

        if(booksOfLoans.isEmpty()) throw new BookNotFoundException("Aucun livre n'est disponible");

        return booksOfLoans;
    }

    @GetMapping(value = "/stock_baisse/{bookId}")
    public void updateStockBookDecrement(@PathVariable int bookId) {
        Book book = bookDao.findById(bookId);
        if(book == null) throw new BookNotFoundException("Le livre correspondant n'existe pas.");
        book.setCopies(book.getCopies() - 1);
        bookDao.save(book);
    }

    @RequestMapping(value="/stock_monte_batch/{bookId}", method = RequestMethod.PUT)
    public void updateBookBatch(@PathVariable int bookId, @RequestBody Book book) {
        bookDao.save(book);
    }

    @GetMapping(value = "/stock_monte/{bookId}")
    public void updateStockBookIncrement(@PathVariable int bookId) {
        Book book = bookDao.findById(bookId);
        if(book == null) throw new BookNotFoundException("Le livre correspondant n'existe pas.");
        book.setCopies(book.getCopies() + 1);
        bookDao.save(book);
    }
}
