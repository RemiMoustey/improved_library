package com.library.mreservations.dao;

import com.library.mreservations.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationDao extends JpaRepository<Reservation, Integer> {
    List<Reservation> findAllByBookId(int bookId);
    List<Reservation> findAllByUserId(int userId);
    void deleteById(int id);
}
