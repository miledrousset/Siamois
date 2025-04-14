package fr.siamois.ui.bean.settings;

import fr.siamois.domain.models.Institution;
import fr.siamois.ui.bean.settings.components.OptionElement;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import javax.faces.bean.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Component
@SessionScoped
public class InstitutionDetailsBean implements Serializable {

    private Institution institution;
    private transient List<OptionElement> elements;

    public void addInstitutionManagementElements() {
        elements = new ArrayList<>();
        elements.add(new OptionElement("bi bi-building", "Paramètres de l'organisation", "Nom de l'organisation", () -> redirectTo("")));
        elements.add(new OptionElement("bi bi-person-circle", "Responsables d'organisation", "Gérer les administrateurs de Bibracte", () -> redirectTo("")));
        elements.add(new OptionElement("bi bi-people", "Équipes", "Gérer les équipes de Bibracte", () -> redirectTo("")));
        elements.add(new OptionElement("bi bi-table", "Thesaurus","Configurer le thesaurus utilisé par l'organisation", () -> redirectTo("")));
    }

    public String redirectTo(String pagePath) {
        return pagePath;
    }

}
