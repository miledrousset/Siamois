package fr.siamois.infrastructure.database.initializer;

import fr.siamois.domain.models.actionunit.ActionCode;
import fr.siamois.domain.models.actionunit.ActionUnit;
import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.exceptions.ErrorProcessingExpansionException;
import fr.siamois.domain.models.exceptions.api.InvalidEndpointException;
import fr.siamois.domain.models.exceptions.api.NotSiamoisThesaurusException;
import fr.siamois.domain.models.exceptions.database.DatabaseDataInitException;
import fr.siamois.domain.models.institution.Institution;
import fr.siamois.domain.models.recordingunit.RecordingUnit;
import fr.siamois.domain.models.spatialunit.SpatialUnit;
import fr.siamois.domain.models.specimen.Specimen;
import fr.siamois.domain.models.vocabulary.Concept;
import fr.siamois.domain.models.vocabulary.Vocabulary;
import fr.siamois.domain.models.vocabulary.VocabularyType;
import fr.siamois.domain.models.vocabulary.label.ConceptLabel;
import fr.siamois.domain.models.vocabulary.label.Label;
import fr.siamois.domain.services.vocabulary.FieldConfigurationService;
import fr.siamois.domain.services.vocabulary.VocabularyService;
import fr.siamois.infrastructure.database.repositories.SpatialUnitRepository;
import fr.siamois.infrastructure.database.repositories.actionunit.ActionCodeRepository;
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
import org.hibernate.type.descriptor.java.ZoneOffsetJavaType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;

@Slf4j
@Component
@Getter
@Setter
public class ChartresDatasetInitializer implements DatabaseInitializer {

    private final ActionCodeRepository actionCodeRepository;

    // petit DTO pour décrire un concept à créer
    public record ConceptSpec(String vocabularyId, String externalId, String label, String lang) {}
    public static final String VOCABULARY_ID = "th240";

