package com.atypon.nosql.security;

import com.atypon.nosql.users.DatabaseUsersDetailService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    private final DatabaseUsersDetailService databaseUsersDetailService;

    private final PasswordEncoder passwordEncoder;

    private final CustomBasicAuthenticationEntryPoint entryPoint;

    public SecurityConfiguration(
            DatabaseUsersDetailService databaseUsersDetailService,
            PasswordEncoder passwordEncoder,
            CustomBasicAuthenticationEntryPoint entryPoint) {
        this.databaseUsersDetailService = databaseUsersDetailService;
        this.passwordEncoder = passwordEncoder;
        this.entryPoint = entryPoint;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);
        daoAuthenticationProvider.setUserDetailsService(databaseUsersDetailService);
        return daoAuthenticationProvider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable().authorizeHttpRequests()
                .anyRequest().permitAll()
                .antMatchers("/users").hasRole("ADMIN")
                .antMatchers("/databases").hasRole("ADMIN")
                .antMatchers("/databases/*").hasRole("ADMIN")
                .antMatchers("/databases/*/collections").hasRole("ADMIN")
                .antMatchers("/databases/*/collections/*").hasRole("ADMIN")
                .antMatchers("/databases/*/collections/*/indexes").hasRole("ADMIN")
                .antMatchers("/databases/*/collections/*/documents").hasAnyRole("ADMIN", "USER")
                .and().httpBasic().authenticationEntryPoint(entryPoint)
                .and().authenticationProvider(authenticationProvider())
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        return http.build();
    }
}
