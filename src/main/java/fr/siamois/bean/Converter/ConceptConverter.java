package fr.siamois.bean.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.siamois.models.vocabulary.Concept;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Slf4j
@Component
public class ConceptConverter implements Converter<Concept>, Serializable {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public Concept getAsObject(FacesContext facesContext, UIComponent uiComponent, String s) {
        try {
            return mapper.readValue(s, Concept.class);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent uiComponent, Concept concept) {
        try {
            return mapper.writeValueAsString(concept);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }
}