package com.library.musers.web.controller;

import com.library.musers.dao.*;
import com.library.musers.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;

@RestController
public class UserController {

    Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    UserDao userDao;

    @PostMapping(value = "/validation")
    public ResponseEntity<Void> insertUser(@RequestBody User user) {
        User addedUser = userDao.save(user);

        if (addedUser == null) {
            return ResponseEntity.noContent().build();
        }

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(addedUser.getId())
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @RequestMapping(value = "/login")
    public String getLoginPage() {
        return "Login";
    }

    @PostMapping(value = "/validation_connection")
    public User getUserByLogin(@RequestParam String login, @RequestParam String password, HttpServletRequest request) {
        return userDao.findByLoginAndPassword(login, password);
    }
}