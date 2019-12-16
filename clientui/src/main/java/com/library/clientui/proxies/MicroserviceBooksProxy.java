package com.library.clientui.proxies;

import com.library.clientui.beans.BookBean;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "microservice-books", url = "localhost:9001")
public interface MicroserviceBooksProxy {

    @GetMapping(value = "/Livres")
    List<BookBean> listBooks();

    @GetMapping(value = "Livres/{id}")
    BookBean takeBook(@PathVariable("id") int id);
}
