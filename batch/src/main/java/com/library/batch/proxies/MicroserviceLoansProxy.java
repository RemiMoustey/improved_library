package com.library.batch.proxies;

import com.library.batch.beans.LoanBean;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "microservice-loans", url = "localhost:9003")
public interface MicroserviceLoansProxy {

    @GetMapping(value = "/prets/{userId}")
    List<LoanBean> getLoans(@PathVariable int userId);
}
