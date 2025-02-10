package fr.siamois.bean.converter;

import fr.siamois.models.auth.Person;
import fr.siamois.services.PersonService;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.faces.bean.ManagedBean;
import java.io.Serializable;

@ManagedBean
@Component
@Slf4j
public class PersonConverter implements Converter<Person>, Serializable {

    private final transient PersonService personService;

    public PersonConverter(PersonService personService) {
        this.personService = personService;
    }

    @Override
    public Person getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }

        // Convert ID (String) to Person object
        return personService.findById(Long.parseLong(value));
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Person value) {
        if (value != null) {
            return String.valueOf(value.getId());
        }
        else {
            return null;
        }
    }
}
