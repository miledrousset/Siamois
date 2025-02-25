package fr.siamois.utils.stratigraphy.stratifiant;

import fr.siamois.models.recordingunit.RecordingUnit;
import fr.siamois.models.recordingunit.StratigraphicRelationship;
import fr.siamois.services.recordingunit.StratigraphicRelationshipService;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


import static org.junit.jupiter.api.Assertions.*;

class StratifiantTest {

    @Test
    void loadStratifiantDataFromSpreadsheet_success() {

        List<RecordingUnit> expectedUnits = new ArrayList<>();
        RecordingUnit unit1 = new RecordingUnit();
        unit1.setFullIdentifier("US1");
        unit1.setId(0L);
        expectedUnits.add(unit1);
        RecordingUnit unit2 = new RecordingUnit();
        unit2.setFullIdentifier("US2");
        unit2.setId(1L);
        expectedUnits.add(unit2);
        RecordingUnit unit3 = new RecordingUnit();
        unit3.setFullIdentifier("US3");
        unit3.setId(2L);
        expectedUnits.add(unit3);
        RecordingUnit unit4 = new RecordingUnit();
        unit4.setFullIdentifier("fait1");
        expectedUnits.add(unit4);
        unit4.setId(3L);

        // add rels
        StratigraphicRelationship rel1 = new StratigraphicRelationship();
        rel1.setUnit1(unit1);
        rel1.setUnit2(unit3);
        rel1.setType(StratigraphicRelationshipService.SYNCHRONOUS);
        unit1.getRelationshipsAsUnit1().add(rel1);
        StratigraphicRelationship rel2 = new StratigraphicRelationship();
        rel2.setUnit1(unit3);
        rel2.setUnit2(unit4);
        rel2.setType(StratigraphicRelationshipService.SYNCHRONOUS);
        unit3.getRelationshipsAsUnit1().add(rel2);

        // Load data from M03001
        assertDoesNotThrow(() -> {
            List<RecordingUnit> units = Stratifiant.loadStratifiantDataFromSpreadsheet("src/test/resources/xlsx/stratifiant/M03001.xlsx");
            // Assert
            assertEquals(4, units.size());
            assertEquals( expectedUnits.stream().map(RecordingUnit::getId).collect(Collectors.toList()),
                     units.stream().map(RecordingUnit::getId).collect(Collectors.toList())
            );
            assertEquals( expectedUnits.stream().map(RecordingUnit::getFullIdentifier).collect(Collectors.toList()),
                    units.stream().map(RecordingUnit::getFullIdentifier).collect(Collectors.toList())
            );
            assertEquals( expectedUnits.stream().map(RecordingUnit::getRelationshipsAsUnit1).collect(Collectors.toList()),
                    units.stream().map(RecordingUnit::getRelationshipsAsUnit1).collect(Collectors.toList())
            );
        });

    }
}