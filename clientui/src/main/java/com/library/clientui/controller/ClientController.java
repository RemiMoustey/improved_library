package com.library.clientui.controller;

import com.library.clientui.beans.BookBean;
import com.library.clientui.beans.LoanBean;
import com.library.clientui.beans.ReservationBean;
import com.library.clientui.beans.UserBean;
import com.library.clientui.proxies.MicroserviceBooksProxy;
import com.library.clientui.proxies.MicroserviceLoansProxy;
import com.library.clientui.proxies.MicroserviceReservationsProxy;
import com.library.clientui.proxies.MicroserviceUsersProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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

    @Autowired
    private MicroserviceReservationsProxy ReservationsProxy;

    public void catchLoggedUserId(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        RestTemplate restTemplate = new RestTemplate();
        UserBean loggedUser = restTemplate.getForObject("http://localhost:9002/utilisateur/" + username, UserBean.class);
        if(loggedUser != null) {
            model.addAttribute("username", username);
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
        if(book.getCopies() == 0 && model.getAttribute("userId") != null) {
            model.addAttribute("numberAllCopies", Arrays.asList(new RestTemplate().getForEntity("http://localhost:9003/tous_les_prets/" + book.getId(), LoanBean[].class).getBody()).size());
            model.addAttribute("numberReservationsForTheBook", Arrays.asList(new RestTemplate().getForEntity("http://localhost:9004/reservations/" + book.getId(), LoanBean[].class).getBody()).size());
            List<LoanBean> loansOfUser = Arrays.asList(new RestTemplate().getForEntity("http://localhost:9003/prets/" + model.getAttribute("userId"), LoanBean[].class).getBody());
            boolean alreadyLent = false;
            for(LoanBean loanOfUser : loansOfUser) {
                if(loanOfUser.getBookId() == book.getId()) {
                    alreadyLent = true;
                    break;
                }
            }
            if(alreadyLent) {
                model.addAttribute("alreadyLent", true);
            } else {
                model.addAttribute("alreadyLent", false);
            }
        }
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
    public void getResults(@RequestParam String search, HttpServletRequest request, HttpServletResponse response) throws IOException {
        List<BookBean> books = BooksProxy.getListSearchedBooks(search);
        request.getSession().setAttribute("books", books);
        response.sendRedirect("/resultats_recherche/" + search);
    }

    @GetMapping(value = "/resultats_recherche/{search}")
    public String printResults(@PathVariable String search, Model model, HttpServletRequest request) {
        if(request.getSession().getAttribute("books") == null) {
            return "DeclinedAccess";
        }
        model.addAttribute("books", request.getSession().getAttribute("books"));
        Map<Integer, Date> firstReturnDatesBook = new HashMap<>();
        Map<Integer, Integer> numberUsersWhoReserved = new HashMap<>();
        for(BookBean book : (List<BookBean>) request.getSession().getAttribute("books")) {
            List<LoanBean> listLoans = Arrays.asList(new RestTemplate().getForEntity("http://localhost:9003/tous_les_prets/" + book.getId(), LoanBean[].class).getBody());
            List<Date> dates = new ArrayList<>();
            for(LoanBean loan : listLoans) {
                dates.add(loan.getDeadline());
            }
            if(dates.size() != 0) {
                firstReturnDatesBook.put(book.getId(), Collections.min(dates));
            }
            List<ReservationBean> listUsersWhoReserved = Arrays.asList(new RestTemplate().getForEntity("http://localhost:9004/reservations/" + book.getId(), ReservationBean[].class).getBody());
            numberUsersWhoReserved.put(book.getId(), listUsersWhoReserved.size());
        }
        model.addAttribute("firstReturnDatesBook", firstReturnDatesBook);
        model.addAttribute("numberUsersWhoReserved", numberUsersWhoReserved);
        request.getSession().removeAttribute("books");
        model.addAttribute("search", search);
        catchLoggedUserId(model);
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
        RestTemplate restTemplate = new RestTemplate();
        List<ReservationBean> listReservations = Arrays.asList(restTemplate.getForEntity("http://localhost:9004/reservations", ReservationBean[].class).getBody());
        boolean isReserved = false;
        for(ReservationBean reservation : listReservations) {
            if(reservation.getBookId() == bookId) {
                isReserved = true;
                break;
            }
        }
        if(isReserved) {
            response.sendRedirect("/priorite_baisse/" + bookId);
        } else {
            BooksProxy.updateStockBookIncrement(bookId);
            response.sendRedirect("/livres");
        }
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
    public void updateExtendedLoan(HttpServletRequest request, HttpServletResponse response) throws IOException, ParseException {
        LoanBean updatedLoan = new LoanBean(Integer.parseInt(request.getParameter("id")));
        updatedLoan.setUserId(Integer.parseInt(request.getParameter("userId")));
        updatedLoan.setBookId(Integer.parseInt(request.getParameter("bookId")));
        updatedLoan.setDeadline(new SimpleDateFormat("EE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH).parse(request.getParameter("deadline")));
        LoansProxy.updateExtendedLoan(updatedLoan);

        response.sendRedirect("/livres");
    }

    @GetMapping(value = "/logout")
    public String getLogoutPage(Model model) {
        catchLoggedUserId(model);
        return "Login";
    }

    @GetMapping(value = "/acces_refuse")
    public String getDeclinedAccess(Model model) {
        catchLoggedUserId(model);
        return "DeclinedAccess";
    }

    @PostMapping(value = "/reserve_book")
    public void insertNewReservation(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ReservationBean newReservation = new ReservationBean();
        newReservation.setBookId(Integer.parseInt(request.getParameter("bookId")));
        newReservation.setUserId(Integer.parseInt(request.getParameter("userId")));
        RestTemplate restTemplate = new RestTemplate();
        List<ReservationBean> listReservations = Arrays.asList(restTemplate.getForEntity("http://localhost:9004/reservations", ReservationBean[].class).getBody());
        int priority = 1;
        if (listReservations != null) {
            for (ReservationBean reservation : listReservations) {
                if (reservation.getBookId() == newReservation.getBookId()) {
                    priority++;
                }
            }
        }
        newReservation.setPriority(priority);
        ReservationsProxy.insertReservation(newReservation);

        response.sendRedirect("/livres?reservation=true");
    }

    @GetMapping(value = "/priorite_baisse/{bookId}")
    public void updatePriorityReservations(@PathVariable int bookId, HttpServletResponse response) throws IOException, MessagingException {
        ReservationsProxy.updatePriorityReservations(bookId);
        List<ReservationBean> listReservations = Arrays.asList(new RestTemplate().getForEntity("http://localhost:9004/reservations", ReservationBean[].class).getBody());
        for(ReservationBean reservation : listReservations) {
            BookBean reservedBook = BooksProxy.getBook(bookId);
            UserBean user = new RestTemplate().getForObject("http://localhost:9002/utilisateur_id/" + reservation.getUserId(), UserBean.class);
            if(reservation.getPriority() == 0) {
                readPropertiesAndSend(user.getEmail(), reservedBook.getTitle());
                break;
            }
        }
        response.sendRedirect("/livres");
    }

    @GetMapping(value = "/reservations_user/{userId}")
    public String getReservations(@PathVariable int userId, Model model) {
        List<ReservationBean> listReservationsUser = ReservationsProxy.getReservationsUser(userId);
        List<BookBean> listReservedBooks = new ArrayList<>();
        for(ReservationBean reservationUser : listReservationsUser) {
            listReservedBooks.add(new RestTemplate().getForObject("http://localhost:9001/livres/" + reservationUser.getBookId(), BookBean.class));
        }
        Map<Integer, Date> returnDatesBook = new HashMap<>();
        Map<Integer, Integer> placeInQueue = new HashMap<>();
        for(BookBean book : listReservedBooks) {
            List<LoanBean> listLoans = Arrays.asList(new RestTemplate().getForEntity("http://localhost:9003/tous_les_prets/" + book.getId(), LoanBean[].class).getBody());
            List<Date> dates = new ArrayList<>();
            for(LoanBean loan : listLoans) {
                dates.add(loan.getDeadline());
            }
            if(dates.size() != 0) {
                returnDatesBook.put(book.getId(), Collections.min(dates));
            }
        }
        model.addAttribute("reservations", listReservationsUser);
        model.addAttribute("reservedBooks", listReservedBooks);
        model.addAttribute("returnDatesBook", returnDatesBook);
        model.addAttribute("placeInQueue", placeInQueue);
        catchLoggedUserId(model);
        return "Reservations";
    }

    @GetMapping(value = "/annuler_reservation/{bookId}/{id}")
    public void deleteReservation(@PathVariable int id, @PathVariable int bookId, HttpServletResponse response) throws IOException, MessagingException {
        RestTemplate restTemplate = new RestTemplate();
        int priorityDeletedReservation = restTemplate.getForObject("http://localhost:9004/reservation/" + id, ReservationBean.class).getPriority();
        ReservationsProxy.deleteReservation(bookId, id);
        List<ReservationBean> reservationsOfBook = Arrays.asList(restTemplate.getForEntity("http://localhost:9004/reservations/" + bookId, ReservationBean[].class).getBody());
        for(ReservationBean reservationOfBook : reservationsOfBook) {
            if(reservationOfBook.getPriority() > priorityDeletedReservation) {
                ReservationBean updatedReservation = new ReservationBean();
                updatedReservation.setId(reservationOfBook.getId());
                updatedReservation.setBookId(reservationOfBook.getBookId());
                updatedReservation.setUserId(reservationOfBook.getUserId());
                updatedReservation.setPriority(reservationOfBook.getPriority() - 1);
                Map<String, String> paramsPut = new HashMap<>();
                paramsPut.put("id", Integer.toString(reservationOfBook.getId()));
                restTemplate.put("http://localhost:9004/priorite_baisseest/{id}", updatedReservation, paramsPut);
            }
        }
        response.sendRedirect("/livres?annuler_reservation=true");
    }

    @GetMapping(value = "/error")
    public String getErrorPage() {
        return "Error";
    }

    private void readPropertiesAndSend(String email, String title) throws IOException, MessagingException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("configuration.properties");
        Properties properties = new Properties();
        if(inputStream != null) {
            properties.load(inputStream);
        } else {
            throw new FileNotFoundException("Le fichier de propriétés n'existe pas");
        }
        Session session = Session.getDefaultInstance(properties);
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(properties.getProperty("mail.smtp.user")));
        message.setSubject(title + " vous attend à la bibliothèque");
        message.setContent("Bonjour,\n\nSuite à votre réservation du livre " + title + ", nous vous informons que ce dernier est disponible à la bibliothèque dans un délai de 48 heures à compter de l'envoi de ce message. Passé ce délai, votre réservation sera expirée.", "text/plain");
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(email));

        Transport transport = session.getTransport("smtp");
        transport.connect(properties.getProperty("mail.smtp.host"), properties.getProperty("mail.smtp.user"), properties.getProperty("mail.smtp.password"));
        transport.sendMessage(message, message.getAllRecipients());
        transport.close();
    }
}