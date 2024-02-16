package me.synology.hajubal.coins.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;
import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * FIXME: 설정할 경우 로그인 페이지도 접근 안되는 문제가 있음
     * @param http
     * @return
     * @throws Exception
     */
    @Profile("local")
    @Order(0)
//    @Bean
    public SecurityFilterChain h2SecurityFilterChain(HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests().requestMatchers(toH2Console()).permitAll().and().build();
    }

    @Order(1)
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((authorizeHttpRequests) ->
                        authorizeHttpRequests
                                .requestMatchers("/actuator/**").permitAll()
                                .anyRequest().authenticated()
                )
                .httpBasic(withDefaults())
                .formLogin(withDefaults())
        ;

        return http.build();
    }
}
