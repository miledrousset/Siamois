package fr.siamois.bean.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.siamois.models.vocabulary.Vocabulary;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import org.springframework.stereotype.Component;

@Component
public class VocabularyConverter implements Converter<Vocabulary> {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public Vocabulary getAsObject(FacesContext facesContext, UIComponent uiComponent, String s) {
        try {
            return mapper.readValue(s, Vocabulary.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent uiComponent, Vocabulary vocabulary) {
        try {
            return mapper.writeValueAsString(vocabulary);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
