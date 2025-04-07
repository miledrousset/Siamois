package fr.siamois.ui.bean.settings;

import fr.siamois.domain.models.Institution;
import fr.siamois.domain.models.UserInfo;
import fr.siamois.domain.services.InstitutionService;
import fr.siamois.domain.services.publisher.InstitutionChangeEventPublisher;
import fr.siamois.domain.utils.DateUtils;
import fr.siamois.ui.bean.SessionSettingsBean;
import jakarta.faces.component.UIComponent;
import jakarta.faces.component.UIInput;
import jakarta.faces.event.AjaxBehaviorEvent;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
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
    private List<Institution> institutions = null;
    private List<Institution> filteredInstitutions = null;
    private List<SortMeta> sortBy;
    private Map<Long, Boolean> toggleSwitchState = new HashMap<>();

    private String filterText;

    public InstitutionListSettingsBean(InstitutionService institutionService, SessionSettingsBean sessionSettingsBean, InstitutionChangeEventPublisher institutionChangeEventPublisher) {
        this.institutionService = institutionService;
        this.sessionSettingsBean = sessionSettingsBean;
        this.institutionChangeEventPublisher = institutionChangeEventPublisher;
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

}
