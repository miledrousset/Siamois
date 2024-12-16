package fr.siamois.bean.Converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import fr.siamois.infrastructure.api.dto.ConceptFieldDTO;
import fr.siamois.models.Team;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Slf4j
@Component
public class ConceptFieldDTOConverter implements Converter<ConceptFieldDTO> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public ConceptFieldDTO getAsObject(FacesContext facesContext, UIComponent uiComponent, String s) {
        try {
            return objectMapper.readValue(s, ConceptFieldDTO.class);
        } catch (JsonProcessingException e) {
            log.error("Error while converting string to Team object", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent uiComponent, ConceptFieldDTO o) {
        try {
            return objectMapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            log.error("Error while converting Team object to string", e);
            throw new RuntimeException(e);
        }
    }


}
