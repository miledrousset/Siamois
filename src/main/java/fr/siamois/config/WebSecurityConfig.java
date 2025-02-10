package fr.siamois.config;

import fr.siamois.bean.LangBean;
import fr.siamois.config.handler.LoginSuccessHandler;
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
    public SecurityFilterChain securityFilterChain(HttpSecurity http, LangBean langBean, LoginSuccessHandler loginSuccessHandler) throws Exception {
        http.authorizeHttpRequests(requests -> requests
                .requestMatchers("/", "/index.xhtml").permitAll()
                .requestMatchers("/fieldConfiguration", "/pages/field/fieldConfiguration.xhtml").authenticated()
                .requestMatchers("/pages/**").authenticated()
                .requestMatchers("/pages/admin/**", "/admin/**").hasAuthority("ADMIN")
                .requestMatchers("/pages/manager/**", "/manager/**").hasAnyAuthority("TEAM_MANAGER", "ADMIN")
                .anyRequest().permitAll()
        );
        http.formLogin(login -> login
                .loginPage("/login?lang=" + langBean.getLanguageCode()).permitAll()
                .loginProcessingUrl("/login")
                .failureUrl("/login?error=true&lang=" + langBean.getLanguageCode())
                .successHandler(loginSuccessHandler)
        );
        http.logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?lang=" + langBean.getLanguageCode())
        );
        http.sessionManagement(session -> session.maximumSessions(1));

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
