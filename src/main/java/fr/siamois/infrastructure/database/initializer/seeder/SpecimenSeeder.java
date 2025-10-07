package fr.siamois.infrastructure.database.initializer.seeder;

import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.institution.Institution;
import fr.siamois.domain.models.recordingunit.RecordingUnit;
import fr.siamois.domain.models.specimen.Specimen;
import fr.siamois.domain.models.vocabulary.Concept;
import fr.siamois.infrastructure.database.repositories.institution.InstitutionRepository;
import fr.siamois.infrastructure.database.repositories.person.PersonRepository;
import fr.siamois.infrastructure.database.repositories.recordingunit.RecordingUnitRepository;
import fr.siamois.infrastructure.database.repositories.specimen.SpecimenRepository;
import fr.siamois.infrastructure.database.repositories.vocabulary.ConceptRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SpecimenSeeder {

    private final PersonRepository personRepository;
    private final ConceptRepository conceptRepository;
    private final InstitutionRepository institutionRepository;
    private final RecordingUnitRepository recordingUnitRepository;
    private final RecordingUnitSeeder recordingUnitSeeder;
    private final SpecimenRepository specimenRepository;
    private final PersonSeeder personSeeder;

    public record SpecimenSpecs(String fullIdentifier, Integer identifier,
                                     ConceptSeeder.ConceptKey type,
                                     ConceptSeeder.ConceptKey category,
                                     String authorEmail,
                                     String institutionIdentifier,
                                     List<String> authors,
                                     List<String> collectors,
                                     OffsetDateTime creationTime,
                                     RecordingUnitSeeder.RecordingUnitKey recordingUnitKey) {

    }

    private void getOrCreateSpecimen(Specimen specimen) {

        Optional<Specimen> opt = specimenRepository.findByFullIdentifier(specimen.getFullIdentifier());
        if (opt.isEmpty()) {
            specimenRepository.save(specimen);
        }
    }


    public void seed(List<SpecimenSpecs> specs) {

        for (var s : specs) {
            // Find Type
            Concept type = recordingUnitSeeder.getConceptFromKey(s.type);
            Concept cat = recordingUnitSeeder.getConceptFromKey(s.category);
            // Find author
            Person author = personSeeder.findPersonOrReturnNull(s.authorEmail);

            // Find Institution
            Institution institution = institutionRepository.findInstitutionByIdentifier(s.institutionIdentifier)
                    .orElseThrow(() -> new IllegalStateException("Institution introuvable"));

            List<Person> authors = new ArrayList<>();
            List<Person> collectors = new ArrayList<>();
            if (s.authors != null) {
                for (var email : s.authors) {
                    Person p = personSeeder.findPersonOrReturnNull(email);
                    authors.add(p);
                }
            }
            if (s.collectors != null) {
                for (var email : s.collectors) {
                    Person p = personSeeder.findPersonOrReturnNull(email);
                    collectors.add(p);
                }
            }

            RecordingUnit ru = recordingUnitSeeder.getRecordingUnitFromKey(s.recordingUnitKey);

            Specimen toGetOrCreate = new Specimen();
            toGetOrCreate.setCreatedByInstitution(institution);
            toGetOrCreate.setIdentifier(s.identifier);
            toGetOrCreate.setCategory(cat);
            toGetOrCreate.setAuthor(author);
            toGetOrCreate.setFullIdentifier(s.fullIdentifier);
            toGetOrCreate.setType(type);
            toGetOrCreate.setRecordingUnit(ru);
            toGetOrCreate.setAuthors(authors);
            toGetOrCreate.setCollectors(collectors);
            toGetOrCreate.setCreationTime(s.creationTime);
            getOrCreateSpecimen(toGetOrCreate);

        }
    }
}

