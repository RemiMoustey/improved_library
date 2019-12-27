package com.library.clientui.proxies;

import com.library.clientui.beans.LoanBean;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "microservice-loans", url = "localhost:9003")
public interface MicroserviceLoansProxy {

    @GetMapping(value = "/prets/{userId}")
    List<LoanBean> getLoans(@PathVariable int userId);

    @GetMapping(value = "/nouveau_pret")
    ResponseEntity<Void> insertLoan(@RequestBody LoanBean loan);

    @GetMapping(value = "retour_pret/{id}/{bookId}")
    void deleteLoan(@PathVariable int id, @PathVariable int bookId);

    @PostMapping(value = "/prolongation")
    ResponseEntity<Void> updateExtendedLoan(@RequestBody LoanBean updatedLoan);
}
