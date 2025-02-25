package fr.siamois.utils.stratigraphy;

import fr.siamois.models.recordingunit.RecordingUnit;
import fr.siamois.models.recordingunit.StratigraphicRelationship;
import fr.siamois.services.recordingunit.StratigraphicRelationshipService;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SynchronousGroupBuilderTest {

    @Test
    void testSynchronousGroupsFormation() {
        // Create Recording Units
        RecordingUnit unitA = new RecordingUnit(); unitA.setFullIdentifier("A"); unitA.setId(1L);
        unitA.setRelationshipsAsUnit1(new HashSet<>()); unitA.setRelationshipsAsUnit2(new HashSet<>());
        RecordingUnit unitB = new RecordingUnit(); unitB.setFullIdentifier("B"); unitB.setId(2L);
        unitB.setRelationshipsAsUnit1(new HashSet<>()); unitB.setRelationshipsAsUnit2(new HashSet<>());
        RecordingUnit unitC = new RecordingUnit(); unitC.setFullIdentifier("C"); unitC.setId(3L);
        unitC.setRelationshipsAsUnit1(new HashSet<>()); unitC.setRelationshipsAsUnit2(new HashSet<>());
        RecordingUnit unitD = new RecordingUnit(); unitD.setFullIdentifier("D"); unitD.setId(4L);
        unitD.setRelationshipsAsUnit1(new HashSet<>()); unitD.setRelationshipsAsUnit2(new HashSet<>());

        // Create Relationships (A ↔ B, A ↔ C)
        createSynchronousRelationship(unitA, unitB);
        createSynchronousRelationship(unitA, unitC);

        // Initialize the builder
        List<RecordingUnit> units = List.of(unitA, unitB, unitC, unitD);
        SynchronousGroupBuilder builder = new SynchronousGroupBuilder(units);

        // Build synchronous groups
        List<SynchronousGroup> groups = builder.build();

        // Assertions
        assertEquals(2, groups.size(), "There should be 2 synchronous groups");

        // Find the group with multiple members
        SynchronousGroup multiUnitGroup = groups.stream()
                .filter(group -> group.getUnits().size() > 1)
                .findFirst()
                .orElseThrow(() -> new AssertionError("Expected a multi-unit synchronous group"));

        SynchronousGroup singleUnitGroup = groups.stream()
                .filter(group -> group.getUnits().size() == 1)
                .findFirst()
                .orElseThrow(() -> new AssertionError("Expected a single-unit synchronous group"));

        // Validate multi-unit group
        assertTrue(multiUnitGroup.getUnits().containsAll(List.of(unitA, unitB, unitC)),
                "Multi-unit group should contain A, B, and C");

        // Validate single-unit group
        assertEquals(1, singleUnitGroup.getUnits().size(), "Single-unit group should have exactly 1 unit");
        assertTrue(singleUnitGroup.getUnits().contains(unitD), "Single-unit group should contain unit D");
    }

    private StratigraphicRelationship createSynchronousRelationship(RecordingUnit unit1, RecordingUnit unit2) {
        StratigraphicRelationship relationship = new StratigraphicRelationship();
        relationship.setUnit1(unit1);
        relationship.setUnit2(unit2);
        relationship.setType(StratigraphicRelationshipService.SYNCHRONOUS);
        unit1.getRelationshipsAsUnit1().add(relationship);
        return relationship;
    }
}
