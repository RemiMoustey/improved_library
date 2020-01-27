package com.library.mreservations;

import com.library.mreservations.controller.ReservationController;
import com.library.mreservations.model.Reservation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.Assert.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class MicroserviceReservationsApplicationTests {

	@Autowired
	private ReservationController reservationController;

	@Test
	void testGetAllReservations() {
		assertEquals(0, reservationController.getAllReservations().size());
	}

	@Test
	void testInsertReservation() {
		int previousSize = reservationController.getAllReservations().size();
		Reservation reservation = new Reservation();
		reservation.setBookId(2);
		reservation.setPriority(0);
		reservation.setUserId(19);
		reservationController.insertReservation(reservation);
		assertEquals(previousSize + 1, reservationController.getAllReservations().size());
		reservationController.deleteReservation(reservationController.getReservationsOfBook(2).get(0).getId());
	}

	@Test
	void testDeleteLoan() {
		Reservation reservation = new Reservation();
		reservation.setBookId(2);
		reservation.setPriority(0);
		reservation.setUserId(19);
		reservationController.insertReservation(reservation);
		int previousSize = reservationController.getAllReservations().size();
		reservationController.deleteReservation(reservationController.getReservationsOfBook(2).get(0).getId());
		assertEquals(previousSize - 1, reservationController.getAllReservations().size());
	}

	@Test
	void testGetReservationsOfBook() {
		Reservation firstReservation = new Reservation();
		firstReservation.setBookId(2);
		firstReservation.setPriority(0);
		firstReservation.setUserId(19);
		reservationController.insertReservation(firstReservation);
		Reservation secondReservation = new Reservation();
		secondReservation.setBookId(2);
		secondReservation.setPriority(0);
		secondReservation.setUserId(21);
		reservationController.insertReservation(secondReservation);
		List<Reservation> list = reservationController.getReservationsOfBook(2);
		assertEquals(2, reservationController.getReservationsOfBook(2).size());
		reservationController.deleteReservation(reservationController.getAllReservations().get(0).getId());
		reservationController.deleteReservation(reservationController.getAllReservations().get(0).getId());
	}

	@Test
	void testGetReservationsOfUser() {
		Reservation firstReservation = new Reservation();
		firstReservation.setBookId(2);
		firstReservation.setPriority(0);
		firstReservation.setUserId(19);
		reservationController.insertReservation(firstReservation);
		Reservation secondReservation = new Reservation();
		secondReservation.setBookId(5);
		secondReservation.setPriority(0);
		secondReservation.setUserId(19);
		reservationController.insertReservation(secondReservation);
		Reservation thirdReservation = new Reservation();
		thirdReservation.setBookId(4);
		thirdReservation.setPriority(0);
		thirdReservation.setUserId(19);
		reservationController.insertReservation(thirdReservation);
		assertEquals(3, reservationController.getReservationsOfUser(19).size());
		reservationController.deleteReservation(reservationController.getAllReservations().get(0).getId());
		reservationController.deleteReservation(reservationController.getAllReservations().get(0).getId());
		reservationController.deleteReservation(reservationController.getAllReservations().get(0).getId());
	}

	@Test
	void testUpdatePriorityReservations() {
		Reservation reservation = new Reservation();
		reservation.setUserId(19);
		reservation.setBookId(2);
		reservation.setPriority(1);
		int previousPriority = reservation.getPriority();
		reservationController.insertReservation(reservation);
		reservationController.updatePriorityReservations(2);
		assertEquals(previousPriority - 1, (int) reservationController.getReservationsOfBook(2).get(0).getPriority());
		reservationController.deleteReservation(reservationController.getAllReservations().get(0).getId());
	}

	@Test
	void testGetReservationBatch() {
		Reservation reservation = new Reservation();
		reservation.setBookId(2);
		reservation.setPriority(3);
		reservation.setUserId(19);
		reservationController.insertReservation(reservation);
		int previousPriority = reservationController.getAllReservations().get(0).getPriority();
		reservation.setPriority(2);
		reservationController.updateReservationBatch(2, reservation);
		assertEquals(2, (int) reservationController.getAllReservations().get(0).getPriority());
		reservationController.deleteReservation(reservationController.getAllReservations().get(0).getId());
	}

	@Test
	void testDeleteReservationBatch() {
		Reservation reservation = new Reservation();
		reservation.setBookId(2);
		reservation.setPriority(3);
		reservation.setUserId(19);
		reservationController.insertReservation(reservation);
		int previousSize = reservationController.getAllReservations().size();
		reservationController.deleteReservation(reservationController.getAllReservations().get(0).getId());
		assertEquals(previousSize - 1, reservationController.getAllReservations().size());
	}
}
