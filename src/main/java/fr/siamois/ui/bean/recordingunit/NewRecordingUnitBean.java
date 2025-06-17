package fr.siamois.ui.bean.recordingunit;

import fr.siamois.domain.models.form.customform.CustomFormPanel;
import fr.siamois.domain.models.form.customformresponse.CustomFormResponse;
import fr.siamois.domain.models.institution.Institution;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.faces.bean.SessionScoped;
import java.util.ArrayList;


@Slf4j
@Getter
@Setter
@Component
@SessionScoped
public class NewRecordingUnitBean {

    protected CustomFormPanel form ;
    protected CustomFormResponse formResponse ;

    public void initForm() {

    }

    public void init(Institution institution) {
        this.institution = institution;
        this.refActionManagers = institutionService.findAllActionManagersOf(institution);
        this.filteredActionManagers = new ArrayList<>(refActionManagers);
    }

}
