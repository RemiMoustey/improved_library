package com.library.mloans;

import com.library.mloans.exceptions.UnauthorizedProlongationException;
import com.library.mloans.model.Loan;
import com.library.mloans.web.controller.LoanController;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class MicroserviceLoansApplicationTests {

	@Autowired
	private LoanController loanController;

	@Test
	void testInsertLoan() {
		int previousSize = loanController.getAllLoans().size();
		Loan loan = new Loan();
		loan.setBookId(2);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(Calendar.getInstance().getTime());
		calendar.add(Calendar.DAY_OF_YEAR, 28);
		loan.setDeadline(calendar.getTime());
		loan.setUserId(19);
		loan.setExtended(false);
		loanController.insertLoan(loan);
		assertEquals(previousSize + 1, loanController.getAllLoans().size());
	}

	@Test
	void testDeleteLoan() {
		Loan loan = new Loan();
		loan.setBookId(3);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(Calendar.getInstance().getTime());
		calendar.add(Calendar.DAY_OF_YEAR, 28);
		loan.setDeadline(calendar.getTime());
		loan.setUserId(19);
		loan.setExtended(false);
		loanController.insertLoan(loan);
		int previousSize = loanController.getAllLoans().size();
		Loan currentLoan = loanController.getLoansOfBook(3).get(0);
		loanController.deleteLoan(currentLoan.getId(), 3);
		assertEquals(previousSize - 1, loanController.getAllLoans().size());
	}

	@Test
	void testGetLoans() {
		assertEquals(0, loanController.getLoans(19).size());
	}

	@Test
	void testUpdateExtendedLoanFail() {
		Loan loan = new Loan();
		loan.setBookId(2);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(Calendar.getInstance().getTime());
		loan.setDeadline(calendar.getTime());
		loan.setUserId(19);
		loan.setExtended(false);
		loanController.insertLoan(loan);
		loan.setDeadline(calendar.getTime());
		try {
			loanController.updateExtendedLoan(loan);
		} catch(UnauthorizedProlongationException e) {
			assert(e.getMessage().contains("Vous ne pouvez pas prolonger votre prêt car la date limite de retour a été dépassée"));
		}
		loanController.deleteLoan(loanController.getLoansOfBook(2).get(0).getId(), 2);
	}

	@Test
	void testUpdateExtendedLoanSuccess() {
		Loan loan = new Loan();
		loan.setBookId(2);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(Calendar.getInstance().getTime());
		calendar.add(Calendar.DAY_OF_YEAR, 28);
		loan.setDeadline(calendar.getTime());
		loan.setUserId(19);
		loan.setExtended(false);
		loanController.insertLoan(loan);
		Date previousDeadline = loanController.getLoansOfBook(2).get(0).getDeadline();
		calendar.add(calendar.DAY_OF_YEAR, 28);
		loan.setDeadline(calendar.getTime());
		loanController.updateExtendedLoan(loan);
		assertTrue(previousDeadline.before(loanController.getLoansOfBook(2).get(0).getDeadline()));
		loanController.deleteLoan(loanController.getLoansOfBook(2).get(0).getId(), 2);
	}

	@Test
	void testGetAllLoans() {
		assertEquals(0, loanController.getAllLoans().size());
	}

	@Test
	void testGetAllLoansOfBook() {
		assertEquals(0, loanController.getLoansOfBook(4).size());
	}
}
