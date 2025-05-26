package fr.siamois.domain.services;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import fr.siamois.ui.bean.panel.models.panel.AbstractPanel;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Converter
public class PanelAttributeConverter implements AttributeConverter<List<AbstractPanel>, String> {

    private final ObjectMapper objectMapper;

    public PanelAttributeConverter() {
        objectMapper = new ObjectMapper();
        objectMapper.setVisibility(objectMapper.getSerializationConfig().getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withCreatorVisibility(JsonAutoDetect.Visibility.NONE)
        );
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
    }

    @Override
    public String convertToDatabaseColumn(List<AbstractPanel> abstractPanels) {
        try {
            return objectMapper.writeValueAsString(abstractPanels);
        } catch (JsonProcessingException e) {
            log.error("Error converting AbstractPanel list to JSON", e);
            return "[]";
        }
    }

    @Override
    public List<AbstractPanel> convertToEntityAttribute(String s) {
        try {
            return objectMapper.readValue(s, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            log.error("Error converting JSON to AbstractPanel list", e);
            return List.of();
        }
    }
}
