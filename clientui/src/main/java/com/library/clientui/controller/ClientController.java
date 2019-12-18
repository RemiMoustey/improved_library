package com.library.clientui.controller;

import com.library.clientui.beans.BookBean;
import com.library.clientui.beans.UserBean;
import com.library.clientui.proxies.MicroserviceBooksProxy;
import com.library.clientui.proxies.MicroserviceUsersProxy;
import feign.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import javax.jws.WebParam;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class ClientController {

    @Autowired
    private MicroserviceBooksProxy BooksProxy;

    @Autowired
    private MicroserviceUsersProxy UsersProxy;

    @RequestMapping("/")
    public String getHome(Model model) {
        List<BookBean> books = BooksProxy.getListBooks();
        model.addAttribute("books", books);
        return "Home";
    }

    @RequestMapping("details-book/{id}")
    public String getCardBook(@PathVariable int id, Model model) {
        BookBean book = BooksProxy.getBook(id);
        model.addAttribute("book", book);
        return "CardBook";
    }

    @RequestMapping("/register")
    public String getRegister(Model model) {
        return "Register";
    }

    @PostMapping("/validation")
    public void insertUser(HttpServletRequest request) {
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

    @PostMapping(value = "/livres/resultats")
    public String getResults(@RequestParam String search, Model model) {
        //StringBuilder sb = new StringBuilder();

        //((List<Character>) (paramMap.get("search"))).forEach(c -> sb.append(c.toString()));
//        List<String> listChar = (List<String>) paramMap.get("search");
//       String string = listChar.get(0);

        //String string = new String (((List<char[]>) (paramMap.get("search"))).get(0));

        List<BookBean> books = BooksProxy.getListSearchedBooks(search);
        model.addAttribute("books", books);



        return "Results";
    }
}
