package com.library.clientui.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/livres", "livres/{id}", "/inscription", "/login", "/resultats_recherche").permitAll()
                .antMatchers("/stock_baisse/{bookId}", "/retour_pret/{id}/{bookId}", "/stock_monte/{bookId}", "/prets/{userId}", "/liste_prets/{bookIds}", "/aucun_pret").authenticated()
                .and()
                .formLogin()
                .defaultSuccessUrl("/livres", true)
                .loginPage("/login")
                .permitAll();
    }

    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers("/css/style.css", "/img/**", "/fonts/**");
        web.ignoring().antMatchers(HttpMethod.POST, "/validation", "/livres/resultats", "/nouveau_pret", "/prolongation", "/reserve_book");
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(new LibraryUserDetailsService());
    }
}
