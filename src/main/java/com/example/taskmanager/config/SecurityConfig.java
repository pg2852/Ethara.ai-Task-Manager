package com.example.taskmanager.config;

import com.example.taskmanager.service.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;

    public SecurityConfig(UserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
        .authorizeHttpRequests(auth -> auth

        	    // Public pages
        	    .requestMatchers("/register", "/login", "/css/**")
        	    .permitAll()

        	    // Admin only
        	    .requestMatchers("/admin/**", "/users/**")
        	    .hasRole("ADMIN")

        	    // Admin + Manager can create/edit/delete tasks
        	    .requestMatchers(
        	        "/tasks/create",
        	        "/tasks/edit/**",
        	        "/tasks/delete/**"
        	    )
        	    .hasAnyRole("ADMIN","MANAGER")

        	    // Admin + Manager can manage teams
        	    .requestMatchers(
        	        "/teams/create",
        	        "/teams/edit/**",
        	        "/teams/delete/**"
        	    )
        	    .hasAnyRole("ADMIN","MANAGER")

        	    // Everyone logged in can view
        	    .requestMatchers(
        	        "/dashboard",
        	        "/tasks",
        	        "/teams"
        	    )
        	    .hasAnyRole("ADMIN","MANAGER","MEMBER")

        	    .anyRequest().authenticated()
        	)
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/dashboard", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            )
            .authenticationProvider(authenticationProvider());

        return http.build();
    }
}