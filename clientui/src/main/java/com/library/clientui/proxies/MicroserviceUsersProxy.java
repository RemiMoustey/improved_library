package com.library.clientui.proxies;

import com.library.clientui.beans.UserBean;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "microservice-users", url = "localhost:9002")
public interface MicroserviceUsersProxy {
    @PostMapping(value = "/validation")
    ResponseEntity<Void> insertUser(@RequestBody UserBean user);
}
