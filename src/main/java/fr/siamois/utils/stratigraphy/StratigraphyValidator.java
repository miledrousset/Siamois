package fr.siamois.utils.stratigraphy;

import fr.siamois.models.recordingunit.RecordingUnit;

import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
public class StratigraphyValidator {

    private static SynchronousGroupBuilder synchronousGroupBuilder;

    // Stratigraphic unit list and their relationship
    private final List<RecordingUnit> recordingUnits;
    private List<SynchronousGroup> synchronousGroupList; // will contain all the units from recordingUnits but grouped

    // locales
    private List<String> collecComm = new ArrayList<>();
    private long[] enSynch; // ensemble synchrone (ES) de l'US (O si pas en synchronisme)
    private final String[] saiUstatut; // statut de l'US (Fait, MES, US simple par défaut)

    public StratigraphyValidator(List<RecordingUnit> recordingUnits) {
        this.recordingUnits = recordingUnits;
        saiUstatut = new String[recordingUnits.size()];
        Arrays.fill(saiUstatut, "US");
    }

}