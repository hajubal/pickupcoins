package me.synology.hajubal.coins.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Order(1)
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((authorizeHttpRequests) ->
                        authorizeHttpRequests
                                .requestMatchers("/actuator/**").permitAll()
                                .requestMatchers("/css/**", "/assets/**").permitAll()
                                .anyRequest().authenticated()
                )
//                .httpBasic(withDefaults())
                .formLogin(loginConfigurer -> {
                    loginConfigurer.loginPage("/login")
                            .permitAll();
                })
        ;

        return http.build();
    }
}
