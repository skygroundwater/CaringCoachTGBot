package com.caringcoachtelegrambot.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    /*@Bean
    public SecurityFilterChain configure(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .userDetailsService(null)
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> {

                    auth.requestMatchers(
                            "/dogs",
                            "/dogs" + "/{id}",
                            "/dogs" + "/put",
                            "/dogs" + "/photo" + "/{name}").hasAnyRole("VOLUNTEER");

                    auth.requestMatchers("/cats",
                            "/cats" + "/{id}",
                            "/cats" + "/put",
                            "/cats" + "/photo" + "/{name}").hasAnyRole("VOLUNTEER");

                })
                .httpBasic(Customizer.withDefaults())
                .build();

    }*/
}
