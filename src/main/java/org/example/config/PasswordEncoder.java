package org.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class PasswordEncoder {

    @Bean
    public BCryptPasswordEncoder passwordEncode() {
        // This method creates and returns a BCryptPasswordEncoder instance.
        // It is used for encoding passwords securely.
        return new BCryptPasswordEncoder();
    }
}
