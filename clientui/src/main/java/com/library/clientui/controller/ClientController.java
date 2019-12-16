package com.library.clientui.controller;

import com.library.clientui.beans.BookBean;
import com.library.clientui.proxies.MicroserviceBooksProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
public class ClientController {

    @Autowired
    private MicroserviceBooksProxy mBooksProxy;

    @RequestMapping("/")
    public String home(Model model) {

        List<BookBean> books = mBooksProxy.listBooks();

        model.addAttribute("books", books);

        return "Home";
    }
}
