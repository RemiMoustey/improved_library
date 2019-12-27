package com.library.batch.proxies;

import com.library.batch.beans.LoanBean;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;


@FeignClient(name = "microservice-loans", url = "localhost:9003")
public interface MicroserviceLoansProxy {


}
