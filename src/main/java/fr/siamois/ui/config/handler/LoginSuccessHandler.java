package fr.siamois.ui.config.handler;

import fr.siamois.ui.bean.SessionSettingsBean;
import fr.siamois.ui.bean.panel.FlowBean;
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

    private final SessionSettingsBean sessionSettingsBean;
    private final FlowBean flowBean;

    public LoginSuccessHandler(SessionSettingsBean sessionSettingsBean, FlowBean flowBean) {
        this.sessionSettingsBean = sessionSettingsBean;
        this.flowBean = flowBean;
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
        sessionSettingsBean.setupSession();
        // We add the "welcome panel" if the flowBean is empty
        if(flowBean.getPanels().isEmpty()) {
            flowBean.addSpatialUnitListPanel();
        }
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
}
