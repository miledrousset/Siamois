package fr.siamois.domain.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class PageRedirectConfig implements WebMvcConfigurer {

    /**
     * Specifies the page redirection without parameters. The page redirection with parameters are placed inside
     * controllers in {@link fr.siamois.ui.redirection}
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("forward:/index.xhtml");
        registry.addViewController("/dashboard").setViewName("forward:/pages/dashboard/dashboard.xhtml");
        registry.addViewController("/login").setViewName("forward:/pages/login/login.xhtml");
        registry.addViewController("/spatialUnit").setViewName("forward:/pages/spatialUnit/list.xhtml");
        registry.addViewController("/fieldConfiguration").setViewName("forward:/pages/field/fieldConfiguration.xhtml");
        registry.addViewController("/admin/manager").setViewName("forward:/pages/admin/manager.xhtml");
        registry.addViewController("/admin/institution").setViewName("forward:/pages/admin/institution.xhtml");
        registry.addViewController("/manager/users").setViewName("forward:/pages/manager/users.xhtml");
        registry.addViewController("/manager/ark").setViewName("forward:/pages/ark/ark.xhtml");
        registry.addViewController("/error/404").setViewName("forward:/pages/error/error-404.xhtml");
        registry.addViewController("/error/403").setViewName("forward:/pages/error/error-403.xhtml");
        registry.addViewController("/error/500").setViewName("forward:/pages/error/error-500.xhtml");
        registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
    }

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        AntPathMatcher matcher = new AntPathMatcher();
        matcher.setCaseSensitive(false);
        configurer.setPathMatcher(matcher);
    }

}
