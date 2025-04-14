package fr.siamois.ui.bean.settings;

import fr.siamois.domain.models.Institution;
import fr.siamois.ui.bean.settings.components.OptionElement;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.faces.bean.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Getter
@Setter
@Component
@SessionScoped
public class InstitutionDetailsBean implements Serializable {

    private Institution institution;
    private transient List<OptionElement> elements;

    public void addInstitutionManagementElements() {
        elements = new ArrayList<>();
        elements.add(new OptionElement("bi bi-building", "Paramètres de l'organisation", "Nom de l'organisation", () -> {
            log.trace("Clicked on institution settings");
            return "";
        }));
        elements.add(new OptionElement("bi bi-person-circle", "Responsables d'organisation", "Gérer les administrateurs de Bibracte", () -> {
            log.trace("Clicked on institution managers");
            return "";
        }));
        elements.add(new OptionElement("bi bi-people", "Équipes", "Gérer les équipes de Bibracte", () -> {
            log.trace("Clicked on institution teams");
            return "";
        }));
        elements.add(new OptionElement("bi bi-table", "Thesaurus","Configurer le thesaurus utilisé par l'organisation", () -> {
            log.trace("Clicked on thesaurus settings");
            return "";
        }));
    }

    public String goToInstitutionList() {
        institution = null;
        elements.clear();
        return "/pages/settings/institutionListSettings.xhtml?faces-redirect=true";
    }

}
