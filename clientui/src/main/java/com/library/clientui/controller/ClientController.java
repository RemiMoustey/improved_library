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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Controller
public class ClientController {

    @Autowired
    private MicroserviceBooksProxy BooksProxy;

    @Autowired
    private MicroserviceUsersProxy UsersProxy;

    @Autowired
    private MicroserviceLoansProxy LoansProxy;

    @GetMapping(value = "/livres")
    public String getHome(Model model) {
        List<BookBean> books = BooksProxy.getListBooks();
        model.addAttribute("books", books);
        return "Home";
    }

    @GetMapping(value = "/livres/{id}")
    public String getCardBook(@PathVariable int id, Model model) {
        BookBean book = BooksProxy.getBook(id);
        model.addAttribute("book", book);
        return "CardBook";
    }

    @GetMapping(value = "/inscription")
    public String getRegister(Model model) {
        return "Register";
    }

    @PostMapping(value = "/validation")
    public void insertUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
        UserBean newUser = new UserBean();
        newUser.setUsername(request.getParameter("username"));
        newUser.setPassword(request.getParameter("password"));
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
    public String getLoginPage() {
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
        response.sendRedirect("/");
    }

    @GetMapping(value = "/retour_pret/{id}/{bookId}")
    public void deleteLoan(@PathVariable int id, @PathVariable int bookId, HttpServletResponse response) throws IOException {
        LoansProxy.deleteLoan(id, bookId);
        response.sendRedirect("/stock_monte/" + bookId);
    }

    @GetMapping(value = "/stock_monte/{bookId}")
    public void updateStockBookIncrement(@PathVariable int bookId, HttpServletResponse response) throws IOException {
        BooksProxy.updateStockBookIncrement(bookId);
        response.sendRedirect("/");
    }

    @GetMapping(value = "/prets/{userId}")
    public void getLoans(@PathVariable int userId, HttpServletRequest request, HttpServletResponse response) throws IOException {
        List<LoanBean> loans = LoansProxy.getLoans(userId);
        request.getSession().setAttribute("loans", loans);
        String redirection = "/liste_prets/";
        for (int i = 0; i < loans.size(); i++) {
            redirection += loans.get(i).getBookId();
            if(i < loans.size() - 1) {
                redirection += ",";
            }
        }
        response.sendRedirect(redirection);
    }

    @GetMapping(value = "/liste_prets/{bookIds}")
    public String getListBooksOfLoans(@PathVariable String bookIds, HttpServletRequest request, Model model) {
        List<BookBean> booksOfLoans = BooksProxy.getListBooksOfLoans(bookIds);
        model.addAttribute("loans", request.getSession().getAttribute("loans"));
        model.addAttribute("booksOfLoans", booksOfLoans);
        return "LoansList";
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
        response.sendRedirect("/");
    }

    @GetMapping(value = "/logout")
    public String getLogoutPage() {
        return "Login";
    }

}