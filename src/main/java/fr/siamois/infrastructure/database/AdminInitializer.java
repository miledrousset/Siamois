package fr.siamois.infrastructure.database;

import fr.siamois.domain.models.Institution;
import fr.siamois.domain.models.auth.Person;
import fr.siamois.infrastructure.database.repositories.InstitutionRepository;
import fr.siamois.infrastructure.database.repositories.auth.PersonRepository;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Component
@Getter
@Setter
public class AdminInitializer {

    private final BCryptPasswordEncoder passwordEncoder;
    private final PersonRepository personRepository;
    private final InstitutionRepository institutionRepository;

    @Value("${siamois.admin.username:admin}")
    private String adminUsername;

    @Value("${siamois.admin.password:admin}")
    private String adminPassword;

    @Value("${siamois.admin.email:admin@example.com}")
    private String adminEmail;

    private Person createdAdmin;

    public AdminInitializer(BCryptPasswordEncoder passwordEncoder,
                            PersonRepository personRepository,
                            InstitutionRepository institutionRepository) {
        this.passwordEncoder = passwordEncoder;
        this.personRepository = personRepository;
        this.institutionRepository = institutionRepository;
    }

    /**
     * Marks all previous person with super admin flag as FALSE if username is different then adminUsername.
     * @return true if the admin was created, false if it already existed
     */
    public boolean initializeAdmin() {
        if (processExistingAdmins()) return false;

        Person person = new Person();
        person.setUsername(adminUsername);
        person.setPassword(passwordEncoder.encode(adminPassword));
        person.setMail(adminEmail);
        person.setName("Admin");
        person.setLastname("Admin");
        person.setIsSuperAdmin(true);
        person.setEnabled(true);

        try {
            createdAdmin = personRepository.save(person);
        } catch (DataIntegrityViolationException e) {
            log.error("User with username {} already exists and is not SUPER ADMIN but is supposed to. " +
                    "Check the database manually", adminUsername);
            throw e;
        }

        return true;
    }

    private boolean processExistingAdmins() {
        List<Person> admins = personRepository.findAllByIsSuperAdmin(true);
        Person adminWithUsername = null;
        for (Person admin : admins) {
            if (isNotAskedAdmin(admin)) {
                admin.setIsSuperAdmin(false);
                personRepository.save(admin);
            } else {
                adminWithUsername = admin;
            }
        }

        if (adminWithUsername != null) {
            createdAdmin = adminWithUsername;
            return true;
        }
        return false;
    }

    private boolean isNotAskedAdmin(Person admin) {
        return !admin.getUsername().equalsIgnoreCase(adminUsername);
    }

    /**
     * Creates the Siamois Administration organisation if it doesn't exist. Changes the manager of the organisation
     * to the current admin
     * @return true if the Siamois Administration organisation is created, false otherwise.
     */
    public boolean initializeAdminOrganization() {
        if (processExistingInstitution()) return false;

        Institution institution = new Institution();
        institution.setName("Siamois Administration");
        institution.setDescription("Institution par défaut pour administrer Siamois");
        institution.setManager(createdAdmin);
        institution.setIdentifier("SIAMOIS");

        institutionRepository.save(institution);
        return true;
    }

    private boolean processExistingInstitution() {
        Institution institution;
        Optional<Institution> optInstitution = institutionRepository.findInstitutionByIdentifier("SIAMOIS");
        if (optInstitution.isPresent()) {
            institution = optInstitution.get();
            if (createdAdminIsNotOwnerOf(institution)) {
                institution.setManager(createdAdmin);
                institutionRepository.save(institution);
            }
            return true;
        }
        return false;
    }

    private boolean createdAdminIsNotOwnerOf(Institution institution) {
        return !Objects.equals(institution.getManager().getId(), createdAdmin.getId());
    }

}
