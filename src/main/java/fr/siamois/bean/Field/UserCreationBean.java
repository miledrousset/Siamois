package fr.siamois.bean.Field;

import fr.siamois.models.ActionUnit;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Slf4j
@Component
public class UserCreationBean implements Serializable {

    // Injections

    // Storage
    List<ActionUnit> refActionUnits = new ArrayList<>();
    List<ActionUnit> filteredActionUnits = new ArrayList<>();

    // Fields
    private String vUsername;
    private String vPassword;
    private String vEmail;
    private String vConfirmPassword;
    private List<ActionUnit> vActionUnits = new ArrayList<>();

    public void createUser() {
        // TODO: Implement this method
    }

}
