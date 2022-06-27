package com.atypon.nosql.security;

import com.atypon.nosql.users.DatabaseUsersDetailService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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

    private final String masterNodeUrl;

    public SecurityConfiguration(
            DatabaseUsersDetailService databaseUsersDetailService,
            PasswordEncoder passwordEncoder,
            CustomBasicAuthenticationEntryPoint entryPoint,
            String masterNodeUrl) {
        this.databaseUsersDetailService = databaseUsersDetailService;
        this.passwordEncoder = passwordEncoder;
        this.entryPoint = entryPoint;
        this.masterNodeUrl = masterNodeUrl;
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
        http.csrf().disable().authorizeRequests()
                .antMatchers(HttpMethod.GET, "/databases")
                .hasAuthority(DatabaseAuthority.GET_DATABASES)
                .antMatchers(HttpMethod.POST, "/databases/*")
                .hasAuthority(DatabaseAuthority.CREATE_DATABASE)
                .antMatchers(HttpMethod.DELETE, "/databases/*")
                .hasAuthority(DatabaseAuthority.REMOVE_COLLECTION)
                .antMatchers(HttpMethod.GET, "/databases/*/collections")
                .hasAuthority(DatabaseAuthority.GET_COLLECTIONS)
                .antMatchers(HttpMethod.POST, "/databases/*/collections/*")
                .hasAuthority(DatabaseAuthority.CREATE_COLLECTION)
                .antMatchers(HttpMethod.DELETE, "/databases/*/collections/*")
                .hasAuthority(DatabaseAuthority.REMOVE_COLLECTION)
                .antMatchers(HttpMethod.GET, "/databases/*/collections/*/indexes")
                .hasAuthority(DatabaseAuthority.GET_INDEXES)
                .antMatchers(HttpMethod.POST, "/databases/*/collections/*/indexes")
                .hasAuthority(DatabaseAuthority.CREATE_INDEX)
                .antMatchers(HttpMethod.DELETE, "/databases/*/collections/*/indexes")
                .hasAuthority(DatabaseAuthority.REMOVE_INDEX)
                .antMatchers(HttpMethod.GET, "/databases/*/collections/*/documents")
                .hasAuthority(DatabaseAuthority.READ_DOCUMENTS)
                .antMatchers(HttpMethod.POST, "/databases/*/collections/*/documents")
                .hasAuthority(DatabaseAuthority.ADD_DOCUMENTS)
                .antMatchers(HttpMethod.PUT, "/databases/*/collections/*/documents")
                .hasAuthority(DatabaseAuthority.UPDATE_DOCUMENTS)
                .antMatchers(HttpMethod.DELETE, "/databases/*/collections/*/documents")
                .hasAuthority(DatabaseAuthority.REMOVE_DOCUMENTS)
                .antMatchers("/sync")
                .hasIpAddress(masterNodeUrl)
                .and().httpBasic().authenticationEntryPoint(entryPoint)
                .and().authenticationProvider(authenticationProvider())
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        return http.build();
    }
}
