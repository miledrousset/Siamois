package fr.siamois.services.vocabulary;

import fr.siamois.infrastructure.api.ConceptApi;
import fr.siamois.infrastructure.api.dto.ConceptBranchDTO;
import fr.siamois.infrastructure.api.dto.FullConceptDTO;
import fr.siamois.infrastructure.api.dto.PurlInfoDTO;
import fr.siamois.infrastructure.repositories.FieldRepository;
import fr.siamois.infrastructure.repositories.vocabulary.ConceptRepository;
import fr.siamois.models.Institution;
import fr.siamois.models.UserInfo;
import fr.siamois.models.auth.Person;
import fr.siamois.models.exceptions.NoConfigForField;
import fr.siamois.models.exceptions.NotSiamoisThesaurusException;
import fr.siamois.models.vocabulary.Concept;
import fr.siamois.models.vocabulary.GlobalFieldConfig;
import fr.siamois.models.vocabulary.Vocabulary;
import fr.siamois.models.vocabulary.VocabularyType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FieldConfigurationServiceTest {

    @Mock private ConceptApi conceptApi;
    @Mock private FieldService fieldService;
    @Mock private FieldRepository fieldRepository;
    @Mock private ConceptRepository conceptRepository;
    @Mock private ConceptService conceptService;

    @InjectMocks
    private FieldConfigurationService service;

    private Vocabulary vocabulary;

    private UserInfo userInfo;

    @BeforeEach
    void beforeEach() {
        VocabularyType type = new VocabularyType();
        type.setId(-1L);
        type.setLabel("Thesaurus");

        vocabulary = new Vocabulary();
        vocabulary.setId(-12L);
        vocabulary.setType(type);
        vocabulary.setVocabularyName("Vocabulary name");
        vocabulary.setBaseUri("http://localhost");
        vocabulary.setLastLang("fr");

        userInfo = new UserInfo(new Institution(), new Person(), "fr");
        userInfo.getInstitution().setId(12L);
        userInfo.getUser().setId(12L);
    }

    private FullConceptDTO fullConceptDTO(String id, String code, String prefLabel) {
        FullConceptDTO conceptDTO = new FullConceptDTO();
        PurlInfoDTO identifier = new PurlInfoDTO();
        identifier.setValue(id);
        identifier.setType("string");
        conceptDTO.setIdentifier(new PurlInfoDTO[]{ identifier });
        PurlInfoDTO notation = new PurlInfoDTO();
        notation.setValue("SIAMOIS#" + code);
        notation.setType("string");
        conceptDTO.setNotation(new PurlInfoDTO[]{ notation });
        PurlInfoDTO label = new PurlInfoDTO();
        label.setValue(prefLabel);
        label.setType("string");
        label.setLang("fr");
        conceptDTO.setPrefLabel(new PurlInfoDTO[] { label });

         return conceptDTO;
    }

    private ConceptBranchDTO conceptBranchDTO() {
        ConceptBranchDTO dto = new ConceptBranchDTO();
        dto.addConceptBranchDTO("http://localhost/th223/1212", fullConceptDTO("1212", "SIATEST", "Test concept"));
        return dto;
    }

    @Test
    void setupFieldConfigurationForInstitution_shouldSave_whenNoConfigExist() throws NotSiamoisThesaurusException {
        ConceptBranchDTO dto = conceptBranchDTO();

        Concept parentConcept = new Concept();
        parentConcept.setId(-1L);
        parentConcept.setExternalId("1212");
        parentConcept.setVocabulary(vocabulary);
        parentConcept.setLangCode("fr");
        parentConcept.setLabel("Test concept");

        when(conceptApi.fetchFieldsBranch(vocabulary)).thenReturn(dto);
        when(fieldService.searchAllFieldCodes()).thenReturn(List.of("SIATEST"));
        when(conceptService.saveOrGetConceptFromFullDTO(any(UserInfo.class), any(Vocabulary.class), any(FullConceptDTO.class)))
                .thenReturn(parentConcept);
        when(fieldRepository.updateConfigForFieldOfInstitution(anyLong(), anyString(), anyLong())).thenReturn(0);

        Optional<GlobalFieldConfig> result = service.setupFieldConfigurationForInstitution(userInfo, vocabulary);

        assertThat(result).isEmpty();
        verify(fieldRepository, times(1)).saveConceptForFieldOfInstitution(anyLong(), anyString(), anyLong());
    }

    @Test
    void setupFieldConfigurationForInstitution_shouldUpdate_whenConfigExist() throws NotSiamoisThesaurusException {
        ConceptBranchDTO dto = conceptBranchDTO();

        Concept parentConcept = new Concept();
        parentConcept.setId(-1L);
        parentConcept.setExternalId("1212");
        parentConcept.setVocabulary(vocabulary);
        parentConcept.setLangCode("fr");
        parentConcept.setLabel("Test concept");

        when(conceptApi.fetchFieldsBranch(vocabulary)).thenReturn(dto);
        when(fieldService.searchAllFieldCodes()).thenReturn(List.of("SIATEST"));
        when(conceptService.saveOrGetConceptFromFullDTO(any(UserInfo.class), any(Vocabulary.class), any(FullConceptDTO.class)))
                .thenReturn(parentConcept);
        when(fieldRepository.updateConfigForFieldOfInstitution(anyLong(), anyString(), anyLong())).thenReturn(1);

        Optional<GlobalFieldConfig> result = service.setupFieldConfigurationForInstitution(userInfo, vocabulary);

        assertThat(result).isEmpty();
        verify(fieldRepository, never()).saveConceptForFieldOfInstitution(anyLong(), anyString(), anyLong());
    }

    @Test
    void setupFieldConfigurationForInstitution_shouldReturnWrongConfig_whenMandatoryCodeIsNotSet() throws NotSiamoisThesaurusException {
        ConceptBranchDTO dto = conceptBranchDTO();

        Concept parentConcept = new Concept();
        parentConcept.setId(-1L);
        parentConcept.setExternalId("1212");
        parentConcept.setVocabulary(vocabulary);
        parentConcept.setLangCode("fr");
        parentConcept.setLabel("Test concept");

        when(fieldService.searchAllFieldCodes()).thenReturn(List.of("SIATEST", "SIAMAND"));
        when(conceptApi.fetchFieldsBranch(vocabulary)).thenReturn(dto);

        Optional<GlobalFieldConfig> result = service.setupFieldConfigurationForInstitution(userInfo, vocabulary);

        assertThat(result).isPresent();
        verify(fieldRepository, never()).updateConfigForFieldOfInstitution(anyLong(), anyString(), anyLong());
        verify(fieldRepository, never()).saveConceptForFieldOfInstitution(anyLong(), anyString(), anyLong());
    }

    @Test
    void setupFieldConfigurationForUser_shouldSave_whenNoConfigExist() throws NotSiamoisThesaurusException {
        ConceptBranchDTO dto = conceptBranchDTO();

        Concept parentConcept = new Concept();
        parentConcept.setId(-1L);
        parentConcept.setExternalId("1212");
        parentConcept.setVocabulary(vocabulary);
        parentConcept.setLangCode("fr");
        parentConcept.setLabel("Test concept");

        when(conceptApi.fetchFieldsBranch(vocabulary)).thenReturn(dto);
        when(fieldService.searchAllFieldCodes()).thenReturn(List.of("SIATEST"));
        when(conceptService.saveOrGetConceptFromFullDTO(any(UserInfo.class), any(Vocabulary.class), any(FullConceptDTO.class)))
                .thenReturn(parentConcept);
        when(fieldRepository.updateConfigForFieldOfUser(anyLong(), anyLong(), anyString(), anyLong())).thenReturn(0);

        Optional<GlobalFieldConfig> result = service.setupFieldConfigurationForUser(userInfo, vocabulary);

        assertThat(result).isEmpty();
        verify(fieldRepository, times(1)).saveConceptForFieldOfUser(anyLong(), anyLong(), anyString(), anyLong());
    }

    @Test
    void setupFieldConfigurationForUser_shouldUpdate_whenConfigExist() throws NotSiamoisThesaurusException {
        ConceptBranchDTO dto = conceptBranchDTO();

        Concept parentConcept = new Concept();
        parentConcept.setId(-1L);
        parentConcept.setExternalId("1212");
        parentConcept.setVocabulary(vocabulary);
        parentConcept.setLangCode("fr");
        parentConcept.setLabel("Test concept");

        when(conceptApi.fetchFieldsBranch(vocabulary)).thenReturn(dto);
        when(fieldService.searchAllFieldCodes()).thenReturn(List.of("SIATEST"));
        when(conceptService.saveOrGetConceptFromFullDTO(any(UserInfo.class), any(Vocabulary.class), any(FullConceptDTO.class)))
                .thenReturn(parentConcept);
        when(fieldRepository.updateConfigForFieldOfUser(anyLong(), anyLong(),anyString(), anyLong())).thenReturn(1);

        Optional<GlobalFieldConfig> result = service.setupFieldConfigurationForUser(userInfo, vocabulary);

        assertThat(result).isEmpty();
        verify(fieldRepository, never()).saveConceptForFieldOfUser(anyLong(), anyLong(),anyString(), anyLong());
    }

    @Test
    void setupFieldConfigurationForUser_shouldReturnWrongConfig_whenMandatoryCodeIsNotSet() throws NotSiamoisThesaurusException {
        ConceptBranchDTO dto = conceptBranchDTO();

        Concept parentConcept = new Concept();
        parentConcept.setId(-1L);
        parentConcept.setExternalId("1212");
        parentConcept.setVocabulary(vocabulary);
        parentConcept.setLangCode("fr");
        parentConcept.setLabel("Test concept");

        when(fieldService.searchAllFieldCodes()).thenReturn(List.of("SIATEST", "SIAMAND"));
        when(conceptApi.fetchFieldsBranch(vocabulary)).thenReturn(dto);

        Optional<GlobalFieldConfig> result = service.setupFieldConfigurationForUser(userInfo, vocabulary);

        assertThat(result).isPresent();
        verify(fieldRepository, never()).updateConfigForFieldOfUser(anyLong(), anyLong(), anyString(), anyLong());
        verify(fieldRepository, never()).saveConceptForFieldOfUser(anyLong(), anyLong(), anyString(), anyLong());
    }

    @Test
    void findConfigurationForFieldCode_shouldThrow_whenNoConfigSet() {
        assertThrows(NoConfigForField.class, () -> service.findConfigurationForFieldCode(userInfo, "SIATEST"));
    }

    @Test
    void findConfigurationForFieldCode_shouldReturnUserConcept_whenUserConfig() throws NoConfigForField {
        Concept concept = new Concept();
        concept.setId(-1L);
        concept.setLabel("Parent config concept");
        concept.setVocabulary(vocabulary);
        concept.setExternalId("1212");
        concept.setLangCode("fr");

        when(conceptRepository.findTopTermConfigForFieldCodeOfUser(anyLong(), anyLong(), anyString()))
                .thenReturn(Optional.of(concept));

        Concept result = service.findConfigurationForFieldCode(userInfo, "SIATEST");

        assertThat(result).isEqualTo(concept);
    }

    @Test
    void findConfigurationForFieldCode_shouldReturnInstitConcept_whenNoUserConfig() throws NoConfigForField {
        Concept concept = new Concept();
        concept.setId(-1L);
        concept.setLabel("Parent config concept");
        concept.setVocabulary(vocabulary);
        concept.setExternalId("1212");
        concept.setLangCode("fr");

        when(conceptRepository.findTopTermConfigForFieldCodeOfInstitution(anyLong(), anyString()))
                .thenReturn(Optional.of(concept));

        Concept result = service.findConfigurationForFieldCode(userInfo, "SIATEST");

        assertThat(result).isEqualTo(concept);
    }

    @Test
    void fetchAutocomplete_shouldReturnExactTerm() throws NoConfigForField {
        ConceptBranchDTO dto = new ConceptBranchDTO();
        dto.addConceptBranchDTO("http://localhost/th223/1213", fullConceptDTO("1213", "", "First value"));
        dto.addConceptBranchDTO("http://localhost/th223/1213", fullConceptDTO("1214", "", "Second value"));
        dto.addConceptBranchDTO("http://localhost/th223/1214", fullConceptDTO("1215", "", "Third value"));

        when(conceptRepository.findTopTermConfigForFieldCodeOfInstitution(anyLong(), eq("SIATEST")))
                .thenReturn(Optional.of(new Concept()));
        when(conceptApi.fetchConceptsUnderTopTerm(any(Concept.class))).thenReturn(dto);

        List<Concept> result = service.fetchAutocomplete(userInfo, "SIATEST", "ue");

        assertThat(result).isNotEmpty().allMatch(val -> val.getLabel().contains("value"));
    }

    @Test
    void fetchAutocomplete_shouldReturnCloseTeams_whenNoExactTerms() throws NoConfigForField {
        ConceptBranchDTO dto = new ConceptBranchDTO();
        dto.addConceptBranchDTO("http://localhost/th223/1213", fullConceptDTO("1213", "", "Sites"));
        dto.addConceptBranchDTO("http://localhost/th223/1213", fullConceptDTO("1214", "", "Zone"));
        dto.addConceptBranchDTO("http://localhost/th223/1216", fullConceptDTO("1216", "", "Chantier"));

        when(conceptRepository.findTopTermConfigForFieldCodeOfInstitution(anyLong(), eq("SIATEST")))
                .thenReturn(Optional.of(new Concept()));
        when(conceptApi.fetchConceptsUnderTopTerm(any(Concept.class))).thenReturn(dto);

        List<Concept> result = service.fetchAutocomplete(userInfo, "SIATEST", "Sie");

        assertThat(result).isNotEmpty().allMatch(match -> match.getLabel().equals("Sites"));
    }

    @Test
    void fetchAllValues() {
        ConceptBranchDTO dto = new ConceptBranchDTO();
        dto.addConceptBranchDTO("http://localhost/th223/1213", fullConceptDTO("1213", "", "First value"));
        dto.addConceptBranchDTO("http://localhost/th223/1214", fullConceptDTO("1214", "", "Second value"));
        dto.addConceptBranchDTO("http://localhost/th223/1215", fullConceptDTO("1215", "", "Third value"));

        Concept parentConcept = new Concept();
        parentConcept.setId(-1L);
        parentConcept.setExternalId("1212");
        parentConcept.setVocabulary(vocabulary);
        parentConcept.setLangCode("fr");
        parentConcept.setLabel("Parent concept");

        when(conceptApi.fetchConceptsUnderTopTerm(parentConcept)).thenReturn(dto);

        List<Concept> result = service.fetchAllValues(userInfo, parentConcept);

        assertThat(result).hasSize(3);
        assertThat(result).extracting(Concept::getLabel).containsExactlyInAnyOrder("First value", "Second value", "Third value");
    }
}