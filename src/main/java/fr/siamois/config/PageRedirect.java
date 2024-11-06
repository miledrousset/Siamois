package fr.siamois.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class PageRedirect implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("forward:/index.xhtml");
        registry.addViewController("/hello").setViewName("forward:/hello.xhtml");
        registry.addViewController("/login").setViewName("forward:/login.xhtml");

        registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
    }

}
