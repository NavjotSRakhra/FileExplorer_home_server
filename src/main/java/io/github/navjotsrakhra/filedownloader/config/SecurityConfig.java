package io.github.navjotsrakhra.filedownloader.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public InMemoryUserDetailsManager userDetailsManager() {
        return new InMemoryUserDetailsManager(
                User.withUsername("admin")
                        .password(passwordEncoder().encode("navjotrakhra"))
                        .roles("ADMIN")
                        .build(),
                User.withUsername("guest")
                        .password(passwordEncoder().encode("guest"))
                        .roles("GUEST")
                        .build()
        );
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(
                        AbstractHttpConfigurer::disable
                );
        return httpSecurity.build();
    }

//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf(httpSecurityCsrfConfigurer -> {
//                    try {
//                        httpSecurityCsrfConfigurer.disable();
//                    } catch (Exception e) {
//                        throw new RuntimeException(e);
//                    }
//                })
//                .authorizeHttpRequests(new Customizer<AuthorizeHttpRequestsConfigurer<org.springframework.security.config.annotation.web.builders.HttpSecurity>.AuthorizationManagerRequestMatcherRegistry>() {
//                    @Override
//                    public void customize(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry authorizationManagerRequestMatcherRegistry) {
//                        authorizationManagerRequestMatcherRegistry
//                                .requestMatchers("/login").permitAll()
//                                .requestMatchers("/files/setDownloadRoot").hasRole("ADMIN")
//                                .anyRequest().authenticated();
//                    }
//                });
//        return http.build();
//    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
