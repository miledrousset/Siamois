package fr.siamois.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.siamois.domain.models.form.customForm.CustomFormPanel;
import fr.siamois.domain.models.form.customField.CustomField;
import fr.siamois.infrastructure.repositories.form.CustomFieldRepository;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Converter
@Component
@Slf4j
public class CustomFormLayoutConverter implements AttributeConverter<List<CustomFormPanel>, String> {

    @Autowired
    private ApplicationContext applicationContext;

    private CustomFieldRepository getCustomFieldRepository() {
        return applicationContext.getBean(CustomFieldRepository.class);
    }

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<CustomFormPanel> layout) {
        try {
            // Convert CustomFormPanel objects to a serializable format
            List<Map<String, Object>> serializableLayout = layout.stream()
                    .map(panel -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("className", panel.getClassName());

                        // Store the IDs of all fields
                        if (panel.getFields() != null) {
                            List<Long> fieldIds = panel.getFields().stream()
                                    .map(CustomField::getId)
                                    .collect(Collectors.toList());
                            map.put("fields", fieldIds);
                        }
                        return map;
                    })
                    .collect(Collectors.toList());

            return objectMapper.writeValueAsString(serializableLayout);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializing CustomFormPanel list to JSON", e);
        }
    }

    @Override
    public List<CustomFormPanel> convertToEntityAttribute(String json) {
        try {
            // First parse the JSON to a list of maps
            List<Map<String, Object>> parsedData = objectMapper.readValue(
                    json, new TypeReference<List<Map<String, Object>>>() {
                    });

            // Then convert each map to a CustomFormPanel
            return parsedData.stream()
                    .map(map -> {
                        CustomFormPanel panel = new CustomFormPanel();
                        panel.setClassName((String) map.get("className"));

                        // Get all fields from the repository using field IDs
                        if (map.containsKey("fields")) {
                            List<Integer> fieldIds = (List<Integer>) map.get("fields");
                            if (fieldIds != null) {
                                List<CustomField> fields = fieldIds.stream()
                                        .map(id -> getCustomFieldRepository().findById(id.longValue())
                                                .orElse(null))
                                        .filter(Objects::nonNull)
                                        .collect(Collectors.toList());
                                panel.setFields(fields);
                            }
                        }

                        return panel;
                    })
                    .collect(Collectors.toList());
        } catch (IOException e) {
            log.error("Error deserializing JSON to CustomFormPanel list", e);
            throw new RuntimeException("Error deserializing JSON to CustomFormPanel list", e);
        }
    }
}