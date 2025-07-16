package fr.siamois.ui.bean.dialog;



import fr.siamois.domain.models.TraceableEntity;
import fr.siamois.domain.models.form.customfield.CustomField;
import fr.siamois.domain.services.spatialunit.SpatialUnitTreeService;
import fr.siamois.domain.services.vocabulary.FieldConfigurationService;
import fr.siamois.ui.bean.LangBean;
import fr.siamois.ui.bean.SessionSettingsBean;
import fr.siamois.ui.bean.panel.FlowBean;
import fr.siamois.ui.bean.panel.models.panel.single.AbstractSingleEntity;
import fr.siamois.ui.lazydatamodel.BaseLazyDataModel;
import fr.siamois.utils.MessageUtils;
import jakarta.faces.component.UIComponent;
import jakarta.faces.event.AjaxBehaviorEvent;
import lombok.extern.slf4j.Slf4j;
import org.primefaces.PrimeFaces;
import java.util.LinkedHashSet;
import java.util.Set;

@Slf4j
public abstract class AbstractNewUnitDialogBean<T extends TraceableEntity> extends AbstractSingleEntity<T> {

    protected BaseLazyDataModel<T> lazyDataModel;
    protected transient Set<T> setToUpdate;

    protected final LangBean langBean;
    protected final FlowBean flowBean;

    protected static final String UPDATE_FAILED_MESSAGE_CODE = "common.entity.spatialUnits.updateFailed";

    protected AbstractNewUnitDialogBean(SessionSettingsBean sessionSettingsBean,
                                        FieldConfigurationService fieldConfigurationService,
                                        SpatialUnitTreeService spatialUnitTreeService,
                                        LangBean langBean,
                                        FlowBean flowBean) {
        super(sessionSettingsBean, fieldConfigurationService, spatialUnitTreeService);
        this.langBean = langBean;
        this.flowBean = flowBean;
    }

    protected abstract void persistUnit() throws Exception;

    protected abstract String getDialogWidgetVar();

    protected abstract String getSuccessMessageCode();

    protected abstract void openPanel(Long unitId);

    protected abstract void createEmptyUnit();

    @Override
    public void setFieldConceptAnswerHasBeenModified(AjaxBehaviorEvent event) {
        UIComponent component = event.getComponent();
        CustomField field = (CustomField) component.getAttributes().get("field");

        formResponse.getAnswers().get(field).setHasBeenModified(true);
    }

    protected void reset() {
        unit = null;
        formResponse = null;
        lazyDataModel = null;
        setToUpdate = null;
    }

    public void init() {
        reset();
        createEmptyUnit();
        unit.setAuthor(sessionSettingsBean.getAuthenticatedUser());
        unit.setCreatedByInstitution(sessionSettingsBean.getSelectedInstitution());
        initForms();
    }

    public void createAndOpen() {
        try {
            createUnit();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            MessageUtils.displayErrorMessage(langBean, UPDATE_FAILED_MESSAGE_CODE, unitName());
            return;
        }
        PrimeFaces.current().executeScript("PF('" + getDialogWidgetVar() + "').hide();handleScrollToTop();");
        MessageUtils.displayInfoMessage(langBean, getSuccessMessageCode(), unitName());
        openPanel(getUnitId());
        flowBean.updateHomePanel();
    }

    public void create() {
        try {
            createUnit();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            MessageUtils.displayErrorMessage(langBean, UPDATE_FAILED_MESSAGE_CODE, unitName());
            return;
        }
        PrimeFaces.current().executeScript("PF('" + getDialogWidgetVar() + "').hide();");
        MessageUtils.displayInfoMessage(langBean, getSuccessMessageCode(), unitName());
        flowBean.updateHomePanel();
    }

    protected void createUnit() throws Exception {
        updateJpaEntityFromFormResponse(formResponse, unit);
        unit.setValidated(false);
        persistUnit();
        updateCollections();
    }

    private void updateCollections() {

        if (lazyDataModel != null) {
            lazyDataModel.addRowToModel(unit);
        }
        if (setToUpdate != null) {
            // TODO : Dans certains cas, ce n'est pas la bonne liste, par exemple si je clique sur créée depuis la colonne "actions" du tableau d'unité spatiale
            // Mais que dans le formulaire je decide de changer l'unité spatiale choisi par défaut par une autre, ce ne sera pas la bonne ligne
            LinkedHashSet<T> newSet = new LinkedHashSet<>();
            newSet.add(unit);
            newSet.addAll(setToUpdate);
            setToUpdate.clear();
            setToUpdate.addAll(newSet);
        }
    }

    protected abstract String unitName();

    protected abstract Long getUnitId();
}
