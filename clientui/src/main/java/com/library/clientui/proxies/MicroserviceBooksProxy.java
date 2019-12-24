package com.library.clientui.proxies;

import com.library.clientui.beans.BookBean;
import feign.Param;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "microservice-books", url = "localhost:9001")
public interface MicroserviceBooksProxy {
    @GetMapping(value = "/livres")
    List<BookBean> getListBooks();

    @GetMapping(value = "livres/{id}")
    BookBean getBook(@PathVariable int id);

    @PostMapping(value = "livres/resultats")
    List<BookBean> getListSearchedBooks(@RequestParam String search);

    @GetMapping(value = "/liste_prets/{bookIds}")
    List<BookBean> getListBooksOfLoans(@PathVariable List<Integer> bookIds);
}
