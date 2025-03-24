package fr.siamois.infrastructure.database.initializer;

import fr.siamois.domain.models.Institution;
import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.exceptions.database.DatabaseDataInitException;
import fr.siamois.infrastructure.database.repositories.InstitutionRepository;
import fr.siamois.infrastructure.database.repositories.auth.PersonRepository;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
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
@Order(1)
public class AdminInitializer implements DatabaseInitializer {

    private final BCryptPasswordEncoder passwordEncoder;
    private final PersonRepository personRepository;
    private final InstitutionRepository institutionRepository;
    private final ApplicationContext applicationContext;

    @Value("${siamois.admin.username:admin}")
    private String adminUsername;

    @Value("${siamois.admin.password:admin}")
    private String adminPassword;

    @Value("${siamois.admin.email:admin@example.com}")
    private String adminEmail;

    private Person createdAdmin;

    public AdminInitializer(BCryptPasswordEncoder passwordEncoder,
                            PersonRepository personRepository,
                            InstitutionRepository institutionRepository,
                            ApplicationContext applicationContext) {
        this.passwordEncoder = passwordEncoder;
        this.personRepository = personRepository;
        this.institutionRepository = institutionRepository;
        this.applicationContext = applicationContext;
    }

    /**
     * Marks all previous person with super admin flag as FALSE if username is different then adminUsername.
     */
    @Override
    public void initialize() throws DatabaseDataInitException {
        initializeAdmin();
        initializeAdminOrganization();
    }

    public void initializeAdmin() throws DatabaseDataInitException {
        if (processExistingAdmins()) return;

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
            log.info("Created admin: {}", createdAdmin.getUsername());
        } catch (DataIntegrityViolationException e) {
            log.error("User with username {} already exists and is not SUPER ADMIN but is supposed to. " +
                    "Check the database manually", adminUsername, e);
            throw new DatabaseDataInitException("Super admin account started wrongly.", e);
        }
    }

    /*
     * @return True if wanted admin already exist, false otherwhise
     */
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
            log.info("Super admin already exists: {}", createdAdmin.getUsername());
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
     */
    public void initializeAdminOrganization() {
        if (processExistingInstitution()) return;

        Institution institution = new Institution();
        institution.setName("Siamois Administration");
        institution.setDescription("Institution par défaut pour administrer Siamois");
        institution.setManager(createdAdmin);
        institution.setIdentifier("SIAMOIS");

        institutionRepository.save(institution);

        log.info("Created institution {}", institution.getIdentifier());
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
            log.info("Institution already exists: {}", institution.getName());
            return true;
        }
        return false;
    }

    private boolean createdAdminIsNotOwnerOf(Institution institution) {
        return !Objects.equals(institution.getManager().getId(), createdAdmin.getId());
    }

}
