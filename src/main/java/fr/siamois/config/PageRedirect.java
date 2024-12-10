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
        registry.addViewController("/login").setViewName("forward:/login.xhtml");
        registry.addViewController("/spatialunit").setViewName("forward:/pages/spatialUnit/spatialUnit.xhtml");
        registry.addViewController("/fieldConfiguration").setViewName("forward:/pages/field/fieldConfiguration.xhtml");
        registry.addViewController("/create/manager").setViewName("forward:/pages/create/manager.xhtml");
        registry.addViewController("/create/team").setViewName("forward:/pages/create/team.xhtml");
        registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
    }

}
