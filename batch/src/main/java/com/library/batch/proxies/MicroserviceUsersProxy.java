package com.library.batch.proxies;

import com.library.batch.beans.UserBean;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "microservice-users", url = "localhost:9002")
public interface MicroserviceUsersProxy {

    @GetMapping(value = "/utilisateurs")
    List<UserBean> getAllUsers();
}
