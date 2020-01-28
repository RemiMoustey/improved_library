package com.library.mloans.web.controller;

import com.library.mloans.dao.LoanDao;
import com.library.mloans.exceptions.LoanNotFoundException;
import com.library.mloans.exceptions.UnauthorizedProlongationException;
import com.library.mloans.model.Loan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@RestController
public class LoanController {

    Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private LoanDao loanDao;

    @PostMapping(value = "/nouveau_pret")
    public ResponseEntity<Void> insertLoan(@RequestBody Loan loan) {
        Loan addedLoan = loanDao.save(loan);

        if (addedLoan == null) {
            return ResponseEntity.noContent().build();
        }

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(addedLoan.getId())
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @GetMapping(value = "/retour_pret/{id}/{bookId}")
    public void deleteLoan(@PathVariable int id, @PathVariable int bookId) {
        loanDao.deleteById(id);
    }

    @GetMapping(value = "/prets/{userId}")
    public List<Loan> getLoans(@PathVariable int userId) {
        return loanDao.findAllByUserId(userId);
    }

    @PostMapping(value = "/prolongation")
    public ResponseEntity<Void> updateExtendedLoan(@RequestBody Loan extendedLoan) {
        Calendar nowCalendar = Calendar.getInstance();
        nowCalendar.setTime(Calendar.getInstance().getTime());
        if(extendedLoan.getDeadline().before(nowCalendar.getTime())) {
            throw new UnauthorizedProlongationException("Vous ne pouvez pas prolonger votre prêt car la date limite de retour a été dépassée");
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(extendedLoan.getDeadline());
        calendar.add(Calendar.DAY_OF_YEAR, 28);
        extendedLoan.setDeadline(calendar.getTime());
        extendedLoan.setExtended(true);
        Loan updatedLoan = loanDao.save(extendedLoan);

        if (updatedLoan == null) {
            return ResponseEntity.noContent().build();
        }

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(updatedLoan.getId())
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @GetMapping(value = "/tous_les_prets")
    public List<Loan> getAllLoans() {
        return loanDao.findAll();
    }

    @GetMapping(value = "/tous_les_prets/{bookId}")
    public List<Loan> getLoansOfBook(@PathVariable int bookId) {
        return loanDao.findAllByBookId(bookId);
    }
}
