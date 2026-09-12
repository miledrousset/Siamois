package fr.siamois.ui.config;

import com.sun.faces.config.ConfigureListener;
import jakarta.faces.annotation.FacesConfig;
import jakarta.faces.webapp.FacesServlet;
import jakarta.servlet.ServletContext;
import org.jboss.weld.environment.servlet.Listener;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.context.ServletContextAware;

@FacesConfig
@Configuration
public class JsfConfig implements ServletContextAware {

    /**
     * <p>Les paramètres de contexte doivent porter le préfixe {@code jakarta.faces.} : sous Faces 4,
     * Mojarra ne lit plus les noms {@code javax.faces.} (cf. {@code ProjectStage.PROJECT_STAGE_PARAM_NAME}).</p>
     * <p>{@code PROJECT_STAGE} et {@code FACELETS_REFRESH_PERIOD} ne sont pas repris ici : JoinFaces règle
     * le stage via {@code joinfaces.faces.project-stage}, et Mojarra désactive de lui-même la relecture
     * des Facelets en stage {@code production}.</p>
     */
    @Override
    public void setServletContext(ServletContext servletContext) {
        servletContext.setInitParameter("com.sun.faces.forceLoadConfiguration", Boolean.TRUE.toString());

        // Exclut les commentaires XHTML du HTML rendu, donc aussi de chaque réponse ajax.
        servletContext.setInitParameter("jakarta.faces.FACELETS_SKIP_COMMENTS", Boolean.TRUE.toString());

        // Aucun composant n'utilise validateClient : activer la validation côté client ne ferait que
        // charger validation.bv.js et annoter chaque champ sans bénéfice.
        servletContext.setInitParameter("primefaces.CLIENT_SIDE_VALIDATION", Boolean.FALSE.toString());
        servletContext.setInitParameter("primefaces.THEME", "siamois-theme");
    }

    @Bean
    public ServletRegistrationBean<jakarta.faces.webapp.FacesServlet> facesServletRegistration() {
        ServletRegistrationBean<jakarta.faces.webapp.FacesServlet> registrationBean = new ServletRegistrationBean<>(new FacesServlet(), "*.xhtml");
        registrationBean.setLoadOnStartup(1);
        return registrationBean;
    }

    /**
     * Weld doit initialiser le BeanManager avant {@link ConfigureListener} (Mojarra exige CDI en Jakarta Faces 4).
     */
    @Bean
    public ServletListenerRegistrationBean<Listener> weldServletListener() {
        ServletListenerRegistrationBean<Listener> bean = new ServletListenerRegistrationBean<>(new Listener());
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return bean;
    }

    @Bean
    public ServletListenerRegistrationBean<ConfigureListener> jsfConfigureListener() {
        ServletListenerRegistrationBean<ConfigureListener> bean = new ServletListenerRegistrationBean<>(new ConfigureListener());
        bean.setOrder(Ordered.LOWEST_PRECEDENCE);
        return bean;
    }
}
