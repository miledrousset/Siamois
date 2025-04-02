package fr.siamois.ui.bean.panel.models.panel;


import fr.siamois.ui.bean.SessionSettingsBean;
import fr.siamois.ui.bean.panel.models.PanelBreadcrumb;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;



@EqualsAndHashCode(callSuper = true)
@Data
@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class WelcomePanel extends AbstractPanel {

    private final SessionSettingsBean sessionSettingsBean;

    private String spatialUnitListErrorMessage;


    public WelcomePanel(SessionSettingsBean sessionSettingsBean) {
        super("Accueil", "bi bi-house", "siamois-panel");
        this.sessionSettingsBean = sessionSettingsBean;
        this.setBreadcrumb(new PanelBreadcrumb());
    }


    @Override
    public String display() {
        return "/panel/homePanel.xhtml";
    }
}
