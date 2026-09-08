package fr.siamois.domain.services.actionunit;

import fr.siamois.domain.models.settings.tableconfig.ConfigurableTable;
import fr.siamois.domain.models.settings.tableconfig.TypeFormConfig;
import fr.siamois.domain.models.vocabulary.Concept;
import fr.siamois.domain.models.vocabulary.Vocabulary;
import fr.siamois.domain.services.settings.tableconfig.TableFieldConfigService;
import fr.siamois.domain.services.vocabulary.ConceptService;
import fr.siamois.domain.services.vocabulary.VocabularyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Seeds every newly created project with a few identifier configurations, so it never starts
 * with only the built-in defaults:
 * <ul>
 *   <li>two recording-unit types, "US" (Unité d'enregistrement) and "F" (Unité incluante) —
 *       their concepts are downloaded from the thesaurus and persisted locally if not already
 *       there;</li>
 *   <li>the {@code _default} identifier format of the Specimen/Mobilier table.</li>
 * </ul>
 * A step whose thesaurus is unreachable, or whose project has no vocabulary configured yet for
 * the relevant field, is skipped rather than failing project creation.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultProjectIdentifierConfigSeeder {

    private static final int MIN_CODE = 1;
    private static final int MAX_CODE = 999;
    private static final String MOBILIER_DEFAULT_IDENTIFIER_FORMAT = "MOB{NUM_MOBILIER:0}";

    private record DefaultType(String uri, String identifierFormat) {
    }

    private static final List<DefaultType> DEFAULT_RECORDING_UNIT_TYPES = List.of(
            new DefaultType("https://opentheso2.mom.fr/?idc=4287627&idt=th1295", "US{NUM_UE:00}"),
            new DefaultType("https://opentheso2.mom.fr/?idc=4287628&idt=th1295", "F{NUM_UE:00}")
    );

    private final VocabularyService vocabularyService;
    private final ConceptService conceptService;
    private final TableFieldConfigService tableFieldConfigService;

    public void seed(Long projectId) {
        seedRecordingUnitTypes(projectId);
        seedMobilierDefault(projectId);
    }

    private void seedRecordingUnitTypes(Long projectId) {
        for (DefaultType type : DEFAULT_RECORDING_UNIT_TYPES) {
            try {
                Vocabulary vocabulary = vocabularyService.findOrCreateVocabularyOfUri(type.uri());
                Concept concept = conceptService.saveOrGetConceptFromUri(vocabulary, type.uri(), null);
                TypeFormConfig config = TypeFormConfig.builder()
                        .identifierFormat(type.identifierFormat())
                        .minCode(MIN_CODE)
                        .maxCode(MAX_CODE)
                        .build();
                tableFieldConfigService.saveFormConfig(projectId, ConfigurableTable.UE, concept.getId(), config);
            } catch (Exception e) {
                log.warn("Could not seed default recording-unit type from {} for project {}",
                        type.uri(), projectId, e);
            }
        }
    }

    private void seedMobilierDefault(Long projectId) {
        try {
            TypeFormConfig config = TypeFormConfig.builder()
                    .typeName(TableFieldConfigService.DEFAULT_TYPE)
                    .identifierFormat(MOBILIER_DEFAULT_IDENTIFIER_FORMAT)
                    .minCode(MIN_CODE)
                    .maxCode(MAX_CODE)
                    .build();
            tableFieldConfigService.saveFormConfig(projectId, ConfigurableTable.MOBILIER, config);
        } catch (Exception e) {
            log.warn("Could not seed the default Mobilier identifier format for project {}", projectId, e);
        }
    }
}
