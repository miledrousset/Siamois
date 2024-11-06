package fr.siamois.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.HeaderWriterLogoutHandler;
import org.springframework.security.web.header.writers.ClearSiteDataHeaderWriter;

@Slf4j
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        log.trace("Security chain ");
        http.authorizeHttpRequests((requests) -> requests.requestMatchers("/hello", "/hello.xhtml").authenticated()
                .requestMatchers("/", "/index.xhtml").authenticated()
                .anyRequest().permitAll()
        );
        http.formLogin((login) -> login
                .loginPage("/login").permitAll()
        );
        http.logout((logout) -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login")
        );
        http.sessionManagement((session) -> session.maximumSessions(1)
        );

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
