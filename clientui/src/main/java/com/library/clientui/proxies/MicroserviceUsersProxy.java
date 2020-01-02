package com.library.clientui.proxies;

import com.library.clientui.beans.UserBean;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(contextId = "usersClient", name = "zuul-server")
@RibbonClient(name = "microservice-users")
public interface MicroserviceUsersProxy {
    @PostMapping(value = "/microservice-users/validation")
    ResponseEntity<Void> insertUser(@RequestBody UserBean user);

    @PostMapping(value = "/microservice-users/validation_connection")
    UserBean getUserByLoginAndPassword(@RequestParam String login, @RequestParam String password);

    @GetMapping(value = "/utilisateurs")
    List<UserBean> getAllUsers();

    @GetMapping(value = "/microservice-users/utilisateur/{username}")
    UserBean getUserByUsername(@PathVariable String username);
}