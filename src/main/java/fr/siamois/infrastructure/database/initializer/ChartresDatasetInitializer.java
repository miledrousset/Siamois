package fr.siamois.infrastructure.database.initializer;

import fr.siamois.domain.models.actionunit.ActionUnit;
import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.exceptions.ErrorProcessingExpansionException;
import fr.siamois.domain.models.exceptions.api.InvalidEndpointException;
import fr.siamois.domain.models.exceptions.api.NotSiamoisThesaurusException;
import fr.siamois.domain.models.exceptions.database.DatabaseDataInitException;
import fr.siamois.domain.models.institution.Institution;
import fr.siamois.domain.models.recordingunit.RecordingUnit;
import fr.siamois.domain.models.spatialunit.SpatialUnit;
import fr.siamois.domain.models.vocabulary.Concept;
import fr.siamois.domain.models.vocabulary.Vocabulary;
import fr.siamois.domain.models.vocabulary.VocabularyType;
import fr.siamois.domain.models.vocabulary.label.ConceptLabel;
import fr.siamois.domain.models.vocabulary.label.Label;
import fr.siamois.domain.services.vocabulary.FieldConfigurationService;
import fr.siamois.domain.services.vocabulary.VocabularyService;
import fr.siamois.infrastructure.database.repositories.SpatialUnitRepository;
import fr.siamois.infrastructure.database.repositories.actionunit.ActionUnitRepository;
import fr.siamois.infrastructure.database.repositories.institution.InstitutionRepository;
import fr.siamois.infrastructure.database.repositories.person.PersonRepository;
import fr.siamois.infrastructure.database.repositories.recordingunit.RecordingUnitRepository;
import fr.siamois.infrastructure.database.repositories.specimen.SpecimenRepository;
import fr.siamois.infrastructure.database.repositories.vocabulary.ConceptRepository;
import fr.siamois.infrastructure.database.repositories.vocabulary.VocabularyRepository;
import fr.siamois.infrastructure.database.repositories.vocabulary.VocabularyTypeRepository;
import fr.siamois.infrastructure.database.repositories.vocabulary.label.ConceptLabelRepository;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Component
@Getter
@Setter
public class ChartresDatasetInitializer implements DatabaseInitializer {

    private final SpatialUnitRepository spatialUnitRepositoryRepository;
    private final ActionUnitRepository actionUnitRepository;
    private final RecordingUnitRepository recordingUnitRepository;
    private final SpecimenRepository specimenRepository;
    private final PersonRepository personRepository;
    private final InstitutionRepository institutionRepository;
    private final VocabularyRepository vocabularyRepository;
    private final VocabularyTypeRepository vocabularyTypeRepository;
    private final SpatialUnitRepository spatialUnitRepository;
    private final ConceptRepository conceptRepository;
    private final ConceptLabelRepository conceptLabelRepository;
    private final FieldConfigurationService fieldConfigurationService;
    private final VocabularyService vocabularyService;


    private Person admin;
    private Institution createdInstitution;
    private Set<SpatialUnit> spatialContext;
    private Vocabulary thesaurus;
    private SpatialUnit chartes;
    private Person author;
    private ActionUnit actionUnit;


    @Value("${siamois.admin.username}")
    private String adminUsername;

    public ChartresDatasetInitializer(SpatialUnitRepository spatialUnitRepositoryRepository,
                                      ActionUnitRepository actionUnitRepository,
                                      RecordingUnitRepository recordingUnitRepository,
                                      SpecimenRepository specimenRepository,
                                      PersonRepository personRepository,
                                      InstitutionRepository institutionRepository,
                                      VocabularyRepository vocabularyRepository,
                                      VocabularyTypeRepository vocabularyTypeRepository,
                                      SpatialUnitRepository spatialUnitRepository, ConceptRepository conceptRepository, ConceptLabelRepository conceptLabelRepository, FieldConfigurationService fieldConfigurationService, VocabularyService vocabularyService) {
        this.spatialUnitRepositoryRepository = spatialUnitRepositoryRepository;
        this.actionUnitRepository = actionUnitRepository;
        this.recordingUnitRepository = recordingUnitRepository;
        this.specimenRepository = specimenRepository;
        this.personRepository = personRepository;
        this.institutionRepository = institutionRepository;
        this.vocabularyRepository = vocabularyRepository;
        this.vocabularyTypeRepository = vocabularyTypeRepository;
        this.spatialUnitRepository = spatialUnitRepository;
        this.conceptRepository = conceptRepository;
        this.conceptLabelRepository = conceptLabelRepository;
        this.fieldConfigurationService = fieldConfigurationService;
        this.vocabularyService = vocabularyService;
    }

