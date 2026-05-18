package com.capg.smartcourier.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


/*
 * configuration is used to define the bean classes , It process all the methods with the @Bean
 *  and if we want to use this beans we can simply inject this beans using @autowire
 */
@Configuration 
public class PasswordConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}