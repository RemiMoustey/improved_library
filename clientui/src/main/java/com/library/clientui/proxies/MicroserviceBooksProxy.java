package com.library.clientui.proxies;

import com.library.clientui.beans.BookBean;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(contextId = "booksClient", name = "zuul-server")
@RibbonClient(name = "microservice-books")
public interface MicroserviceBooksProxy {
    @GetMapping(value = "/microservice-books/livres")
    List<BookBean> getListBooks();

    @GetMapping(value = "/microservice-books/livres/{id}")
    BookBean getBook(@PathVariable int id);

    @PostMapping(value = "/microservice-books/livres/resultats")
    List<BookBean> getListSearchedBooks(@RequestParam String search);

    @GetMapping(value = "/microservice-books/liste_prets/{bookIds}")
    List<BookBean> getListBooksOfLoans(@PathVariable String bookIds);

    @GetMapping(value = "/microservice-books/stock_baisse/{bookId}")
    void updateStockBookDecrement(@PathVariable int bookId);

    @GetMapping(value = "/microservice-books/stock_monte/{bookId}")
    void updateStockBookIncrement(@PathVariable int bookId);
}
