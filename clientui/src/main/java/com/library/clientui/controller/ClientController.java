package com.library.clientui.controller;

import com.library.clientui.beans.BookBean;
import com.library.clientui.beans.LoanBean;
import com.library.clientui.beans.UserBean;
import com.library.clientui.proxies.MicroserviceBooksProxy;
import com.library.clientui.proxies.MicroserviceLoansProxy;
import com.library.clientui.proxies.MicroserviceUsersProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Controller
public class ClientController {

    @Autowired
    private MicroserviceBooksProxy BooksProxy;

    @Autowired
    private MicroserviceUsersProxy UsersProxy;

    @Autowired
    private MicroserviceLoansProxy LoansProxy;

    @GetMapping(value = "/")
    public String getHome(Model model) {
        List<BookBean> books = BooksProxy.getListBooks();
        model.addAttribute("books", books);
        return "Home";
    }

    @GetMapping(value = "details-book/{id}")
    public String getCardBook(@PathVariable int id, Model model) {
        BookBean book = BooksProxy.getBook(id);
        model.addAttribute("book", book);
        return "CardBook";
    }

    @GetMapping(value = "/register")
    public String getRegister(Model model) {
        return "Register";
    }

    @PostMapping(value = "/validation")
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
        List<BookBean> books = BooksProxy.getListSearchedBooks(search);
        model.addAttribute("books", books);
        return "Results";
    }

    @GetMapping(value = "/login")
    public String getLoginPage() {
        return "Login";
    }

    @PostMapping(value = "/validation_connection")
    public void getUserByLogin(@RequestParam String login, @RequestParam String password, HttpServletRequest request, HttpServletResponse response, Model model) {
        UserBean user = UsersProxy.getUserByLoginAndPassword(login, password);
        if(user != null) {
            model.addAttribute("connectedUser", user);
            request.getSession().setAttribute("connectedUser", user.getId());
            try {
                response.sendRedirect("?connected=true");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            request.getSession().setAttribute("connectionError", "Identifiants inconnus");
            try {
                response.sendRedirect("/login");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @PostMapping(value = "/nouveau_pret")
    public void insertLoan(HttpServletRequest request, HttpServletResponse response) {
        LoanBean newLoan = new LoanBean();
        newLoan.setUserId(Integer.parseInt(request.getParameter("userId")));
        newLoan.setBookId(Integer.parseInt(request.getParameter("bookId")));
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(Calendar.getInstance().getTime());
        calendar.add(Calendar.DAY_OF_YEAR, 28);
        newLoan.setDeadline(calendar.getTime());
        LoansProxy.insertLoan(newLoan);
        try {
            response.sendRedirect("/");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @GetMapping(value = "/prets/{userId}")
    public void getLoans(@PathVariable int userId, HttpServletRequest request, HttpServletResponse response) {
        List<LoanBean> loans = LoansProxy.getLoans(userId);
        request.getSession().setAttribute("loans", loans);
        String redirection = "/liste_prets/";
        for (int i = 0; i < loans.size(); i++) {
            redirection += loans.get(i).getBookId();
            if(i < loans.size() - 1) {
                redirection += ",";
            }
        }
        try {
            response.sendRedirect(redirection);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @GetMapping(value = "/liste_prets/{bookIds}")
    public String getListBooksOfLoans(@PathVariable String bookIds, HttpServletRequest request, Model model) {
        List<BookBean> booksOfLoans = BooksProxy.getListBooksOfLoans(bookIds);
        model.addAttribute("loans", request.getSession().getAttribute("loans"));
        model.addAttribute("booksOfLoans", booksOfLoans);
        return "LoansList";
    }
}