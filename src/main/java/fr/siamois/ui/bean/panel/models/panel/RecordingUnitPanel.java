package fr.siamois.ui.bean.panel.models.panel;

import fr.siamois.domain.models.exceptions.recordingunit.RecordingUnitNotFoundException;
import fr.siamois.domain.services.SpatialUnitService;
import fr.siamois.domain.services.actionunit.ActionUnitService;
import fr.siamois.domain.services.person.PersonService;
import fr.siamois.domain.services.recordingunit.RecordingUnitService;
import fr.siamois.domain.services.vocabulary.ConceptService;
import fr.siamois.domain.services.vocabulary.FieldConfigurationService;
import fr.siamois.ui.bean.LangBean;
import fr.siamois.ui.bean.SessionSettingsBean;
import fr.siamois.ui.bean.panel.FlowBean;
import fr.siamois.ui.bean.panel.models.PanelBreadcrumb;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.primefaces.model.menu.DefaultMenuItem;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class RecordingUnitPanel extends RecordingUnitPanelBase {


    // ------- Locals
    private String recordingUnitErrorMessage;
    private Long recordingUnitId;


    public RecordingUnitPanel(LangBean langBean, SessionSettingsBean sessionSettingsBean, SpatialUnitService spatialUnitService,
                                 ActionUnitService actionUnitService, RecordingUnitService recordingUnitService,
                                 PersonService personService, ConceptService conceptService,
                                 FieldConfigurationService fieldConfigurationService, FlowBean flowBean) {
        super(
                langBean,
                sessionSettingsBean,
                spatialUnitService,
                actionUnitService,
                recordingUnitService,
                personService,
                conceptService,
                fieldConfigurationService,
                flowBean,
                "Unité d'enregistrement",
                "bi bi-pencil-square",
                "siamois-panel recording-unit-panel recording-unit-single-panel");
    }

    @Override
    public String display() {
        return "/panel/recordingUnitPanel.xhtml";
    }



    void init() {

        try {


                this.recordingUnit = this.recordingUnitService.findById(recordingUnitId);
                if (this.recordingUnit.getStartDate() != null) {
                    this.startDate = offsetDateTimeToLocalDate(this.recordingUnit.getStartDate());
                }
                if (this.recordingUnit.getEndDate() != null) {
                    this.endDate = offsetDateTimeToLocalDate(this.recordingUnit.getEndDate());
                }




        } catch (RecordingUnitNotFoundException e) {
            recordingUnitErrorMessage = "Unable to get recording unit";
            log.error("Recording unit with ID {} not found", recordingUnitId);

        } catch (RuntimeException err) {
            recordingUnitErrorMessage = "Unable to get recording unit";

        }

        DefaultMenuItem item = DefaultMenuItem.builder()
                .value(recordingUnit.getFullIdentifier())
                .icon("bi bi-arrow-down-square")
                .build();
        this.getBreadcrumb().getModel().getElements().add(item);


    }



    public static class RecordingUnitPanelBuilder {

        private final RecordingUnitPanel recordingUnitPanel;

        public RecordingUnitPanelBuilder(ObjectProvider<RecordingUnitPanel> recordingUnitPanelProvider) {
            this.recordingUnitPanel = recordingUnitPanelProvider.getObject();
        }

        public RecordingUnitPanel.RecordingUnitPanelBuilder breadcrumb(PanelBreadcrumb breadcrumb) {
            recordingUnitPanel.setBreadcrumb(breadcrumb);

            return this;
        }


        public RecordingUnitPanel.RecordingUnitPanelBuilder id(Long id) {
            recordingUnitPanel.setRecordingUnitId(id);

            return this;
        }

        public RecordingUnitPanel build() {
            recordingUnitPanel.init();
            return recordingUnitPanel;
        }
    }
}
