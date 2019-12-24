package com.library.mloans.web.controller;

import com.library.mloans.dao.LoanDao;
import com.library.mloans.model.Loan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.List;

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

    @GetMapping(value = "/prets/{userId}")
    public List<Loan> getLoans(@PathVariable int userId) {
        return loanDao.findAllByUserId(userId);
    }
}
