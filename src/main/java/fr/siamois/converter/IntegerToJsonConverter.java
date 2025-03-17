package fr.siamois.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class IntegerToJsonConverter implements AttributeConverter<Integer, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Integer attribute) {
        if (attribute == null) {
            return null;
        }
        try {
            // This will create a properly formatted JSON string with quotes: "42"
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert Integer to JSON string", e);
        }
    }

    @Override
    public Integer convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        try {
            // Convert the JSON string back to Integer
            return objectMapper.readValue(dbData, new TypeReference<Integer>() {});

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert JSON string to Integer", e);
        }
    }
}
