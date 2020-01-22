package com.library.mreservations.controller;

import com.library.mreservations.dao.ReservationDao;
import com.library.mreservations.model.Reservation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
public class ReservationController {
    Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ReservationDao reservationDao;

    @PostMapping(value = "/reserve_book")
    public ResponseEntity<Void> insertReservation(@RequestBody Reservation reservation) {
        Reservation addedReservation = reservationDao.save(reservation);

        if (addedReservation == null) {
            return ResponseEntity.noContent().build();
        }

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(addedReservation.getId())
                .toUri();

        return ResponseEntity.created(location).build();
    }
}
