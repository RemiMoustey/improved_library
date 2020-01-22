package com.library.clientui.proxies;

import com.library.clientui.beans.ReservationBean;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "reservationsClient", name = "zuul-server")
@RibbonClient(name = "microservice-reservations")
public interface MicroserviceReservationsProxy {
    @PostMapping(value = "/microservice-reservations/reserve_book")
    ResponseEntity<Void> insertReservation(@RequestBody ReservationBean reservation);
}
