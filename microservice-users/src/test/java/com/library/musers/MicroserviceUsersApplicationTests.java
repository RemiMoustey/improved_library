package com.library.musers;

import com.library.musers.model.User;
import com.library.musers.web.controller.UserController;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.Assert.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class MicroserviceUsersApplicationTests {

	@Autowired
	UserController userController;

	@Test
	void testInsertUser() {
		int previousSize = userController.getAllUsers().size();
		User newUser = new User();
		newUser.setUsername("Test");
		newUser.setPassword("test");
		newUser.setEmail("remimoustey@gmail.com");
		newUser.setAddress("Address");
		newUser.setPostalCode(75000);
		newUser.setTown("Paris");
		newUser.setNumber(12345678);
		newUser.setPhoneNumber("0102030405");
		userController.insertUser(newUser);
		assertEquals(previousSize + 1, userController.getAllUsers().size());
		userController.deleteUser(userController.getUserByUsername("Test").getId());
	}

	@Test
	void testDeleteUser() {
		User newUser = new User();
		newUser.setUsername("Test");
		newUser.setPassword("test");
		newUser.setEmail("remimoustey@gmail.com");
		newUser.setAddress("Adresse");
		newUser.setPostalCode(75000);
		newUser.setTown("Paris");
		newUser.setNumber(12345678);
		newUser.setPhoneNumber("0102030405");
		userController.insertUser(newUser);
		int previousSize = userController.getAllUsers().size();
		userController.deleteUser(userController.getUserByUsername("Test").getId());
		assertEquals(previousSize - 1, userController.getAllUsers().size());
	}

	@Test
	void testGetAllUsers() {
		assertEquals(6, userController.getAllUsers().size());
	}

	@Test
	void testGetUserByUsername() {
		assertEquals(19, (int) userController.getUserByUsername("azertyuiop").getId());
	}
}
