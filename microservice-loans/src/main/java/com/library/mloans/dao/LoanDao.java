package com.library.mloans.dao;

import com.library.mloans.model.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanDao extends JpaRepository<Loan, Integer> {
    List<Loan> findAllByUserId(int userId);
}
