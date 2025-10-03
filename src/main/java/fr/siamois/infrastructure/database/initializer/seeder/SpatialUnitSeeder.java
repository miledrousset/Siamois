package fr.siamois.infrastructure.database.initializer.seeder;


import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.institution.Institution;
import fr.siamois.domain.models.spatialunit.SpatialUnit;
import fr.siamois.domain.models.vocabulary.Concept;
import fr.siamois.infrastructure.database.repositories.SpatialUnitRepository;
import fr.siamois.infrastructure.database.repositories.institution.InstitutionRepository;
import fr.siamois.infrastructure.database.repositories.person.PersonRepository;
import fr.siamois.infrastructure.database.repositories.vocabulary.ConceptRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
@RequiredArgsConstructor

public class SpatialUnitSeeder {
    private final PersonRepository personRepository;
    private final ConceptRepository conceptRepository;
    private final InstitutionRepository institutionRepository;
    private final SpatialUnitRepository spatialUnitRepository;

    public record SpatialUnitSpecs(String name, String typeVocabularyExtId, String typeConceptExtId, String authorEmail,
                                   String institutionIdentifier, Set<SpatialUnitKey> childrenKey) {

    }

    public record SpatialUnitKey(String unitName) {}

    private SpatialUnit getOrCreateSpatialUnit(SpatialUnit spatialUnit) {

        Optional<SpatialUnit> opt = spatialUnitRepository.findByNameAndInstitution(spatialUnit.getName(), spatialUnit.getCreatedByInstitution().getId());

        return opt.orElseGet(() -> spatialUnitRepository.save(spatialUnit));
    }

    public Map<String, SpatialUnit> seed(List<SpatialUnitSpecs> specs) {
        Map<String, SpatialUnit> result = new HashMap<>();
        for (var s : specs) {
            // Find Type
            Concept type = conceptRepository
                    .findConceptByExternalIdIgnoreCase(s.typeVocabularyExtId, s.typeConceptExtId)
                    .orElseThrow(() -> new IllegalStateException("Concept introuvable"));
            // Find author
            Person author = personRepository
                    .findByEmailIgnoreCase(s.authorEmail)
                    .orElseThrow(() -> new IllegalStateException("Auteur introuvable"));
            // Find Institution
            Institution institution = institutionRepository.findInstitutionByIdentifier(s.institutionIdentifier)
                    .orElseThrow(() -> new IllegalStateException("Institution introuvable"));
            Set<SpatialUnit> children = new HashSet<>();
            if(s.childrenKey != null) {
                for(var childKey : s.childrenKey) {
                    SpatialUnit child = spatialUnitRepository.findByNameAndInstitution(childKey.unitName, institution.getId())
                            .orElseThrow(() -> new IllegalStateException("Enfant introuvable"));
                    children.add(child);
                }
            }

            SpatialUnit toGetOrCreate = new SpatialUnit();
            toGetOrCreate.setName(s.name);
            toGetOrCreate.setCreatedByInstitution(institution);
            toGetOrCreate.setAuthor(author);
            toGetOrCreate.setCategory(type);
            toGetOrCreate.setChildren(children);

            result.put(s.name, getOrCreateSpatialUnit(toGetOrCreate));
        }
        return result;
    }
}
