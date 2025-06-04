package fr.siamois.ui.bean.panel.models.panel;


import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.recordingunit.RecordingUnit;
import fr.siamois.domain.services.SpatialUnitService;
import fr.siamois.domain.services.actionunit.ActionUnitService;
import fr.siamois.domain.services.person.PersonService;
import fr.siamois.domain.services.recordingunit.RecordingUnitService;
import fr.siamois.domain.services.vocabulary.ConceptService;
import fr.siamois.domain.services.vocabulary.LabelService;
import fr.siamois.ui.bean.LangBean;
import fr.siamois.ui.bean.SessionSettingsBean;
import fr.siamois.ui.bean.panel.models.PanelBreadcrumb;
import fr.siamois.ui.lazydatamodel.BaseLazyDataModel;
import fr.siamois.ui.lazydatamodel.RecordingUnitLazyDataModel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;


@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class RecordingUnitListPanel extends AbstractListPanel<RecordingUnit> {

    private final transient RecordingUnitService recordingUnitService;
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


    public RecordingUnitListPanel(SpatialUnitService spatialUnitService, PersonService personService,
                                  ConceptService conceptService,
                                  SessionSettingsBean sessionSettingsBean,
                                  LangBean langBean,
                                  LabelService labelService,
                                  ActionUnitService actionUnitService, RecordingUnitService recordingUnitService) {



        super("panel.title.allrecordingunit",
                "bi bi-pencil-square",
                "siamois-panel recording-unit-panel recording-unit-list-panel",
                spatialUnitService, personService, conceptService, sessionSettingsBean, langBean, labelService, actionUnitService);
        this.recordingUnitService = recordingUnitService;
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



    public List<Person> authorsAvailable() {

        return personService.findAllAuthorsOfActionUnitByInstitution(sessionSettingsBean.getSelectedInstitution());

    }

    @Override
    public String display() {
        return "/panel/recordingUnitListPanel.xhtml";
    }

    @Override
    public String ressourceUri() {
        return "/recordingUnit";
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
