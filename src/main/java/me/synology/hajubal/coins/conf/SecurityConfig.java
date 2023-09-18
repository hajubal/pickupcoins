package me.synology.hajubal.coins.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Profile("local")
    @Order(0)
    @Bean
    public SecurityFilterChain h2SecurityFilterChain(HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests().requestMatchers(toH2Console()).permitAll().and().build();
    }

    @Order(1)
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .cors().disable()
                .headers().frameOptions().disable().and()
                .authorizeHttpRequests()
                //.requestMatchers(toH2Console()).permitAll()  //h2-console이 propertiesdp 설정이 안될 경우 오류 발생
                .requestMatchers("/actuator/**").permitAll()
                        .anyRequest().authenticated()
                .and().formLogin()
                ;

        return http.build();
    }
}
