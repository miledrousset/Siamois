package fr.siamois.bean.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.siamois.models.vocabulary.Concept;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@FacesConverter("conceptConverter")
@Component("conceptConverter")
public class ConceptConverter implements Converter<Concept> {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public Concept getAsObject(FacesContext facesContext, UIComponent uiComponent, String s) {
        try {
            return mapper.readValue(s, Concept.class);
        } catch (JsonProcessingException e) {
            log.error("Error while converting string to concept", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent uiComponent, Concept concept) {
        try {
            return mapper.writeValueAsString(concept);
        } catch (JsonProcessingException e) {
            log.error("Error while converting concept to string", e);
            throw new RuntimeException(e);
        }
    }
}