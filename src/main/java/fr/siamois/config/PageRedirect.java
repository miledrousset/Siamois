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
        registry.addViewController("/dashboard").setViewName("forward:/pages/dashboard/dashboard.xhtml");
        registry.addViewController("/login").setViewName("forward:/pages/login/login.xhtml");
        registry.addViewController("/spatialunit").setViewName("forward:/pages/spatialUnit/spatialUnit.xhtml");
        registry.addViewController("/fieldConfiguration").setViewName("forward:/pages/field/fieldConfiguration.xhtml");
        registry.addViewController("/admin/manager").setViewName("forward:/pages/admin/manager.xhtml");
        registry.addViewController("/admin/institution").setViewName("forward:/pages/admin/institution.xhtml");
        registry.addViewController("/manager/users").setViewName("forward:pages/manager/users.xhtml");
        registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
    }

}
