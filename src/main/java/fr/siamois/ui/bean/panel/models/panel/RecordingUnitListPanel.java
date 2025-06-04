package fr.siamois.ui.bean.panel.models.panel;


import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.exceptions.recordingunit.FailedRecordingUnitSaveException;
import fr.siamois.domain.models.recordingunit.RecordingUnit;
import fr.siamois.domain.services.BookmarkService;
import fr.siamois.domain.services.SpatialUnitService;
import fr.siamois.domain.services.actionunit.ActionUnitService;
import fr.siamois.domain.services.person.PersonService;
import fr.siamois.domain.services.recordingunit.RecordingUnitService;
import fr.siamois.domain.services.vocabulary.ConceptService;
import fr.siamois.domain.services.vocabulary.LabelService;
import fr.siamois.domain.utils.MessageUtils;
import fr.siamois.ui.bean.LangBean;
import fr.siamois.ui.bean.NavBean;
import fr.siamois.ui.bean.SessionSettingsBean;
import fr.siamois.ui.bean.panel.models.PanelBreadcrumb;
import fr.siamois.ui.lazydatamodel.BaseLazyDataModel;
import fr.siamois.ui.lazydatamodel.RecordingUnitLazyDataModel;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.event.RowEditEvent;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class RecordingUnitListPanel extends AbstractListPanel<RecordingUnit> {

    private final transient RecordingUnitService recordingUnitService;
    private final transient NavBean navBean;

    // locals
    private String actionUnitListErrorMessage;


    @Override
    protected long countUnitsByInstitution() {
        return actionUnitService.countByInstitution(sessionSettingsBean.getSelectedInstitution());
    }

    @Override
    protected BaseLazyDataModel<RecordingUnit> createLazyDataModel() {
        return new RecordingUnitLazyDataModel(recordingUnitService, sessionSettingsBean, langBean);
    }

    @Override
    protected void setErrorMessage(String msg) {
        this.errorMessage = msg;
    }

    private static final String RECORDING_UNIT_BASE_URI = "/recording-unit";


    public RecordingUnitListPanel(SpatialUnitService spatialUnitService, PersonService personService,
                                  ConceptService conceptService,
                                  SessionSettingsBean sessionSettingsBean,
                                  LangBean langBean,
                                  LabelService labelService,
                                  ActionUnitService actionUnitService,
                                  RecordingUnitService recordingUnitService, BookmarkService bookmarkService, NavBean navBean) {



        super("panel.title.allrecordingunit",
                "bi bi-pencil-square",
                "siamois-panel recording-unit-panel recording-unit-list-panel",
                spatialUnitService,
                personService,
                conceptService,
                sessionSettingsBean,
                langBean,
                labelService,
                actionUnitService,
                bookmarkService);
        this.recordingUnitService = recordingUnitService;
        this.navBean = navBean;
    }

    @Override
    public String displayHeader() {
        return "/panel/header/recordingUnitListPanelHeader.xhtml";
    }


    @Override
    protected String getBreadcrumbKey() {
        return "common.entity.recordingUnits";
    }

    @Override
    protected String getBreadcrumbIcon() {
        return "bi bi-pencil-square";
    }

    public void bookmarkRecordingUnit(String fullIdentifier) {

        // Maybe check that ressource exists and user has access to it?
        bookmarkService.save(
                sessionSettingsBean.getUserInfo(),
                RECORDING_UNIT_BASE_URI+fullIdentifier,
                fullIdentifier
        );
        MessageUtils.displayInfoMessage(langBean, "common.bookmark.saved");
    }

    public void unBookmarkRecordingUnit(String fullIdentifier) {
        bookmarkService.deleteBookmark(
                sessionSettingsBean.getUserInfo(),
                RECORDING_UNIT_BASE_URI+fullIdentifier
        );
        MessageUtils.displayInfoMessage(langBean, "common.bookmark.unsaved");
    }

    public void toggleBookmark(String fullIdentifier) {
        if(Boolean.TRUE.equals(isRessourceBookmarkedByUser(RECORDING_UNIT_BASE_URI+fullIdentifier))) {
            unBookmarkRecordingUnit(fullIdentifier);
        }
        else {
            bookmarkRecordingUnit(fullIdentifier);
        }
        navBean.reloadBookarkedPanels();
    }

    public Boolean isRecordingUnitBookmarkedByUser(String fullIdentifier) {
        return isRessourceBookmarkedByUser(RECORDING_UNIT_BASE_URI+fullIdentifier);
    }

    public List<Person> authorsAvailable() {

        return personService.findAllAuthorsOfActionUnitByInstitution(sessionSettingsBean.getSelectedInstitution());

    }

    @Override
    public void init() {
        selectedUnits = new ArrayList<>();
        super.init();
    }

    @Override
    public String display() {
        return "/panel/recordingUnitListPanel.xhtml";
    }

    @Override
    public String ressourceUri() {
        return "/recordingUnit";
    }

    public void handleRowEdit(RowEditEvent<RecordingUnit> event) {

        RecordingUnit toSave = event.getObject();

        try {
            recordingUnitService.save(toSave, toSave.getType(), List.of(),  List.of(),  List.of());
        }
        catch(FailedRecordingUnitSaveException e) {
            MessageUtils.displayErrorMessage(langBean, "common.entity.recordingUnits.updateFailed", toSave.getFullIdentifier());
            return ;
        }

        MessageUtils.displayInfoMessage(langBean, "common.entity.recordingUnits.updated", toSave.getFullIdentifier());
    }



    public static class RecordingUnitListPanelBuilder {

        private final RecordingUnitListPanel recordingUnitListPanel;

        public RecordingUnitListPanelBuilder(ObjectProvider<RecordingUnitListPanel> actionUnitListPanelProvider) {
            this.recordingUnitListPanel = actionUnitListPanelProvider.getObject();
        }

        public RecordingUnitListPanel.RecordingUnitListPanelBuilder breadcrumb(PanelBreadcrumb breadcrumb) {
            recordingUnitListPanel.setBreadcrumb(breadcrumb);

            return this;
        }

        public RecordingUnitListPanel build() {
            recordingUnitListPanel.init();
            return recordingUnitListPanel;
        }
    }





}
