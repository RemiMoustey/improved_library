package com.library.clientui.controller;

import com.library.clientui.beans.BookBean;
import com.library.clientui.beans.LoanBean;
import com.library.clientui.beans.UserBean;
import com.library.clientui.proxies.MicroserviceBooksProxy;
import com.library.clientui.proxies.MicroserviceLoansProxy;
import com.library.clientui.proxies.MicroserviceUsersProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
public class ClientController {

    @Autowired
    private MicroserviceBooksProxy BooksProxy;

    @Autowired
    private MicroserviceUsersProxy UsersProxy;

    @Autowired
    private MicroserviceLoansProxy LoansProxy;

    public void catchLoggedUserId(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        RestTemplate restTemplate = new RestTemplate();
        UserBean loggedUser = restTemplate.getForObject("http://localhost:9002/utilisateur/" + username, UserBean.class);
        if(loggedUser != null) {
            model.addAttribute("userId", loggedUser.getId());
        }
    }

    @GetMapping(value = "/livres")
    public String getHome(Model model) {
        List<BookBean> books = BooksProxy.getListBooks();
        model.addAttribute("books", books);
        catchLoggedUserId(model);
        return "Home";
    }

    @GetMapping(value = "/livres/{id}")
    public String getCardBook(@PathVariable int id, Model model) {
        BookBean book = BooksProxy.getBook(id);
        model.addAttribute("book", book);
        catchLoggedUserId(model);
        return "CardBook";
    }

    @GetMapping(value = "/inscription")
    public String getRegister(Model model) {
        catchLoggedUserId(model);
        return "Register";
    }

    @PostMapping(value = "/validation")
    public void insertUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
        UserBean newUser = new UserBean();
        newUser.setUsername(request.getParameter("username"));
        newUser.setPassword(new BCryptPasswordEncoder().encode(request.getParameter("password")));
        newUser.setEmail(request.getParameter("email"));
        newUser.setAddress(request.getParameter("address"));
        newUser.setPostalCode(Integer.parseInt(request.getParameter("postalCode")));
        newUser.setTown(request.getParameter("town"));
        newUser.setNumber(Integer.parseInt(request.getParameter("number")));
        newUser.setPhoneNumber(request.getParameter("phoneNumber"));
        UsersProxy.insertUser(newUser);
        response.sendRedirect("/livres");
    }

    @PostMapping(value = "/livres/resultats")
    public String getResults(@RequestParam String search, Model model) {
        List<BookBean> books = BooksProxy.getListSearchedBooks(search);
        model.addAttribute("books", books);
        return "Results";
    }

    @GetMapping(value = "/login")
    public String getLoginPage(Model model) {
        catchLoggedUserId(model);
        return "Login";
    }

    @PostMapping(value = "/nouveau_pret")
    public void insertLoan(HttpServletRequest request, HttpServletResponse response) throws IOException {
        LoanBean newLoan = new LoanBean();
        newLoan.setUserId(Integer.parseInt(request.getParameter("userId")));
        newLoan.setBookId(Integer.parseInt(request.getParameter("bookId")));
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(Calendar.getInstance().getTime());
        calendar.add(Calendar.DAY_OF_YEAR, 28);
        newLoan.setDeadline(calendar.getTime());
        LoansProxy.insertLoan(newLoan);
        response.sendRedirect("/stock_baisse/" + request.getParameter("bookId"));
    }

    @GetMapping(value = "/stock_baisse/{bookId}")
    public void updateStockBookDecrement(@PathVariable int bookId, HttpServletResponse response) throws IOException {
        BooksProxy.updateStockBookDecrement(bookId);
        response.sendRedirect("/livres");
    }

    @GetMapping(value = "/retour_pret/{id}/{bookId}")
    public void deleteLoan(@PathVariable int id, @PathVariable int bookId, HttpServletResponse response) throws IOException {
        LoansProxy.deleteLoan(id, bookId);
        response.sendRedirect("/stock_monte/" + bookId);
    }

    @GetMapping(value = "/stock_monte/{bookId}")
    public void updateStockBookIncrement(@PathVariable int bookId, HttpServletResponse response) throws IOException {
        BooksProxy.updateStockBookIncrement(bookId);
        response.sendRedirect("/livres");
    }

    @GetMapping(value = "/prets/{userId}")
    public void getLoans(@PathVariable int userId, HttpServletRequest request, HttpServletResponse response) throws IOException {
        RestTemplate restTemplate = new RestTemplate();
        List<LoanBean> allLoans = Arrays.asList(restTemplate.getForEntity("http://localhost:9003/tous_les_prets", LoanBean[].class).getBody());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        UserBean loggedUser = restTemplate.getForObject("http://localhost:9002/utilisateur/" + username, UserBean.class);
        boolean hasLoanForUser = false;
        for(LoanBean loan : allLoans) {
            hasLoanForUser = (loan.getUserId() == userId) ? true : false;
            if(hasLoanForUser) {
                break;
            }
        }
        if(hasLoanForUser) {
            List<LoanBean> loans = LoansProxy.getLoans(userId);
            request.getSession().setAttribute("loans", loans);
            String redirection = "/liste_prets/";
            for (int i = 0; i < loans.size(); i++) {
                redirection += loans.get(i).getBookId();
                if (i < loans.size() - 1) {
                    redirection += ",";
                }
            }
            response.sendRedirect(redirection);
        } else if(loggedUser.getId() != userId) {
            response.sendRedirect("/acces_refuse");
        }
        else {
            response.sendRedirect("/aucun_pret");
        }
    }

    @GetMapping(value = "/liste_prets/{bookIds}")
    public String getListBooksOfLoans(@PathVariable String bookIds, HttpServletRequest request, Model model) {
        List<BookBean> booksOfLoans = BooksProxy.getListBooksOfLoans(bookIds);
        model.addAttribute("loans", request.getSession().getAttribute("loans"));
        model.addAttribute("booksOfLoans", booksOfLoans);
        catchLoggedUserId(model);
        return "LoansList";
    }

    @GetMapping(value = "/aucun_pret")
    public String getNoLoans(Model model) {
        catchLoggedUserId(model);
        return "NoLoans";
    }

    @PostMapping(value = "/prolongation")
    public void updateExtendedLoan(HttpServletRequest request, HttpServletResponse response) throws IOException {
        LoanBean updatedLoan = new LoanBean(Integer.parseInt(request.getParameter("id")));
        updatedLoan.setUserId(Integer.parseInt(request.getParameter("userId")));
        updatedLoan.setBookId(Integer.parseInt(request.getParameter("bookId")));
        Calendar calendar = Calendar.getInstance();
        try {
            Date date = new SimpleDateFormat("EE MMM dd HH:mm:ss z yyyy",
                    Locale.ENGLISH).parse(request.getParameter("deadline"));
            calendar.setTime(date);
            calendar.add(Calendar.DAY_OF_YEAR, 28);
            updatedLoan.setDeadline(calendar.getTime());
            updatedLoan.setExtended(true);
            LoansProxy.updateExtendedLoan(updatedLoan);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        response.sendRedirect("/livres");
    }

    @GetMapping(value = "/logout")
    public String getLogoutPage(Model model) {
        catchLoggedUserId(model);
        return "Login";
    }

    @GetMapping(value = "acces_refuse")
    public String getDeclinedAccess(Model model) {
        catchLoggedUserId(model);
        return "DeclinedAccess";
    }

}