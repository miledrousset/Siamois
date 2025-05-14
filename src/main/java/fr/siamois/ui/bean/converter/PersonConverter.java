package fr.siamois.ui.bean.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.siamois.domain.models.auth.Person;
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


    private final ObjectMapper objectMapper = new ObjectMapper();


    @Override
    public Person getAsObject(FacesContext context, UIComponent component, String value) {

        try {
            return objectMapper.readValue(value, Person.class);
        } catch (JsonProcessingException e) {
            log.error("Error while converting string to Person object", e);
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Person value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            log.error("Error while converting Team object to string", e);
            return null;
        }
    }
}
