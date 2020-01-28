package com.library.clientui.proxies;

import com.library.clientui.beans.ReservationBean;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(contextId = "reservationsClient", name = "zuul-server")
@RibbonClient(name = "microservice-reservations")
public interface MicroserviceReservationsProxy {
    @PostMapping(value = "/microservice-reservations/reserve_book")
    ResponseEntity<Void> insertReservation(@RequestBody ReservationBean reservation);

    @GetMapping(value = "/microservice-reservations/priorite_baisse/{bookId}")
    void updatePriorityReservations(@PathVariable int bookId);

    @GetMapping(value = "/microservice-reservations/reservations_user/{userId}")
    List<ReservationBean> getReservationsUser(@PathVariable int userId);

    @GetMapping(value = "/microservice-reservations/annuler_reservation/{bookId}/{id}")
    void deleteReservation(@PathVariable int bookId, @PathVariable int id);
}
