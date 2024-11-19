package fr.siamois.config;

/**
@ControllerAdvice
public class GlobalExceptionHandler {

    // Gestion des erreurs 500
    @ExceptionHandler(Exception.class)
    public ModelAndView handleException(HttpServletRequest request, Exception ex, Model model) {
        if (getHttpStatus(request).equals(HttpStatus.INTERNAL_SERVER_ERROR)) {
            model.addAttribute("errorMessage", ex.getMessage());
            return new ModelAndView("redirect:/errorPages/error500.xhtml");
        }

        model.addAttribute("errorMessage", ex.getMessage());
        return new ModelAndView("redirect:/errorPages/errorGeneric.xhtml");
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

    private HttpStatus getHttpStatus(HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        if (statusCode == null) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return HttpStatus.valueOf(statusCode);
    }
}
 **/
