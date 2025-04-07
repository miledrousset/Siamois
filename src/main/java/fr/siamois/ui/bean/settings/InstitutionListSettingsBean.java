package fr.siamois.ui.bean.settings;

import fr.siamois.domain.models.Institution;
import fr.siamois.domain.models.UserInfo;
import fr.siamois.domain.models.exceptions.institution.FailedInstitutionSaveException;
import fr.siamois.domain.models.exceptions.institution.InstitutionAlreadyExistException;
import fr.siamois.domain.services.InstitutionService;
import fr.siamois.domain.services.publisher.InstitutionChangeEventPublisher;
import fr.siamois.domain.utils.DateUtils;
import fr.siamois.ui.bean.SessionSettingsBean;
import fr.siamois.ui.bean.dialog.institution.InstitutionCreationBean;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.primefaces.PrimeFaces;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.*;

@Slf4j
@Getter
@Setter
@Component
@SessionScope
public class InstitutionListSettingsBean implements Serializable {

    private final transient InstitutionService institutionService;
    private final SessionSettingsBean sessionSettingsBean;
    private final transient InstitutionChangeEventPublisher institutionChangeEventPublisher;
    private final InstitutionCreationBean institutionCreationBean;
    private List<Institution> institutions = null;
    private List<Institution> filteredInstitutions = null;
    private List<SortMeta> sortBy;
    private Map<Long, Boolean> toggleSwitchState = new HashMap<>();

    private String filterText;

    public InstitutionListSettingsBean(InstitutionService institutionService,
                                       SessionSettingsBean sessionSettingsBean,
                                       InstitutionChangeEventPublisher institutionChangeEventPublisher,
                                       InstitutionCreationBean institutionCreationBean) {
        this.institutionService = institutionService;
        this.sessionSettingsBean = sessionSettingsBean;
        this.institutionChangeEventPublisher = institutionChangeEventPublisher;
        this.institutionCreationBean = institutionCreationBean;
    }

    public void init() {
        if (institutions == null) {
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
        institutionCreationBean.reset();
        institutionCreationBean.setSaveActionFromBean(this::createInstitution);
        PrimeFaces.current().executeScript("PF('newInstitutionDialog').show();");
    }

    public void createInstitution() {
        Institution institution = null;
        try {
            institution = institutionCreationBean.createInstitution();
        } catch (InstitutionAlreadyExistException e) {
            log.error("Institution already exists");
            return;
        } catch (FailedInstitutionSaveException e) {
            log.error("Failed to create institution", e);
            return;
        }
        if (institution != null) {
            institutions.add(institution);
            filteredInstitutions.add(institution);
            toggleSwitchState.put(institution.getId(), false);
            institutionCreationBean.reset();
            PrimeFaces.current().executeScript("PF('newInstitutionDialog').hide();");
        }
    }

}
