package com.library.mreservations.controller;

import com.library.mreservations.dao.ReservationDao;
import com.library.mreservations.model.Reservation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Calendar;
import java.util.List;

@RestController
public class ReservationController {
    Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ReservationDao reservationDao;

    @GetMapping(value = "/reservations")
    public List<Reservation> getAllReservations() {
        return reservationDao.findAll();
    }

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

    @GetMapping(value = "/reservations/{bookId}")
    public List<Reservation> getReservationsOfBook(@PathVariable int bookId) {
        return reservationDao.findAllByBookId(bookId);
    }

    @GetMapping(value = "/reservations_user/{userId}")
    public List<Reservation> getReservationsOfUser(@PathVariable int userId) {
        return reservationDao.findAllByUserId(userId);
    }

    @GetMapping(value = "/priorite_baisse/{bookId}")
    public void updatePriorityReservations(@PathVariable int bookId) {
        List<Reservation> listReservationsBook = reservationDao.findAllByBookId(bookId);
        for(Reservation reservationBook : listReservationsBook) {
            reservationBook.setPriority(reservationBook.getPriority() - 1);
            if(reservationBook.getPriority() == 0) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(Calendar.getInstance().getTime());
//                calendar.add(Calendar.DAY_OF_YEAR, 2);
//                calendar.add(Calendar.HOUR_OF_DAY, 1);
                calendar.add(Calendar.MINUTE, 1);
                reservationBook.setDeadline(calendar.getTime());
            }
            reservationDao.save(reservationBook);
        }
    }

    @RequestMapping(value="/priorite_baisse_batch/{bookId}", method = RequestMethod.PUT)
    public void updatePriorityReservationsInBatch(@PathVariable int bookId, @RequestBody Reservation reservation) {
        reservationDao.save(reservation);
    }

    @GetMapping(value = "/suppression_reservation/{reservationId}")
    public void deleteReservation(@PathVariable int reservationId) {
        reservationDao.deleteById(reservationId);
    }

    @RequestMapping(value="/suppression_reservation_batch/{reservationId}", method = RequestMethod.DELETE)
    public void deleteReservationInBatch(@PathVariable int reservationId) {
        reservationDao.deleteById(reservationId);
    }
}
