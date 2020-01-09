package com.library.clientui.security;

import com.library.clientui.beans.UserBean;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class LibraryUserDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<UserBean> users = Arrays.asList(new RestTemplate().getForEntity("http://localhost:9002/utilisateurs/", UserBean[].class).getBody());
        Optional<UserBean> user = users.stream().filter(u -> u.getUsername().equals(username)).findAny();
        if (!user.isPresent()) {
            throw new UsernameNotFoundException("User not found by name: " + username);
        }

        return buildUserDetails(user.get());
    }

    public UserDetails buildUserDetails(UserBean user) {
        return User.withUsername(user.getUsername()).password("{bcrypt}" + user.getPassword()).roles("USER").build();
    }
}
