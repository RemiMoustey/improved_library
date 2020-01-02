package com.library.clientui.proxies;

import com.library.clientui.beans.LoanBean;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(contextId = "loansClient", name = "zuul-server")
@RibbonClient(name = "microservice-loans")
public interface MicroserviceLoansProxy {

    @GetMapping(value = "/microservice-loans/prets/{userId}")
    List<LoanBean> getLoans(@PathVariable int userId);

    @GetMapping(value = "/microservice-loans/nouveau_pret")
    ResponseEntity<Void> insertLoan(@RequestBody LoanBean loan);

    @GetMapping(value = "/microservice-loans/retour_pret/{id}/{bookId}")
    void deleteLoan(@PathVariable int id, @PathVariable int bookId);

    @PostMapping(value = "/microservice-loans/prolongation")
    ResponseEntity<Void> updateExtendedLoan(@RequestBody LoanBean updatedLoan);

    @GetMapping(value = "microservice-loans/tous_les_prets")
    List<LoanBean> getAllLoans();
}