    /**
        Insert chartres test dataset into DB
     */
    @Override
    @Transactional
    public void initialize() throws DatabaseDataInitException {
        getAdmin();
        initializeOrganization();
        initializeThesaurus();
        initializeSpatialUnits();
        initializeActions();
        initializeRecordings();
    }

    private void initializeThesaurus() throws DatabaseDataInitException {
        // Verify if the thesaurus is already imported in SIAMOIS
        Optional<Vocabulary> optVocabulary = vocabularyRepository.findVocabularyByBaseUriAndVocabExternalId(
                "https://thesaurus.mom.fr",
                "th240"
        );
        if(optVocabulary.isPresent()) {
            thesaurus = optVocabulary.get();
        }
        else {
            try {
                thesaurus = vocabularyService.findOrCreateVocabularyOfUri("https://thesaurus.mom.fr/?idt=th240");
            } catch (InvalidEndpointException e) {
                throw new DatabaseDataInitException("error with thesaurus init",e);
            }
            try {
                fieldConfigurationService.setupFieldConfigurationForInstitution(createdInstitution, thesaurus);
            } catch (NotSiamoisThesaurusException | ErrorProcessingExpansionException e) {
                throw new DatabaseDataInitException("error with thesaurus init",e);
            }
        }
    }

    void initializeSpatialUnits() {
        // Add concepts
        Optional<Concept> optParcelle = conceptRepository.findConceptByExternalIdIgnoreCase("th240","4287532");
        Concept parcelle = null;
        if(optParcelle.isPresent()) {
            parcelle = optParcelle.get();
        }
        else {
            parcelle = new Concept();
            parcelle.setExternalId("4287532");
            parcelle.setVocabulary(thesaurus);
            parcelle = conceptRepository.save(parcelle);
            ConceptLabel label = new ConceptLabel();
            label.setConcept(parcelle);
            label.setValue("Parcelle");
            label.setLangCode("fr");
            conceptLabelRepository.save(label);
        }

        Optional<Concept> optCommune = conceptRepository.findConceptByExternalIdIgnoreCase("th240","4282370");
        Concept commune = null;
        if(optCommune.isPresent()) {
            commune = optCommune.get();
        }
        else {
            commune = new Concept();
            commune.setExternalId("4282370");
            commune.setVocabulary(thesaurus);
            commune = conceptRepository.save(commune);
            ConceptLabel label = new ConceptLabel();
            label.setConcept(commune);
            label.setValue("Commune");
            label.setLangCode("fr");
            conceptLabelRepository.save(label);
        }

        Optional<Person> optAuthor = personRepository.findByEmailIgnoreCase("anais.pinhede@siamois.fr");
        if(optAuthor.isPresent()) {
            author = optAuthor.get();
        }
        else {
            author = new Person();
            author.setUsername("anais.pinhede");
            author.setName("Anaïs");
            author.setLastname("Pinhède");
            author.setEmail("anais.pinhede@siamois.fr");
            author.setPassword("mysuperstrongpassword");
            personRepository.save(author);
        }

        Optional<SpatialUnit> optChartres = spatialUnitRepository.findByNameAndInstitution("Chartres", createdInstitution.getId());
        if(optChartres.isPresent()) {
            chartes = optChartres.get();
            spatialContext = chartes.getChildren();
        }
        else {
            SpatialUnit parcelleDA154 = new SpatialUnit(); parcelleDA154.setName("Parcelle DA 154"); parcelleDA154.setCategory(parcelle);
            parcelleDA154.setCreatedByInstitution(createdInstitution); parcelleDA154.setAuthor(author);
            SpatialUnit parcelleDA155 = new SpatialUnit(); parcelleDA155.setName("Parcelle DA 155"); parcelleDA155.setCategory(parcelle);
            parcelleDA155.setCreatedByInstitution(createdInstitution); parcelleDA155.setAuthor(author);
            SpatialUnit chartres = new SpatialUnit(); chartres.setName("Chartres"); chartres.setCategory(commune);
            chartres.setCreatedByInstitution(createdInstitution); chartres.setAuthor(author);
            parcelleDA154 = spatialUnitRepository.save(parcelleDA154);
            parcelleDA155 = spatialUnitRepository.save(parcelleDA155);
            chartres.getChildren().add(parcelleDA154);
            chartres.getChildren().add(parcelleDA155);
            spatialUnitRepository.save(chartres);
        }
    }

