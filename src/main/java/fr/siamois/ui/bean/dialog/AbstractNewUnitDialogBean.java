package fr.siamois.ui.bean.dialog;



import fr.siamois.domain.models.TraceableEntity;
import fr.siamois.domain.models.exceptions.EntityAlreadyExistsException;
import fr.siamois.domain.models.form.customfield.CustomField;
import fr.siamois.domain.services.spatialunit.SpatialUnitTreeService;
import fr.siamois.domain.services.vocabulary.FieldConfigurationService;
import fr.siamois.ui.bean.LangBean;
import fr.siamois.ui.bean.RedirectBean;
import fr.siamois.ui.bean.SessionSettingsBean;
import fr.siamois.ui.bean.panel.FlowBean;
import fr.siamois.ui.bean.panel.models.panel.single.AbstractSingleEntity;
import fr.siamois.ui.lazydatamodel.BaseLazyDataModel;
import fr.siamois.utils.MessageUtils;
import jakarta.faces.component.UIComponent;
import jakarta.faces.event.AjaxBehaviorEvent;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.primefaces.PrimeFaces;
import java.util.LinkedHashSet;
import java.util.Set;

@Slf4j
@EqualsAndHashCode(callSuper = true)
public abstract class AbstractNewUnitDialogBean<T extends TraceableEntity> extends AbstractSingleEntity<T> {

    protected BaseLazyDataModel<T> lazyDataModel;
    protected transient Set<T> setToUpdate;

    protected final LangBean langBean;
    protected final FlowBean flowBean;
    protected final RedirectBean redirectBean;

    protected static final String UPDATE_FAILED_MESSAGE_CODE = "common.entity.spatialUnits.updateFailed";
    protected static final String ENTITY_ALREADY_EXIST_MESSAGE_CODE = "common.entity.alreadyExist";

    protected AbstractNewUnitDialogBean(LangBean langBean,
                                        RedirectBean redirectBean,
                                        FlowBean flowBean,
                                        AbstractSingleEntity.Deps deps) {
        super(deps);
        this.langBean = langBean;
        this.flowBean = flowBean;
        this.redirectBean = redirectBean;
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
        performCreate(true, true); // open panel + scroll to top
    }

    public void create() {
        performCreate(false, false); // pas d'ouverture, pas de scroll
    }

    private void performCreate(boolean openAfter, boolean scrollToTop) {
        try {
            createUnit();
        } catch (EntityAlreadyExistsException e) {
            log.error(e.getMessage(), e);
            MessageUtils.displayErrorMessage(langBean, ENTITY_ALREADY_EXIST_MESSAGE_CODE, unitName());
            return;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            MessageUtils.displayErrorMessage(langBean, UPDATE_FAILED_MESSAGE_CODE, unitName());
            return;
        }

        // JS conditionnel
        StringBuilder js = new StringBuilder("PF('")
                .append(getDialogWidgetVar())
                .append("').hide();");
        if (scrollToTop) {
            js.append("handleScrollToTop();");
        }
        PrimeFaces.current().executeScript(js.toString());

        // Refresh commun
        PrimeFaces.current().ajax().update("flow");

        // Message succès
        MessageUtils.displayInfoMessage(langBean, getSuccessMessageCode(), unitName());

        // Actions spécifiques
        if (openAfter) {
            openPanel(getUnitId());
            //redirectBean.redirectTo("");
        }

        // Commun
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
