package com.library.musers.dao;

import com.library.musers.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserDao extends JpaRepository<User, Integer> {
    List<User> findAll();
    User findUserByUsername(String username);
    User findUserById(int id);
}
