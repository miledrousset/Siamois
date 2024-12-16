package fr.siamois.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.ForbiddenException;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ForbiddenException.class)
    public ModelAndView handleForbiddenException(HttpServletRequest request, Exception ex, Model model) {
        if(getHttpStatus(request).equals(HttpStatus.FORBIDDEN)) {
            model.addAttribute("errorMessage", ex.getMessage());
            return new ModelAndView("redirect:/errorPages/error403.xhtml");
        }
        return new ModelAndView("redirect:/errorPages/error500.xhtml");
    }

    // Gestion des erreurs 404
    @ExceptionHandler(NoHandlerFoundException.class)
    public ModelAndView handleNotFoundException(Exception ex, Model model) {
        model.addAttribute("errorMessage", ex.getMessage());
        return new ModelAndView("redirect:/errorPages/error404.xhtml");
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ModelAndView handleNoRessourceFoundException(NoResourceFoundException ex, Model model) {
        return handleNotFoundException(ex, model);
    }

    // Gestion des erreurs 500
    @ExceptionHandler(Exception.class)
    public ModelAndView handleException(HttpServletRequest request, Exception ex, Model model) {
        if (getHttpStatus(request).equals(HttpStatus.INTERNAL_SERVER_ERROR)) {
            model.addAttribute("errorMessage", ex.getMessage());
            return new ModelAndView("redirect:/errorPages/error500.xhtml");
        }
        return new ModelAndView("redirect:/errorPages/error500.xhtml");
    }

    private HttpStatus getHttpStatus(HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        if (statusCode == null) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return HttpStatus.valueOf(statusCode);
    }
}
