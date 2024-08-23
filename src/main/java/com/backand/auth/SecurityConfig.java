package com.backand.auth;

import com.backand.auth.model.ApplicationUser;
import com.backand.auth.repository.ApplicationUserRepository;
import com.backand.auth.rest.JwtTokenFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final ApplicationUserRepository applicationUserRepository;
    private final JwtTokenFilter jwtTokenFilter;

    // TO REFACTOR
    @EventListener(ApplicationReadyEvent.class)
    public void saveUser() {
        ApplicationUser user1 = new ApplicationUser("pepu@pepu.pl", getBcryptPasswordEncoder().encode("admin"), "ROLE_ADMIN");
        applicationUserRepository.save(user1);

        ApplicationUser user2 = new ApplicationUser("jan@kowalski.pl", getBcryptPasswordEncoder().encode("user"), "ROLE_USER");
        applicationUserRepository.save(user2);
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> applicationUserRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("User with email not found: " + username));
    }

    @Bean
    public PasswordEncoder getBcryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.cors().configurationSource(request -> new CorsConfiguration().applyPermitDefaultValues());
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.authorizeHttpRequests()
                .antMatchers("/auth/login").permitAll()
                .antMatchers("/hello").hasRole("ADMIN")
                .anyRequest().authenticated();

        http.addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}
