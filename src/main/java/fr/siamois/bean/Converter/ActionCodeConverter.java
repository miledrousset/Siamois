package fr.siamois.bean.Converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.siamois.models.actionunit.ActionCode;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * PrimeFaces converter for {@link ActionCode} object
 * @author Grégory Bliault
 */
@Slf4j
@Component
public class ActionCodeConverter implements Converter<ActionCode> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public ActionCode getAsObject(FacesContext facesContext, UIComponent uiComponent, String s) {
        try {
            return objectMapper.readValue(s, ActionCode.class);
        } catch (JsonProcessingException e) {
            log.error("Error while converting string to ActionCode object", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent uiComponent, ActionCode o) {
        try {
            return objectMapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            log.error("Error while converting Team object to string", e);
            throw new RuntimeException(e);
        }
    }
}
