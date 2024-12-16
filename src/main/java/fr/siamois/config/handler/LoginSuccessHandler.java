package fr.siamois.config.handler;

import fr.siamois.bean.NavBean;
import fr.siamois.bean.SessionSettings;
import fr.siamois.models.auth.Person;
import fr.siamois.services.TeamService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final SessionSettings sessionSettings;
    private final NavBean navBean;
    private final TeamService teamService;

    public LoginSuccessHandler(SessionSettings sessionSettings, NavBean navBean, TeamService teamService) {
        this.sessionSettings = sessionSettings;
        this.navBean = navBean;
        this.teamService = teamService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        setupSession(authentication);
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

    private void setupSession(Authentication authentication) {
        sessionSettings.setAuthenticatedUser((Person) authentication.getPrincipal());
        if (sessionSettings.getAuthenticatedUser().hasRole("ADMIN")) {
            navBean.setTeams(teamService.findAllTeams());
        } else {
            navBean.setTeams(teamService.findTeamsOfPerson(sessionSettings.getAuthenticatedUser()));
        }
        sessionSettings.setSelectedTeam(navBean.getTeams().get(0));
    }
}
