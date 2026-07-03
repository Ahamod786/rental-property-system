package com.rental.property_system.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF for simplicity in this project phase
            .csrf(csrf -> csrf.disable()) 
            .authorizeHttpRequests(auth -> auth
                // These URLs are public to everyone
                .requestMatchers("/", "/property/**", "/register", "/css/**", "/js/**").permitAll()
                // Only the OWNER can see the dashboard
                .requestMatchers("/dashboard/**").hasAuthority("OWNER")
                // Everything else (like booking a property) requires being logged in
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login") // We will create this custom login page
                .defaultSuccessUrl("/") // Where to go after successful login
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/") // Where to go after logging out
                .permitAll()
            );

        return http.build();
    }

    // This encrypts passwords so they aren't stored as plain text in the database
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}