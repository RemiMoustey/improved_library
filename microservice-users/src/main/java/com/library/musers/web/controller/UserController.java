package com.library.musers.web.controller;

import com.library.musers.dao.UserDao;
import com.library.musers.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.ws.rs.Path;
import java.net.URI;
import java.util.List;

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

    @GetMapping(value = "/supprimer_utilisateur/{id}")
    public void deleteUser(@PathVariable int id) {
        userDao.deleteById(id);
    }

    @GetMapping(value = "/login")
    public String getLoginPage() {
        return "Login";
    }

    @GetMapping(value = "/utilisateurs")
    public List<User> getAllUsers() {
        return userDao.findAll();
    }

    @GetMapping(value = "/utilisateur/{username}")
    public User getUserByUsername(@PathVariable String username) {
        return userDao.findUserByUsername(username);
    }

    @GetMapping(value = "/utilisateur_id/{userId}")
    public User getUserById(@PathVariable int userId) {
        return userDao.findUserById(userId);
    }
}
