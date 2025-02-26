package fr.siamois.view.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.siamois.domain.models.Team;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * PrimeFaces converter for {@link Team} object
 * @author Julien Linget
 */
@Slf4j
@Component
public class TeamConverter implements Converter<Team>, Serializable {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Team getAsObject(FacesContext facesContext, UIComponent uiComponent, String s) {
        try {
            return objectMapper.readValue(s, Team.class);
        } catch (JsonProcessingException e) {
            log.error("Error while converting string to Team object", e);
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent uiComponent, Team o) {
        try {
            return objectMapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            log.error("Error while converting Team object to string", e);
            return null;
        }
    }
}
