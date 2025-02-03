package fr.siamois.config.handler;

import fr.siamois.bean.NavBean;
import fr.siamois.bean.SessionSettings;
import fr.siamois.models.auth.Person;
import fr.siamois.services.InstitutionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Handler to manage the redirection after a successful login and setup the session.
 *
 * @author Julien Linget
 */
@Slf4j
@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final SessionSettings sessionSettings;
    private final NavBean navBean;
    private final InstitutionService institutionService;

    public LoginSuccessHandler(SessionSettings sessionSettings, NavBean navBean, InstitutionService institutionService) {
        this.sessionSettings = sessionSettings;
        this.navBean = navBean;
        this.institutionService = institutionService;
    }

    /**
     * Redirects the user to the saved request URL if it exists, otherwise to the home page.
     * @param request The HTTP request
     * @param response The HTTP response
     * @param authentication The authentication object
     * @throws IOException If the redirection fails
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        setupSession();
        redirectRequest(request, response);
    }

    private static void redirectRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Retrieve the saved request
        HttpSessionRequestCache requestCache = new HttpSessionRequestCache();
        SavedRequest savedRequest = requestCache.getRequest(request, response);

        // Get the context path
        String contextPath = request.getContextPath();

        // Redirect to the saved request URL if it exists, otherwise redirect to the login page
        if (savedRequest != null) {
            String targetUrl = savedRequest.getRedirectUrl();
            response.sendRedirect(targetUrl);
        } else {
            response.sendRedirect(contextPath + "/");
        }
    }

    private void setupSession() {
        if (sessionSettings.getAuthenticatedUser().hasRole("ADMIN")) {
            navBean.setInstitutions(institutionService.findAll());
        } else {
            Person authUser = sessionSettings.getAuthenticatedUser();
            navBean.setInstitutions(institutionService.findInstitutionsOfPerson(authUser));
        }

        navBean.getInstitutions()
                .sort(((o1, o2) -> o1.getName().compareToIgnoreCase(o2.getName())));

        if (!navBean.getInstitutions().isEmpty()) {
            navBean.setSelectedInstitution(navBean.getInstitutions().get(0));
        }

        log.info("User {} logged in", sessionSettings.getAuthenticatedUser().getUsername());
    }
}
