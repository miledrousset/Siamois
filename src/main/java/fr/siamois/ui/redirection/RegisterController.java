package fr.siamois.ui.redirection;

import fr.siamois.domain.models.auth.PendingPerson;
import fr.siamois.domain.services.person.PersonService;
import fr.siamois.ui.bean.RegisterBean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.faces.bean.SessionScoped;
import java.util.Optional;

@Controller
@SessionScoped
public class RegisterController {

    private final PersonService personService;
    private final RegisterBean registerBean;

    public RegisterController(PersonService personService, RegisterBean registerBean) {
        this.personService = personService;
        this.registerBean = registerBean;
    }

    @GetMapping("/register/{token}")
    public String goToRegister(@PathVariable String token) {
        Optional<PendingPerson> pendingPerson = personService.findPendingByToken(token);
        if (pendingPerson.isEmpty()) {
            return "redirect:/error/404";
        }

        registerBean.init(pendingPerson.get());

        return "forward:/pages/login/register.xhtml";
    }

}
