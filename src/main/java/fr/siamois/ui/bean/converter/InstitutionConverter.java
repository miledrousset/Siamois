package fr.siamois.ui.bean.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.siamois.domain.models.Institution;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Slf4j
@Component
public class InstitutionConverter implements Converter<Institution>, Serializable {

    private final ObjectMapper  mapper = new ObjectMapper();

    @Override
    public Institution getAsObject(FacesContext facesContext, UIComponent uiComponent, String s) {
        try {
            return mapper.readValue(s, Institution.class);
        } catch (Exception e) {
            log.error("Error while converting string institution to object", e);
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent uiComponent, Institution institution) {
        try {
            return mapper.writeValueAsString(institution);
        } catch (Exception e) {
            log.error("Error while converting institution to string", e);
            return null;
        }
    }
}
