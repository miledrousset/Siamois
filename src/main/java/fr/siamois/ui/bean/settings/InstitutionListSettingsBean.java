package fr.siamois.ui.bean.settings;

import fr.siamois.domain.models.Institution;
import fr.siamois.domain.models.UserInfo;
import fr.siamois.domain.models.exceptions.institution.FailedInstitutionSaveException;
import fr.siamois.domain.models.exceptions.institution.InstitutionAlreadyExistException;
import fr.siamois.domain.services.InstitutionService;
import fr.siamois.domain.services.publisher.InstitutionChangeEventPublisher;
import fr.siamois.domain.services.recordingunit.RecordingUnitService;
import fr.siamois.domain.utils.DateUtils;
import fr.siamois.domain.utils.MessageUtils;
import fr.siamois.ui.bean.LangBean;
import fr.siamois.ui.bean.SessionSettingsBean;
import fr.siamois.ui.bean.dialog.institution.InstitutionDialogBean;
import jakarta.faces.application.FacesMessage;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.primefaces.PrimeFaces;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;
import org.springframework.stereotype.Component;

import javax.faces.bean.SessionScoped;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Getter
@Setter
@Component
@SessionScoped
public class InstitutionListSettingsBean implements Serializable {

    private final transient InstitutionService institutionService;
    private final SessionSettingsBean sessionSettingsBean;
    private final transient InstitutionChangeEventPublisher institutionChangeEventPublisher;
    private final InstitutionDialogBean institutionDialogBean;
    private final transient RecordingUnitService recordingUnitService;
    private final InstitutionDetailsBean institutionDetailsBean;
    private final LangBean langBean;
    private List<Institution> institutions = null;
    private List<Institution> filteredInstitutions = null;
    private List<SortMeta> sortBy;
    private Map<Long, Boolean> toggleSwitchState = new HashMap<>();

    private String filterText;

    public InstitutionListSettingsBean(InstitutionService institutionService,
                                       SessionSettingsBean sessionSettingsBean,
                                       InstitutionChangeEventPublisher institutionChangeEventPublisher,
                                       InstitutionDialogBean institutionDialogBean, RecordingUnitService recordingUnitService, InstitutionDetailsBean institutionDetailsBean, LangBean langBean) {
        this.institutionService = institutionService;
        this.sessionSettingsBean = sessionSettingsBean;
        this.institutionChangeEventPublisher = institutionChangeEventPublisher;
        this.institutionDialogBean = institutionDialogBean;
        this.recordingUnitService = recordingUnitService;
        this.institutionDetailsBean = institutionDetailsBean;
        this.langBean = langBean;
    }

    public void init() {

            UserInfo info = sessionSettingsBean.getUserInfo();
            institutions = institutionService.findInstitutionsOfPerson(info.getUser());
            onFilterType();
            updateTogglesState();
            sortBy = new ArrayList<>();
            sortBy.add(SortMeta.builder()
                    .field("active")
                    .order(SortOrder.ASCENDING)
                    .priority(1)
                    .build());

    }

    public String displayDate(OffsetDateTime date) {
        return DateUtils.formatOffsetDateTime(date);
    }

    public void onFilterType() {
        if (filterText != null && !filterText.isEmpty() && filterText.length() > 2) {
            filteredInstitutions = institutions.stream()
                    .filter(institution -> institution.getName().toLowerCase().contains(filterText.toLowerCase()))
                    .toList();
        } else {
            filteredInstitutions = new ArrayList<>(institutions);
        }
    }

    public void changeCurrentInstitution(Institution institution) {
        log.trace("Change current institution received : {}", institution);
        sessionSettingsBean.setSelectedInstitution(institution);
        institutionChangeEventPublisher.publishInstitutionChangeEvent();
        updateTogglesState();
    }

    private void updateTogglesState() {
        Institution selected = sessionSettingsBean.getSelectedInstitution();
        for (Institution institution : institutions) {
            boolean isSelected = institution.getId().equals(selected.getId());
            toggleSwitchState.put(institution.getId(), isSelected);
        }
    }

    public boolean userIsSuperadmin() {
        return sessionSettingsBean.getAuthenticatedUser().isSuperAdmin();
    }

    public boolean hasMoreThenOneInstitution() {
        return institutions.size() > 1;
    }

    public void displayCreateDialog() {
        log.trace("Display create institution dialog");
        institutionDialogBean.reset();
        institutionDialogBean.setTitle("Créer une organisation");
        institutionDialogBean.setButtonLabel("Créer l'organisation");
        institutionDialogBean.setActionFromBean(this::createInstitution);
        PrimeFaces.current().ajax().update("newInstitutionDialog");
        PrimeFaces.current().executeScript("PF('newInstitutionDialog').show();");
    }

    public void createInstitution() {
        Institution institution;

        try {
            institution = institutionDialogBean.createInstitution();
            MessageUtils.displayPlainMessage(langBean, FacesMessage.SEVERITY_INFO, "L'institution %s a bien été crée", institution.getName());
        } catch (InstitutionAlreadyExistException e) {
            log.error("Institution already exists");
            MessageUtils.displayPlainMessage(langBean, FacesMessage.SEVERITY_ERROR, "Une institution avec ce nom ou cet identifiant existe déjà.");
            return;
        } catch (FailedInstitutionSaveException e) {
            log.error("Failed to create institution", e);
            MessageUtils.displayPlainMessage(langBean, FacesMessage.SEVERITY_ERROR, "Erreur interne lors de la création de l'institution.");
            return;
        }

        institutions.add(institution);
        filteredInstitutions.add(institution);
        toggleSwitchState.put(institution.getId(), false);
        institutionDialogBean.reset();
        PrimeFaces.current().executeScript("PF('newInstitutionDialog').hide();");
    }

    public long numberOfMemberInInstitution(Institution institution) {
        return institutionService.countMembersInInstitution(institution);
    }

    public long numberOfRecordingUnitInInstitution(Institution institution) {
        return recordingUnitService.countByInstitution(institution);
    }

    public String redirectToInstitution(Institution institution) {
        institutionDetailsBean.setInstitution(institution);
        institutionDetailsBean.addInstitutionManagementElements();
        return "/pages/settings/institutionSettings.xhtml?faces-redirect=true";
    }

}