    void initializeActions() {

        Optional<Concept> optPrevFouille = conceptRepository.findConceptByExternalIdIgnoreCase("th240","4287534");
        Concept prevFouille = null;
        if(optPrevFouille.isPresent()) {
            prevFouille = optPrevFouille.get();
        }
        else {
            prevFouille = new Concept();
            prevFouille.setExternalId("4287534");
            prevFouille.setVocabulary(thesaurus);
            prevFouille = conceptRepository.save(prevFouille);
            ConceptLabel label = new ConceptLabel();
            label.setConcept(prevFouille);
            label.setValue("Fouille préventive");
            label.setLangCode("fr");
            conceptLabelRepository.save(label);
        }

        Optional<Concept> optArcheoDiag = conceptRepository.findConceptByExternalIdIgnoreCase("th240","4287533");
        Concept archeoDiag = null;
        if(optArcheoDiag.isPresent()) {
            archeoDiag = optArcheoDiag.get();
        }
        else {
            archeoDiag = new Concept();
            archeoDiag.setExternalId("4287534");
            archeoDiag.setVocabulary(thesaurus);
            archeoDiag = conceptRepository.save(archeoDiag);
            ConceptLabel label = new ConceptLabel();
            label.setConcept(archeoDiag);
            label.setValue("Diagnostic archéologique");
            label.setLangCode("fr");
            conceptLabelRepository.save(label);
        }

        Optional<ActionUnit> optAU = actionUnitRepository.findByIdentifierAndCreatedByInstitution("C309_01", createdInstitution);
        if(optAU.isPresent()) {
            actionUnit = optAU.get();
        }
        else {
            // create it
            actionUnit = new ActionUnit();
            actionUnit.setCreatedByInstitution(createdInstitution);
            actionUnit.setIdentifier("C309_01");
            actionUnit.setName("Pôle Gare - Phase 1");
            actionUnit.setAuthor(author);
            actionUnit.setType(archeoDiag);
            actionUnit.setSpatialContext(spatialContext);
            actionUnit = actionUnitRepository.save(actionUnit);
            // create a second one
            ActionUnit action2 = new ActionUnit();
            action2.setCreatedByInstitution(createdInstitution);
            action2.setIdentifier("C309_11");
            action2.setName("Pôle Gare - rue du Chemin de Fer et rue du Faubourg Saint-Jean (phase 1)");
            action2.setAuthor(author);
            action2.setType(prevFouille);
            actionUnitRepository.save(action2);
        }


    }

    void initializeRecordings() {

        // Recording 1100
        Optional<RecordingUnit> optRU1100 = recordingUnitRepository.findByIdentifierAndCreatedByInstitution(1100, createdInstitution);
        RecordingUnit ru1100;
        if(optRU1100.isPresent()) {

        }
        else {
            ru1100 = new RecordingUnit();
            ru1100.setCreatedByInstitution(createdInstitution);
            ru1100.setIdentifier(1100);
            ru1100.setFullIdentifier("chartes-C309-01-1100");
            ru1100.setAuthors(List.of(author));
            ru1100.setExcavators(List.of(author));
        }


    }



    /**
     * Creates the Chartres organisation if it doesn't exist. Changes the manager of the organisation
     * to the current admin
     */
    void initializeOrganization() {
        if (processExistingInstitution()) return;

        Institution institution = new Institution();
        institution.setName("Chartres (Test équipe dev)");
        institution.setDescription("Insertion du jeu de donnée fourni par Anaïs Pinhède");
        institution.getManagers().add(admin);
        institution.setIdentifier("chartres");

        createdInstitution = institutionRepository.save(institution);

        log.info("Created institution {}", institution.getIdentifier());
    }

    private boolean createdAdminIsNotOwnerOf(Set<Person> managers) {
        return managers
                .stream()
                .noneMatch(adm -> adm.getId().equals(admin.getId()));
    }

    protected boolean processExistingInstitution() {
        Institution institution;
        Optional<Institution> optInstitution = institutionRepository.findInstitutionByIdentifier("chartres");
        if (optInstitution.isPresent()) {
            institution = optInstitution.get();
            if (createdAdminIsNotOwnerOf(institution.getManagers())) {
                institution.getManagers().add(admin);
                institutionRepository.save(institution);
            }
            log.debug("Institution already exists: {}", institution.getName());
            createdInstitution = institution;
            return true;
        }
        return false;
    }

    void getAdmin() throws DatabaseDataInitException {
        if (processExistingAdmins()) return;

        throw new DatabaseDataInitException("Cant' find superadmin account", new Exception());
    }

    /*
     * @return True if wanted admin already exist, false otherwise
     */
    private boolean processExistingAdmins() {
        List<Person> admins = personRepository.findAllSuperAdmin();
        Person adminWithUsername = null;
        for (Person admin : admins) {
            if (isNotAskedAdmin(admin)) {
                admin.setSuperAdmin(false);
                personRepository.save(admin);
            } else {
                adminWithUsername = admin;
            }
        }

        if (adminWithUsername != null) {
            admin = adminWithUsername;
            return true;
        }
        return false;
    }

    private boolean isNotAskedAdmin(Person admin) {
        return !admin.getUsername().equalsIgnoreCase(adminUsername);
    }
}
