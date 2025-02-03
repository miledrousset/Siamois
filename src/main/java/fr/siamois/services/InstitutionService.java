package fr.siamois.services;

import fr.siamois.infrastructure.repositories.InstitutionRepository;
import fr.siamois.models.Institution;
import fr.siamois.models.auth.Person;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class InstitutionService {

    private final InstitutionRepository institutionRepository;

    public InstitutionService(InstitutionRepository institutionRepository) {
        this.institutionRepository = institutionRepository;
    }

    public List<Institution> findAll() {
        List<Institution> result = new ArrayList<>();
        for (Institution institution : institutionRepository.findAll())
            result.add(institution);
        return result;
    }

    public List<Institution> findInstitutionsOfPerson(Person person) {
        return institutionRepository.findAllOfPerson(person.getId());
    }

}
