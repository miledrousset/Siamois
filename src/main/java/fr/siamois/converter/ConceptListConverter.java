package fr.siamois.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.siamois.domain.models.vocabulary.Concept;
import fr.siamois.infrastructure.database.repositories.vocabulary.ConceptRepository;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Converter
@Component
@Slf4j
public class ConceptListConverter implements AttributeConverter<List<Concept>, String> {

    @Autowired
    private ApplicationContext applicationContext;

    // Lazy getter for the repository
    private ConceptRepository getConceptRepository() {
        return applicationContext.getBean(ConceptRepository.class);
    }

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<Concept> concepts) {
        if (concepts == null || concepts.isEmpty()) {
            return null;
        }
        List<Long> ids = concepts.stream()
                .map(Concept::getId)
                .collect(Collectors.toList());
        try {
            return objectMapper.writeValueAsString(ids);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializing Concept list to JSON", e);
        }
    }

    @Override
    public List<Concept> convertToEntityAttribute(String json) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        try {
            List<Long> ids = objectMapper.readValue(json, new TypeReference<List<Long>>() {});
            return (List<Concept>) getConceptRepository().findAllById(ids);
        } catch (IOException e) {
            log.error("Error deserializing Concept list from JSON", e);
            throw new RuntimeException("Error deserializing JSON to Concept list", e);
        }
    }
}