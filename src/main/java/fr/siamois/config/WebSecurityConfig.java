package fr.siamois.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuration class for the security of the application.
 * @author Julien Linget
 */
@Slf4j
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    /**
     * Security filter chain of the application.
     * @param http HttpSecurity object to configure the security chain.
     * @return The security filter chain with the configuration.
     * @throws Exception If any filter configuration fails.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        log.trace("Security chain ");
        http.authorizeHttpRequests((requests) -> requests
                .requestMatchers("/", "/index.xhtml").authenticated()
                .requestMatchers("/fieldConfiguration", "/pages/field/fieldConfiguration.xhtml").authenticated()
                .requestMatchers("/pages/create/spatialUnit.xhtml").authenticated()
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

    /**
     * Password encoder bean.
     * @return BCryptPasswordEncoder object to encode passwords with bcrypt.
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
