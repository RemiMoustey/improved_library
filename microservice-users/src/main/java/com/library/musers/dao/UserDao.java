package com.library.musers.dao;

import com.library.musers.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDao extends JpaRepository<User, Integer> {
    User findByLoginAndPassword(String login, String password);
}
