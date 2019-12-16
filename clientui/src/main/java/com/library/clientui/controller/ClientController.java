package com.library.clientui.controller;

import com.library.clientui.beans.BookBean;
import com.library.clientui.beans.UserBean;
import com.library.clientui.proxies.MicroserviceBooksProxy;
import com.library.clientui.proxies.MicroserviceUsersProxy;
import feign.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

@Controller
public class ClientController {

    @Autowired
    private MicroserviceBooksProxy BooksProxy;

    @Autowired
    private MicroserviceUsersProxy UsersProxy;

    @RequestMapping("/")
    public String home(Model model) {
        List<BookBean> books = BooksProxy.listBooks();
        model.addAttribute("books", books);
        return "Home";
    }

    @RequestMapping("details-book/{id}")
    public String cardBook(@PathVariable int id, Model model) {
        BookBean book = BooksProxy.takeBook(id);
        model.addAttribute("book", book);
        return "CardBook";
    }

    @RequestMapping("/register")
    public String register(Model model) {
        return "Register";
    }

    @PostMapping("/validation")
    public void insertUser(HttpServletRequest request, Model model) {
        UserBean newUser = new UserBean();
        newUser.setLogin(request.getParameter("login"));
        newUser.setPassword(request.getParameter("password"));
        newUser.setEmail(request.getParameter("email"));
        newUser.setAddress(request.getParameter("address"));
        newUser.setPostalCode(Integer.parseInt(request.getParameter("postalCode")));
        newUser.setTown(request.getParameter("town"));
        newUser.setNumber(Integer.parseInt(request.getParameter("number")));
        newUser.setPhoneNumber(request.getParameter("phoneNumber"));
        UsersProxy.insertUser(newUser);
    }
}
