package fr.siamois.bean;

import fr.siamois.models.Person;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.Serializable;

@SessionScoped
@Named("helloBean")
@Getter
@Setter
public class HelloBean implements Serializable {

    private String username;

    @PostConstruct
    public void init() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth.isAuthenticated()) {
            Person person = (Person) auth.getPrincipal();
            username = person.getAuthorities().toString();
        }
    }

}
