package fr.siamois.bean.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.siamois.models.vocabulary.Concept;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import org.springframework.stereotype.Component;

@Component
public class ConceptConverter implements Converter<Concept> {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public Concept getAsObject(FacesContext facesContext, UIComponent uiComponent, String s) {
        try {
            return mapper.readValue(s, Concept.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent uiComponent, Concept concept) {
        try {
            return mapper.writeValueAsString(concept);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
