package com.library.mbooks;

import com.library.mbooks.model.Book;
import com.library.mbooks.web.controller.BookController;
import com.library.mbooks.web.exceptions.BookNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class MicroserviceBooksApplicationTests {

	@Autowired
	private BookController bookController;

	@Test
	void testGetListBooks() {
		assertEquals(5, bookController.getListBooks().size());
	}

	@Test
	void testGetBookFail() {
		try {
			bookController.getBook(17);
		} catch(BookNotFoundException e) {
			assert(e.getMessage().contains("Le livre correspondant n'existe pas."));
		}
	}

	@Test
	void testGetBookSuccess() {
		Book foundBook = bookController.getBook(4);
		assertEquals("Contes", foundBook.getTitle());
	}

	@Test
	void testGetListSearchedBooks() {
		assertEquals(2, bookController.getListSearchedBooks("Charles").size());
	}

	@Test
	void testGetListBooksOfLoansFail() {
		List<Integer> listIds = new ArrayList<>();
		listIds.add(15);
		listIds.add(16);
		listIds.add(17);
		try {
			bookController.getListBooksOfLoans(listIds);
		} catch(BookNotFoundException e) {
			assert(e.getMessage().contains("Aucun livre n'est disponible"));
		}
	}

	@Test
	void testGetListBooksOfLoansSuccess() {
		List<Integer> listIds = new ArrayList<>();
		listIds.add(1);
		listIds.add(2);
		listIds.add(3);
		assertEquals(3, bookController.getListBooksOfLoans(listIds).size());
	}

	@Test
	void testUpdateStockBookDecrementFail() {
		try {
			bookController.updateStockBookDecrement(22);
		} catch(BookNotFoundException e) {
			assert(e.getMessage().contains("Le livre correspondant n'existe pas."));
		}
	}

	@Test
	void testUpdateStockBookDecrementSuccess() {
		int numberCopiesBeforeUpdate = bookController.getBook(2).getCopies();
		bookController.updateStockBookDecrement(2);
		assertEquals(numberCopiesBeforeUpdate - 1, (int) bookController.getBook(2).getCopies());
	}

	@Test
	void testUpdateStockBookIncrementFail(){
		try {
			bookController.updateStockBookIncrement(18);
		} catch(BookNotFoundException e) {
			assert(e.getMessage().contains("Le livre correspondant n'existe pas."));
		}
	}

	@Test
	void testUpdateStockBookIncrementSuccess() {
		int numberCopiesBeforeUpdate = bookController.getBook(1).getCopies();
		bookController.updateStockBookIncrement(1);
		assertEquals(numberCopiesBeforeUpdate + 1, (int) bookController.getBook(1).getCopies());
	}

	@Test
	void testUpdateBookBatch() {
		Book book = bookController.getBook(5);
		int numberCopiesBeforeUpdate = book.getCopies();
		book.setCopies(book.getCopies() + 1);
		assertEquals(numberCopiesBeforeUpdate, (int) bookController.getBook(5).getCopies());
	}
}