    List<ConceptSpec> concepts = List.of(
            new ConceptSpec(VOCABULARY_ID, "4287534", "Fouille préventive", "fr"),
            new ConceptSpec(VOCABULARY_ID, "4287533", "Diagnostic archéologique", "fr"),
            new ConceptSpec(VOCABULARY_ID, "4287532", "Parcelle", "fr"),
            new ConceptSpec(VOCABULARY_ID, "4282370", "Commune", "fr"),
            new ConceptSpec(VOCABULARY_ID, "4282375", "Unité stratigraphique", "fr"),
            new ConceptSpec(VOCABULARY_ID, "4287539", "Dépôt", "fr"),
            new ConceptSpec(VOCABULARY_ID, "4287541", "Couche d'occupation", "fr"),
            new ConceptSpec(VOCABULARY_ID, "4287537", "Interface", "fr"),
            new ConceptSpec(VOCABULARY_ID, "4287540", "Creusement", "fr"),
            new ConceptSpec(VOCABULARY_ID, "4286252", "Individuel", "fr"),
            new ConceptSpec(VOCABULARY_ID, "4286251", "Lot", "fr"),
            new ConceptSpec(VOCABULARY_ID, "4287542", "ANImal", "fr"),
            new ConceptSpec(VOCABULARY_ID, "4287543", "METal", "fr"),
            new ConceptSpec(VOCABULARY_ID, "4283545", "Code OA", "fr")
    );

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
    private Person fouilleur1;
    private Person fouilleur2;
    private RecordingUnit ru;
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
                                      SpatialUnitRepository spatialUnitRepository, ConceptRepository conceptRepository, ConceptLabelRepository conceptLabelRepository, FieldConfigurationService fieldConfigurationService, VocabularyService vocabularyService, ActionCodeRepository actionCodeRepository) {
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
        this.actionCodeRepository = actionCodeRepository;
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
        initializeConcepts();
        initializeSpatialUnits();
        initializeActions();
        initializeRecordings();
        initializeSpecimens();
    }

    public void initConcepts(Vocabulary thesaurus, List<ConceptSpec> specs) {

        for (ConceptSpec spec : specs) {
            Optional<Concept> existing = conceptRepository.findConceptByExternalIdIgnoreCase(
                    spec.vocabularyId(), spec.externalId()
            );

            Concept concept = existing.orElseGet(() -> {
                Concept c = new Concept();
                c.setExternalId(spec.externalId());
                c.setVocabulary(thesaurus);
                return conceptRepository.save(c);
            });

            // Do we need to add the label?
            Optional<ConceptLabel> opt = conceptLabelRepository.findByConceptAndLangCode(concept, spec.lang());
            if(opt.isEmpty()) {
                ConceptLabel label = new ConceptLabel();
                label.setConcept(concept);
                label.setValue(spec.label());
                label.setLangCode(spec.lang());
                conceptLabelRepository.save(label);
            }


        }
    }

    private void initializeConcepts() {
        initConcepts(thesaurus, concepts);
    }

    private void initializeThesaurus() throws DatabaseDataInitException {
        // Verify if the thesaurus is already imported in SIAMOIS
        Optional<Vocabulary> optVocabulary = vocabularyRepository.findVocabularyByBaseUriAndVocabExternalId(
                "https://thesaurus.mom.fr",
                VOCABULARY_ID
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

    private Person getOrCreatePerson(String email, String name, String lastname, String username) {
        Optional<Person> optAuthor = personRepository.findByEmailIgnoreCase(email);
        Person authorGetOrCreated ;
        if(optAuthor.isPresent()) {
            authorGetOrCreated = optAuthor.get();
        }
        else {
            authorGetOrCreated = new Person();
            authorGetOrCreated.setUsername(username);
            authorGetOrCreated.setName(name);
            authorGetOrCreated.setLastname(lastname);
            authorGetOrCreated.setEmail(email);
            authorGetOrCreated.setPassword("mysuperstrongpassword");
            personRepository.save(authorGetOrCreated);
        }
        return authorGetOrCreated;
    }

    private ActionCode getOrCreateActionCode(String code, Concept type) {
        Optional<ActionCode> optCode = actionCodeRepository.findById(code);
        ActionCode codeGetOrCreated ;
        if(optCode.isPresent()) {
            codeGetOrCreated = optCode.get();
        }
        else {
            codeGetOrCreated = new ActionCode();
            codeGetOrCreated.setCode(code);
            codeGetOrCreated.setType(type);
            actionCodeRepository.save(codeGetOrCreated);
        }
        return codeGetOrCreated;
    }

    void initializeSpatialUnits() {
        // find concepts
        Concept parcelle = conceptRepository
                .findConceptByExternalIdIgnoreCase(VOCABULARY_ID, "4287532")
                .orElseThrow(() -> new IllegalStateException("Concept parcelle introuvable dans th240"));
        Concept commune = conceptRepository
                .findConceptByExternalIdIgnoreCase(VOCABULARY_ID, "4282370")
                .orElseThrow(() -> new IllegalStateException("Concept commune introuvable dans th240"));

        author = getOrCreatePerson("anais.pinhede@siamois.fr", "Anaïs", "Pinhède", "anais.pinhede");
        fouilleur1 = getOrCreatePerson("pascal.gibut@siamois.fr", "Pascal", "Gibut", "pascal.gibut");
        fouilleur2 = getOrCreatePerson("duflos.franck@siamois.fr", "Duflos", "Franck", "duflos.franck");

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

        // find concepts
        Concept prevFouille = conceptRepository
                .findConceptByExternalIdIgnoreCase(VOCABULARY_ID, "4287534")
                .orElseThrow(() -> new IllegalStateException("Concept prevFouille introuvable dans th240"));
        Concept archeoDiag = conceptRepository
                .findConceptByExternalIdIgnoreCase(VOCABULARY_ID, "4287533")
                .orElseThrow(() -> new IllegalStateException("Concept archeoDiag introuvable dans th240"));

        Optional<ActionUnit> optAU = actionUnitRepository.findByIdentifierAndCreatedByInstitution("C309_01", createdInstitution);
        if(optAU.isPresent()) {
            actionUnit = optAU.get();
        }
        else {
            // Get or create code
            Concept actionCodeConcept = conceptRepository
                    .findConceptByExternalIdIgnoreCase(VOCABULARY_ID, "4283545")
                    .orElseThrow(() -> new IllegalStateException("Concept actionCodeConcept introuvable dans th240"));
            ActionCode codeOA = getOrCreateActionCode("069260", actionCodeConcept);
            ActionCode codeOA2 = getOrCreateActionCode("0610216", actionCodeConcept);
            // create it
            actionUnit = new ActionUnit();
            actionUnit.setCreatedByInstitution(createdInstitution);
            actionUnit.setIdentifier("C309_01");
            actionUnit.setName("Pôle Gare - Phase 1");
            actionUnit.setAuthor(fouilleur1);
            actionUnit.setPrimaryActionCode(codeOA);
            actionUnit.setFullIdentifier("chartres-C309_01");
            actionUnit.setType(archeoDiag);
            actionUnit.setSpatialContext(spatialContext);
            actionUnit = actionUnitRepository.save(actionUnit);
            actionUnit.setBeginDate(OffsetDateTime.of(2012, 6, 12, 0, 0, 0, 0, ZoneOffset.UTC));
            actionUnit.setEndDate(OffsetDateTime.of(2012, 7, 17, 0, 0, 0, 0, ZoneOffset.UTC));
            // create a second one
            ActionUnit action2 = new ActionUnit();
            action2.setCreatedByInstitution(createdInstitution);
            action2.setPrimaryActionCode(codeOA2);
            action2.setIdentifier("C309_11");
            action2.setFullIdentifier("chartres-C309_11");
            action2.setName("Pôle Gare - rue du Chemin de Fer et rue du Faubourg Saint-Jean (phase 1)");
            action2.setAuthor(fouilleur1);
            action2.setType(prevFouille);
            action2.setBeginDate(OffsetDateTime.of(2015, 6, 8, 0, 0, 0, 0, ZoneOffset.UTC));
            action2.setEndDate(OffsetDateTime.of(2015, 9, 29, 0, 0, 0, 0, ZoneOffset.UTC));
            actionUnitRepository.save(action2);
        }


    }

    void initializeRecordings() {

        Concept us = conceptRepository
                .findConceptByExternalIdIgnoreCase(VOCABULARY_ID, "4282375")
                .orElseThrow(() -> new IllegalStateException("Concept us introuvable dans th240"));
        Concept depot = conceptRepository
                .findConceptByExternalIdIgnoreCase(VOCABULARY_ID, "4287539")
                .orElseThrow(() -> new IllegalStateException("Concept dépôt introuvable dans th240"));
        Concept coucheOcp = conceptRepository
                .findConceptByExternalIdIgnoreCase(VOCABULARY_ID, "4287541")
                .orElseThrow(() -> new IllegalStateException("Concept couche d'occupation introuvable dans th240"));
        Concept interfaceConcept = conceptRepository
                .findConceptByExternalIdIgnoreCase(VOCABULARY_ID, "4287537")
                .orElseThrow(() -> new IllegalStateException("Concept interface introuvable dans th240"));
        Concept creusement = conceptRepository
                .findConceptByExternalIdIgnoreCase(VOCABULARY_ID, "4287540")
                .orElseThrow(() -> new IllegalStateException("Concept creusement introuvable dans th240"));

        Optional<RecordingUnit> optRU = recordingUnitRepository.findByIdentifierAndCreatedByInstitution(1100, createdInstitution);
        if(optRU.isPresent()) {
            ru = optRU.get();
        }
        else {
            // create it
            ru = new RecordingUnit();
            ru.setCreatedByInstitution(createdInstitution);
            ru.setIdentifier(1100);
            ru.setFullIdentifier("chartres-C309_01-1100");
            ru.setAuthor(fouilleur1);
            ru.setExcavators(List.of(fouilleur1, fouilleur2));
            ru.setAuthors(List.of(fouilleur1));
            ru.setType(us);
            ru.setCreationTime(OffsetDateTime.of(2012, 6, 22, 0, 0, 0, 0, ZoneOffset.UTC));
            ru.setSecondaryType(depot);
            ru.setThirdType(coucheOcp);
            ru.setSpatialUnit(chartes);
            ru.setActionUnit(actionUnit);
            ru = recordingUnitRepository.save(ru);
            // create a second one
            RecordingUnit ru2 = new RecordingUnit();
            ru2.setCreatedByInstitution(createdInstitution);
            ru2.setIdentifier(1015);
            ru2.setFullIdentifier("chartres-C309_01-1015");
            ru2.setAuthor(fouilleur1);
            ru2.setCreationTime(OffsetDateTime.of(2012, 7, 5, 0, 0, 0, 0, ZoneOffset.UTC));
            ru2.setExcavators(List.of(fouilleur1));
            ru2.setAuthors(List.of(fouilleur1));
            ru2.setType(us);
            ru2.setSecondaryType(interfaceConcept);
            ru2.setThirdType(creusement);
            ru2.setSpatialUnit(chartes);
            ru2.setActionUnit(actionUnit);
            recordingUnitRepository.save(ru2);
        }


    }

    void initializeSpecimens() {

        Concept indiv = conceptRepository
                .findConceptByExternalIdIgnoreCase(VOCABULARY_ID, "4286252")
                .orElseThrow(() -> new IllegalStateException("Concept indiv introuvable dans th240"));
        Concept lot = conceptRepository
                .findConceptByExternalIdIgnoreCase(VOCABULARY_ID, "4286251")
                .orElseThrow(() -> new IllegalStateException("Concept lot d'occupation introuvable dans th240"));
        Concept animal = conceptRepository
                .findConceptByExternalIdIgnoreCase(VOCABULARY_ID, "4287542")
                .orElseThrow(() -> new IllegalStateException("Concept animal introuvable dans th240"));
        Concept metal = conceptRepository
                .findConceptByExternalIdIgnoreCase(VOCABULARY_ID, "4287543")
                .orElseThrow(() -> new IllegalStateException("Concept metal introuvable dans th240"));


        Optional<Specimen> optSpec = specimenRepository.findByFullIdentifierAndCreatedByInstitution("chartres-C309_01-1100-1", createdInstitution);
            if(optSpec.isEmpty()) {
                // create it
                Specimen s1 = new Specimen();
                s1.setCreatedByInstitution(createdInstitution);
                s1.setIdentifier(1);
                s1.setFullIdentifier("chartres-C309_01-1100-1");
                s1.setAuthor(fouilleur1);
                s1.setCollectors(List.of(fouilleur1));
                s1.setAuthors(List.of(fouilleur1));
                s1.setType(metal);
                s1.setCreationTime(OffsetDateTime.of(2012, 6, 22, 0, 0, 0, 0, ZoneOffset.UTC));
                s1.setRecordingUnit(ru);
                s1.setCategory(indiv);
                specimenRepository.save(s1);
                // create a second one
                Specimen s2 = new Specimen();
                s2.setCreatedByInstitution(createdInstitution);
                s2.setIdentifier(1);
                s2.setFullIdentifier("chartres-C309_01-1100-57");
                s2.setAuthor(fouilleur1);
                s2.setCollectors(List.of(fouilleur1));
                s2.setAuthors(List.of(fouilleur1));
                s2.setType(animal);
                s2.setCategory(lot);
                s2.setCreationTime(OffsetDateTime.of(2012, 6, 22, 0, 0, 0, 0, ZoneOffset.UTC));
                s2.setRecordingUnit(ru);
                specimenRepository.save(s2);
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
        for (Person adminLoop : admins) {
            if (isNotAskedAdmin(adminLoop)) {
                adminLoop.setSuperAdmin(false);
                personRepository.save(adminLoop);
            } else {
                adminWithUsername = adminLoop;
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
