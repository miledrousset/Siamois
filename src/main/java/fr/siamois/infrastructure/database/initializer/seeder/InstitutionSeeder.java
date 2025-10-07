package fr.siamois.infrastructure.database.initializer.seeder;


import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.institution.Institution;
import fr.siamois.infrastructure.database.repositories.institution.InstitutionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
@RequiredArgsConstructor
public class InstitutionSeeder {
    private final InstitutionRepository institutionRepository;
    private final PersonSeeder personSeeder;

    public record InstitutionSpec(String name, String description, String identifier, List<String> managerEmails) {
    }

    public Institution findInstitutionOrReturnNull(String identifier) {
        Optional<Institution> opt = institutionRepository.findInstitutionByIdentifier(identifier);
        return opt.orElse(null);
    }

    private void getOrCreateInstitution(Institution i) {
        Institution opt = findInstitutionOrReturnNull(i.getIdentifier());
        if (opt == null) {
            institutionRepository.save(i);
        }
    }

    public void seed(List<InstitutionSpec> specs) {
        for (var s : specs) {
            Set<Person> managers = new HashSet<>();
            if (s.managerEmails != null) {
                for (var email : s.managerEmails) {
                    Person p = personSeeder.findPersonOrReturnNull(email);
                    if(p == null) {
                        throw new IllegalArgumentException("Invalid email: " + email);
                    }
                    managers.add(p);
                }
            }
            Institution toCreate = new Institution();
            toCreate.setName(s.name);
            toCreate.setIdentifier(s.identifier);
            toCreate.setDescription(s.description);
            toCreate.setManagers(managers);
            getOrCreateInstitution(toCreate);
        }
    }
}
